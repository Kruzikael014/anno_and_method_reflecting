package app.server.router;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import app.exception.NotYetClassifiedException;
import app.others.annotation.Body;
import app.others.annotation.Controller;
import app.others.annotation.PathParam;
import app.others.annotation.QueryParam;
import app.others.annotation.Route;
import app.others.annotation.Routes;
import app.others.enumeration.HttpMethod;

public class Router implements HttpHandler {

	private static class RouteDefinition {
		Pattern pattern;
		Method method;
		Object controllerInstance;
		List<String> paramNames;
		HttpMethod httpMethod;

		public RouteDefinition(Pattern pattern, Method method, Object controllerInstance, List<String> paramNames,
				HttpMethod httpMethod) {
			this.pattern = pattern;
			this.method = method;
			this.controllerInstance = controllerInstance;
			this.paramNames = paramNames;
			this.httpMethod = httpMethod;
		}

	}

	// All the defined route (method in controller)
	private final List<Router.RouteDefinition> routes = new Vector<Router.RouteDefinition>();

	public void registerController(Class<?> controllerClass) {
		try {
			// get the instance from the passed class
			Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

			// class used to contain a prefix or just a '/', here we obtain the prefix or
			// the '/'
			String prefix = "";
			if (controllerClass.isAnnotationPresent(Controller.class)) {
				prefix = controllerClass.getAnnotation(Controller.class).path();
			} else {
				throw new NotYetClassifiedException();
			}

			// iterate methods in controller class
			for (Method method : controllerClass.getDeclaredMethods()) {
				if (method.isAnnotationPresent(Route.class) || method.isAnnotationPresent(Routes.class)) {
					// Route is repeatable, so we need to capture all route defined above a method
					Route[] routeAnnotations = method.getAnnotationsByType(Route.class);

					// iterate through all the annotations (if more than 1, if not then it will only
					// be executed once)
					for (Route route : routeAnnotations) {
						String rawPath = (prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix)
								+ (route.path().isEmpty() ? ""
										: (route.path().startsWith("/") ? route.path() : "/" + route.path()));

						List<String> paramNames = new ArrayList<String>();
						String regexPath = Arrays.stream(rawPath.split("/")).map(pathParam -> {
							// we only consider things wrapped with { } as path param, other than that, then
							// it's just part of the path
							if (pathParam.startsWith("{") && pathParam.endsWith("}")) {
								String name = pathParam.substring(1, pathParam.length() - 1);
								paramNames.add(name);
								return "([^/]+)";
							}
							return pathParam;
						}).reduce((a, b) -> a + "/" + b).orElse("");

						Pattern pattern = Pattern.compile("^" + regexPath + "$");

						RouteDefinition routeDefinition = new RouteDefinition(pattern, method, controllerInstance,
								paramNames, route.method());

						routes.add(routeDefinition);
					}

				}
				// else it doesnt meant to be a route, maybe just a regular function
			}

		} catch (Exception e) {
			throw new RuntimeException("Failed to register controller: " + controllerClass, e);
		}
	}

	private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
		
		byte[] bytes = response.getBytes();
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(statusCode, bytes.length);
		try (var os = exchange.getResponseBody()) {
			os.write(bytes);
		}
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestPath = exchange.getRequestURI().getPath();
		HttpMethod requestMethod;

		try {
			requestMethod = HttpMethod.valueOf(exchange.getRequestMethod().toUpperCase());
		} catch (Exception e) {
			sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
			return;
		}

		String response = null;
		int statusCode = 0;

		try {
			RouteDefinition matched = null;
			Matcher matcher = null;

			// search matching routes
			for (RouteDefinition rd : routes) {
				if (rd.httpMethod.equals(requestMethod)) {
					matcher = rd.pattern.matcher(requestPath);
					if (matcher.matches()) {
						matched = rd;
						break;
					}
				}
			}

			if (matched == null) {
				sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
				return;
			}

			Method method = matched.method;
			var parameters = method.getParameters();
			Object[] args = new Object[parameters.length];

			// get query string if exists
			String query = exchange.getRequestURI().getQuery();
			Map<String, String> queryParams = new HashMap<>();
			if (query != null) {
				for (String pair : query.split("&")) {
					String[] kv = pair.split("=", 2);
					queryParams.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
							kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
				}
			}

			String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

			// inject every param
			for (int i = 0; i < parameters.length; i++) {
				var param = parameters[i];
				if (param.isAnnotationPresent(PathParam.class)) {
					String name = param.getAnnotation(PathParam.class).value();
					int index = matched.paramNames.indexOf(name);
					args[i] = matcher.group(index + 1); // path param groups start at 1
				} else if (param.isAnnotationPresent(QueryParam.class)) {
					String name = param.getAnnotation(QueryParam.class).value();
					args[i] = queryParams.get(name);
				} else if (param.isAnnotationPresent(Body.class)) {
					args[i] = body;
				} else {
					args[i] = null; // fallback if no annotation
				}
			}

			try {
				Object result = method.invoke(matched.controllerInstance, args);

				response = (result != null) ? result.toString() : "";
				statusCode = 200;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = "{\"error\":\"Internal Server Error\"}";
			statusCode = 500;
		}
		sendResponse(exchange, statusCode, response);
	}

}
