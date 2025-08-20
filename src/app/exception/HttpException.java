package app.exception;

public class HttpException extends RuntimeException {
	private int statusCode;

	public HttpException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public final int getStatusCode() {
		return statusCode;
	}

	public final void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
