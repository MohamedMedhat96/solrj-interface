package esrinea.gss.solrAssignment.exceptions;

public class DocumentNotFoundException extends RuntimeException {

	public DocumentNotFoundException(String message, Exception e)
	{
		super(message,e);
	}
}
