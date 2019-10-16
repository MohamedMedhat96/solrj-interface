package esrinea.gss.solrAssignment.solr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

//Generic Error Response Message

@JsonInclude(value = Include.NON_NULL)
public class Response<T> {

	private T data;
	private String message; // Error Message loaded from the htmlmessages.properties which is in the
							// resources
	private int code;      //code for the error such as 404,401,etc.

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
