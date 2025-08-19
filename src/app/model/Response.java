package app.model;

public class Response {
	private int statusCode;
	private String body;

	public Response(int statusCode, String body) {
		super();
		this.statusCode = statusCode;
		this.body = body;
	}

	public final int getStatusCode() {
		return statusCode;
	}

	public final void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public final String getBody() {
		return body;
	}

	public final void setBody(String body) {
		this.body = body;
	}

	public Response() {
		super();
	}
}
