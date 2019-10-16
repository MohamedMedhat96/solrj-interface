package esrinea.gss.solrAssignment.exceptions;

public class CustomServerException extends RuntimeException {

	public CustomServerException(String message, Exception e) {
			super(message,e);
	}
}
