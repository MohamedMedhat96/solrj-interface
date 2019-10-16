package esrinea.gss.solrAssignment.solr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import esrinea.gss.solrAssignment.Collection.CollectionDTO;
import esrinea.gss.solrAssignment.Collection.CollectionModel;
import esrinea.gss.solrAssignment.Document.DocumentModel;

@RestController
public class SolController {
	
	@Autowired
	SolrService service;
	
	@GetMapping("/collections")
	public Response searchByQuery(@RequestBody CollectionDTO json) {
		
		return service.search(json);
		}
	
	@PostMapping("/collections")
	public Response postACollection(@RequestBody CollectionModel json) throws Throwable
	{
		return service.addCollection(json);
	}
	
	@DeleteMapping("/collections")
	public Response deleteACollection(@RequestBody CollectionModel json) {
		return service.removeCollection(json);
	}
	@PostMapping("/collections/{name}")
	public Response addDocument(@RequestBody DocumentModel json,@PathVariable String name)
	{
		return service.addDocument(json, name);
	}
	@DeleteMapping("/collections/{name}")
	public Response deleteDocument(@RequestBody DocumentModel json, @PathVariable String name)
	{
		return service.removeDocument(json, name);
	}
	
}
