package esrinea.gss.solrAssignment.Field;

import javax.persistence.Entity;

@Entity
public class FieldModel {

	private Object name;
	private Object type;
	private Object stored;
	private Object indexed;
	private Object requried;
	private Object multiValued;
	public Object getName() {
		return name;
	}
	public void setName(Object name) {
		this.name = name;
	}
	public Object getType() {
		return type;
	}
	public void setType(Object type) {
		this.type = type;
	}
	public Object getStored() {
		return stored;
	}
	public void setStored(Object stored) {
		this.stored = stored;
	}
	public Object getIndexed() {
		return indexed;
	}
	public void setIndexed(Object indexed) {
		this.indexed = indexed;
	}
	public Object getRequried() {
		return requried;
	}
	public void setRequried(Object requried) {
		this.requried = requried;
	}
	public Object getMultiValued() {
		return multiValued;
	}
	public void setMultiValued(Object multiValued) {
		this.multiValued = multiValued;
	}
	
}
