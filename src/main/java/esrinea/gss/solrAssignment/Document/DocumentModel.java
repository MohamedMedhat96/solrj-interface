package esrinea.gss.solrAssignment.Document;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

@Entity
public class DocumentModel<T> {

	
	private String id;	
	private Map<String, Object> fields;
	public Map<String, Object> getFields() {
		return fields;
	}
	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	
}
