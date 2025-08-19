package app.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import app.server.controller.RoleController;
import app.server.router.Router;

public class Server {
	private HttpServer server;
	private Router router;

	private int port;

	public Server(int port) {
		this.port = port;
		try {
			router = new Router();

			router.registerController(RoleController.class);

			server = HttpServer.create(new InetSocketAddress(8080), 0);
			server.createContext("/", router);
			server.setExecutor(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		this.server.start();
		System.out.println("Server started at http://localhost:" + port);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			stop();
		}));
	}

	public void stop() {
		this.server.stop(0);
		System.out.println("Server stopped");
	}
}
