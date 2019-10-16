package esrinea.gss.solrAssignment.Collection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.validation.constraints.DecimalMin;

import org.apache.solr.common.SolrInputField;
import org.springframework.validation.annotation.Validated;

import esrinea.gss.solrAssignment.Document.DocumentModel;
import esrinea.gss.solrAssignment.Field.FieldModel;

@Entity
public class CollectionModel {

	private String name;
	
	private int replicas;
	private List<FieldModel> fields;
	private int shards;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getShards() {
		return shards;
	}

	public void setShards(int shards) {
		this.shards = shards;
	}

	public int getReplicas() {
		return replicas;
	}

	public void setReplicas(int replicas) {
		this.replicas = replicas;
	}

	public List<FieldModel> getFields() {
		return fields;
	}

	public void setFields(List<FieldModel> fields) {
		this.fields = fields;
	}

	

}
