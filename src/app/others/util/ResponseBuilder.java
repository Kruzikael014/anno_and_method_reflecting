package app.others.util;

import app.model.Response;
import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

public class ResponseBuilder {

	private Response response;
	private HttpExchange exchange;

	public void reset() {
		this.response = new Response();
	}

	public ResponseBuilder(HttpExchange exchange) {
		reset();
		this.exchange = exchange;
	}

	public ResponseBuilder setStatusCode(int statusCode) {
		response.setStatusCode(statusCode);
		return this;
	}

	public ResponseBuilder setBody(String body) {
		response.setBody(body);
		return this;
	}

	public void send() throws IOException {
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

		byte[] bytes = response.getBody().getBytes();
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(response.getStatusCode(), bytes.length);
		try (var os = exchange.getResponseBody()) {
			os.write(bytes);
		}
		reset();
	}

}
