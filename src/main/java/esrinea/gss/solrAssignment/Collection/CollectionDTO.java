package esrinea.gss.solrAssignment.Collection;

import javax.persistence.Entity;

@Entity
public class CollectionDTO {

	private String name;
	private String query;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
}
