package colector.co.com.collector.model.response;

public class ErrorResponse {

	private int code;
	private String message;
	private String data;
	
	public ErrorResponse(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public ErrorResponse(int code, String message, String data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getData() {
		return data;
	}

	@Override
	public String toString() {
		return "ErrorResponse [code=" + code + ", message=" + message + "]";
	}
	
	
}
