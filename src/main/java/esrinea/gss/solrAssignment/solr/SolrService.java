package esrinea.gss.solrAssignment.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import esrinea.gss.solrAssignment.Collection.CollectionDTO;
import esrinea.gss.solrAssignment.Collection.CollectionModel;
import esrinea.gss.solrAssignment.Document.DocumentModel;
import esrinea.gss.solrAssignment.Field.FieldModel;
import esrinea.gss.solrAssignment.exceptions.CollectionNotFoundException;
import esrinea.gss.solrAssignment.exceptions.CustomServerException;
import esrinea.gss.solrAssignment.exceptions.DocumentNotFoundException;
import esrinea.gss.solrAssignment.exceptions.IncorrectInputException;

@Service
public class SolrService {
	CloudSolrClient solrClient;

	public CloudSolrClient createConnection() {
		Collection<String> solrNodes = new ArrayList<>();
		solrNodes.add("http://localhost:8983/solr");
		if (solrClient == null) {
			solrClient = new CloudSolrClient.Builder().withSolrUrl(solrNodes).build();
		}
		return solrClient;

	}

	public SolrDocumentList getSolrResponse(SolrQuery solrQuery, String collection, CloudSolrClient solrClient) {
		QueryResponse response = null;
		SolrDocumentList list = null;
		
		try {
			QueryRequest req = new QueryRequest(solrQuery);
			
			solrClient.setDefaultCollection(collection);
			response = req.process(solrClient);
			
			list = response.getResults();
		} catch (Exception e) {
			e.printStackTrace();// handle errors in this block
		}
		return list;
	}

	public Response search(CollectionDTO collection) {
		if(collection.getQuery() == null ||	 collection.getQuery().equals(""))
		{
			throw new IncorrectInputException("Query cannot be empty", new Exception());
				}
		if(collection.getName() == null || collection.getName().equals(""))
		{
			throw new IncorrectInputException("Collection name cannot be empty", new Exception());
		}
		CloudSolrClient solrClient = this.createConnection();
		String query = collection.getQuery();
		String collectionName = collection.getName();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		solrQuery.setRows(50);
		solrQuery.set("collection", collectionName);
		solrQuery.set("wt", "json");
		SolrDocumentList documentList = this.getSolrResponse(solrQuery, collectionName, solrClient);
		Response response = new Response();
		if (documentList != null && documentList.size() > 0) {
			response.setData(documentList);

			response.setMessage("Query Executed");
			response.setCode(200);
		} else {
			response.setMessage("Failure");

		}
		System.out.println(query.toString());
		return response;
	}

	public Response addCollection(CollectionModel collection) throws CustomServerException {
		
		if(collection.getShards() <= 0)
			throw new IncorrectInputException("Shards need to be at least one", new Exception());
		if(collection.getReplicas() <= 0)
			throw new IncorrectInputException("Replicas need to be at least one", new Exception());
		if(collection.getName() == null || collection.getName().equals(""))
		{
			throw new IncorrectInputException("Collection name cannot be empty", new Exception());
		}
		
		CloudSolrClient solrClient = this.createConnection();
		String collectionName = collection.getName();
		int shards = collection.getShards();
		int replicas = collection.getReplicas();
		CollectionAdminRequest.Create create = CollectionAdminRequest.createCollection(collectionName, shards,
				replicas);

		try {
			create.process(solrClient);

		} catch (Exception e) {
			throw new CustomServerException(e.getMessage(), e);
		}
		// rClient.commit(collectionName);

		List<FieldModel> fields = collection.getFields();
		try {
		for (FieldModel field : fields) {
			Map<String, Object> fieldAttributes = new HashMap<String, Object>();
			if(field.getName()!=null)
			fieldAttributes.put("name", field.getName());
			else
				throw new IncorrectInputException("Field name cannot be empty", new Exception());
			if(field.getType()!=null)
			fieldAttributes.put("type", field.getType());
			else
				throw new IncorrectInputException("Field type cannot be empty", new Exception());
			if(field.getIndexed() != null)
			fieldAttributes.put("indexed", field.getIndexed());
			if(field.getMultiValued()!=null)
			fieldAttributes.put("multiValued", field.getMultiValued());
			
			SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fieldAttributes);
				SchemaResponse.UpdateResponse response = schemaRequest.process(solrClient, collectionName);
		}
		}
		catch (SolrServerException e) {
			this.removeCollection(collection);
			throw new CustomServerException(e.getMessage(), e);
		} catch (IOException e) {
			this.removeCollection(collection);
			throw new CustomServerException(e.getMessage(), e);
			
		}catch(IncorrectInputException e)
		{
			this.removeCollection(collection);
			throw e;
			
		}

		Response x = new Response();
		// x.setData(fieldAttributes.get("name"));
		x.setMessage("Passed");
		return x;
	}

	public Response addDocument(DocumentModel json,String collectionName) throws CustomServerException {
		CloudSolrClient solrClient = this.createConnection();
	
		if(collectionName == null || collectionName.isEmpty())
			throw new IncorrectInputException("Collection name cannot be empty", new Exception());
		
		if(json.getFields().size() == 0)
			throw new IncorrectInputException("Document Fields cannot be empty", new Exception());

		SolrInputDocument doc = new SolrInputDocument();
		Map<String,Object> map = json.getFields();
		  for (Entry<String, Object> entry : map.entrySet())  {
	             doc.addField(entry.getKey(),entry.getValue());
	    } 
		try {
			solrClient.setDefaultCollection(collectionName);
			solrClient.add(collectionName, doc);
			solrClient.commit(collectionName);
		} catch (SolrServerException | IOException e) {
			throw new CustomServerException(e.getMessage(), e);
		}catch(SolrException e)
		{
			throw new CollectionNotFoundException(e.getMessage(),e);
		}
		
		
		Response response = new Response();
		response.setMessage("Document Added");
		response.setCode(200);
		return response;
		
		
	}

	public Response removeCollection(CollectionModel json) throws CustomServerException {

		CloudSolrClient solrClient = this.createConnection();
		String collection = json.getName();
		
		try {
			CollectionAdminRequest.Delete d = CollectionAdminRequest.deleteCollection(collection);
			d.process(solrClient);
		} catch (SolrServerException | IOException e) {
			throw new CustomServerException(e.getMessage(), e);
		}catch(SolrException e)
		{
			throw new CollectionNotFoundException(e.getMessage(),e);
		}

		Response response = new Response();
		response.setMessage("Collection Removed");
		response.setCode(200);
		return response;

	}
	
	public Response removeDocument(DocumentModel json, String collectionName) throws Exception
	{
		CloudSolrClient solrClient = this.createConnection();
		
		String documentId = json.getId();
		try {
			solrClient.setDefaultCollection(collectionName);
			Object document = solrClient.getById(documentId);
			if(document == null)
				throw new DocumentNotFoundException("The document was not found", new Exception());
			
			solrClient.deleteById(collectionName, documentId);
			solrClient.commit();
		} catch (SolrServerException | IOException e) {
			throw new CustomServerException(e.getMessage(), e);
		}
		catch(SolrException e)
		{
			throw new CollectionNotFoundException(e.getMessage(),e);
		}catch(DocumentNotFoundException e)
		{
			throw e;
		}
		Response response = new Response();
		response.setMessage("Document Removed");
		response.setCode(200);
		return response;
		
	}
	
}
