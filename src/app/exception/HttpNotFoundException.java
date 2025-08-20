package app.exception;

public class HttpNotFoundException extends HttpException {

	public HttpNotFoundException() {
		super("We cannot find the resource you're looking for", 404);
	}

}
