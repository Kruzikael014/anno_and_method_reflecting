package app.exception;

public class HttpForbiddenException extends HttpException {

	public HttpForbiddenException() {
		super("You are not allowed to access this resource", 403);
	}

}
