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
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
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

/**
 * Creating a SolrCloud connection instance.
 * @return CloudSolrClient an instance of the SolrCloud connection.
 */
	public CloudSolrClient createConnection() {
		Collection<String> solrNodes = new ArrayList<>();
		solrNodes.add("http://localhost:8983/solr");
		if (solrClient == null) {
			solrClient = new CloudSolrClient.Builder().withSolrUrl(solrNodes).build();
		}
		return solrClient;

	}
	
	/**
	 * Returning all documents in a collection based on a specific Query.
	 * @param solrQuery The query you search SolrCloud with.
	 * @param collection The collection you wish to invoke the query on.
	 * @param solrClient The SolrCloud connections instance.
	 * @return SolrDocumentList the list of documents that fit the query
	 */

	public SolrDocumentList getSolrResponse(SolrQuery solrQuery, String collection, CloudSolrClient solrClient) {
		QueryResponse response = null;
		SolrDocumentList list = null;
		
			QueryRequest req = new QueryRequest(solrQuery);
			
			solrClient.setDefaultCollection(collection);
			try {
				response = req.process(solrClient);
			} catch (SolrServerException e) {
				
			} catch (IOException e) {
				
				}
			catch(RemoteSolrException e)
			{
				throw new IncorrectInputException("The field is undefined: "+(solrQuery.getQuery().split(":"))[0], e);
			}
			catch(SolrException e)
			{
				throw new CollectionNotFoundException(e.getMessage(),e);
			}
			
			list = response.getResults();
		
		return list;
	}
	
	/**
	 * Helper method for getting documents in a collections based on a specific query 
	 * 
	 * @param CollectionDTO a collection Data Transfer Object which contains the collection name and the Query
	 * @return Response with the list of documents, success/failure message and success/failure code.
	 */

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
} else {
			
			response.setData( new ArrayList());
		}

			response.setMessage("Query Executed");
			response.setCode(200);
				System.out.println(query.toString());
		return response;
	}

	
	/**
	 * Adding a collection to SolrCloud
	 * @param collection that you wish to add with the name, number of shards and number of replicas.
	 * @return Response with the success code and success message.
	 * @throws CustomServerException 
	 */
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
			} catch (SolrServerException e) {
				throw new CustomServerException(e.getMessage(),e);
			} catch (IOException e) {
				throw new CustomServerException(e.getMessage(),e);
			}catch(RemoteSolrException e) {
				throw new IncorrectInputException("The collection you entered already exists", e);
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
	/**
	 * Adding a document to a collection
	 * @param document the document you wish to add.
	 * @param collectionName the collection name you wish to add the document to.
	 * @return Response Success message and the failure message.
	 * @throws CustomServerException
	 */

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
	
	/**
	 * Removing a collection from SolrCloud given the collection name.
	 * @param collection the name of the collection that you want to be removed
	 * @return response returning the success/failure code and success/failure message.
	 * @throws CustomServerException
	 */

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
	/**
	 * Removing a document from a collection.
	 * @param document The document ID you wish to remove
	 * @param collectionName The collection name you wish to remove the document from.
	 * @return response with the success/failure code and the success/failure message.
	 * @throws Exception
	 */
	public Response removeDocument(DocumentModel json, String collectionName)
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
