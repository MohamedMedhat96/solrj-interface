package esrinea.gss.solrAssignment.exceptions;

public class CollectionNotFoundException extends RuntimeException {

	public CollectionNotFoundException(String message, Exception e)
	{
		super(message,e);
	}
}
