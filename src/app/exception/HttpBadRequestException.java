package app.exception;

public class HttpBadRequestException extends HttpException {

	public HttpBadRequestException() {
		super("Please check your request body and other parameters", 400);
	}

}
