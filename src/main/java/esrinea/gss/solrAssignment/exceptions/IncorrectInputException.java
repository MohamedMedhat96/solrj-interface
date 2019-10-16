package esrinea.gss.solrAssignment.exceptions;

public class IncorrectInputException extends RuntimeException {

	public IncorrectInputException(String message, Exception e) {
		super(message, e);
	}
}
