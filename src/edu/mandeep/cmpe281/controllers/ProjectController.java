package edu.mandeep.cmpe281.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import edu.mandeep.cmpe281.beans.Project;
import edu.mandeep.cmpe281.exception.RecordNotFoundException;

@Controller
@RequestMapping(value = "cmpe281Mandeep969/rest/project")
public class ProjectController {

	/**
	 * to retrieve an Project detail with given id
	 * @param id
	 * @return JSON of the specified Project
	 */
	@RequestMapping(value = "json/{id}", method = RequestMethod.GET,produces={"application/json"})
	public ResponseEntity<Project> getProject(@PathVariable("id") int id){

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try{
			System.out.println("Retrieving Project details with ID" +id);

			Entity ProjectDatastoreEntity = datastore.get(KeyFactory.createKey("Project", id));

			int empId = (int) ProjectDatastoreEntity.getKey().getId();
			String name = (String) ProjectDatastoreEntity.getProperty("name");
			double budget = (double) ProjectDatastoreEntity.getProperty("budget");
			Project emp = new Project(empId, name, budget);
			
			return new ResponseEntity<Project>( emp, HttpStatus.OK);

		}catch(EntityNotFoundException e){

			System.out.println("Project ID: " +id+ "not found " +e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		}		
	}
	
	@RequestMapping(value = "xml/{id}", method = RequestMethod.GET,produces={"application/xml"})
	public ResponseEntity<Project> getXMLProject(@PathVariable("id") int id){

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try{
			System.out.println("Retrieving Project details with ID" +id);

			Entity ProjectDatastoreEntity = datastore.get(KeyFactory.createKey("Project", id));

			int empId = (int) ProjectDatastoreEntity.getKey().getId();
			String name = (String) ProjectDatastoreEntity.getProperty("name");
			double budget = (double) ProjectDatastoreEntity.getProperty("budget");
			Project emp = new Project(empId, name, budget);
			
			return new ResponseEntity<Project>( emp, HttpStatus.OK);

		}catch(EntityNotFoundException e){

			System.out.println("Project ID: " +id+ "not found " +e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		}		
	}

	/**
	 * to create a new entry of an Project
	 * @param Project 
	 * @return  Http Status depending on the status.
	 */
	@RequestMapping(method = RequestMethod.POST, produces={"application/json", "application/xml"},
	        consumes={"application/json", "application/xml"})
	public ResponseEntity<Void> createProject(@RequestBody Project emp, UriComponentsBuilder ucb){

		System.out.println("Creating Project with ID: "+emp.getId());

		if( isProjectExist( emp.getId() ) ){
			System.out.println("Project with ID: " +emp.getId()+ " already exists in datastore");
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ProjectDatastoreEntity = new Entity("Project", emp.getId());

		ProjectDatastoreEntity.setProperty("name", emp.getName());
		ProjectDatastoreEntity.setProperty("budget", emp.getBudget());
		datastore.put(ProjectDatastoreEntity);

		UriComponents  uric = ucb.path("cmpe281Mandeep969/rest/project/{id}").buildAndExpand(emp.getId());

		HttpHeaders header = new HttpHeaders();
		header.setLocation(uric.toUri());

		System.out.println("Project with ID: "+ emp.getId() + " created");
		return new ResponseEntity<Void>(header,HttpStatus.CREATED);
	}

	/**
	 *  to delete the record of an Project having the specified id
	 * @param id
	 * @return status whether Project is deleted or not from datastore
	 */

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteProject(@PathVariable("id") int id){

		System.out.println("Deleting Project record with ID: " +id);

		if(!isProjectExist(id)){
			System.out.println("Project with ID : "+id+" not found in datastore");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.delete(KeyFactory.createKey("Project", id));

		System.out.println("Deleted Project with id: " +id );

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * to update the details of an Project based on the id specified
	 * @param id
	 * @return http status depending whether update was successful or not
	 */

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}",produces={"application/json", "application/xml"},
	        consumes={"application/json", "application/xml"})
	public ResponseEntity<Void> updateProject(@PathVariable("id") int id, @RequestBody Project emp){

		System.out.println("Updating Project record having ID: " +id);

		if( id != emp.getId() ){

			System.out.println("Project ID in URL and Request Body must be same");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

		}

		if( !isProjectExist( emp.getId() ) ){

			System.out.println("Project record with ID: " +emp.getId()+" doesn't exists");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ProjectEntity = new Entity("Project", emp.getId());

		String name = emp.getName();
		double budget= emp.getBudget();

		if(emp.getName() != null)
		{
			ProjectEntity.setProperty("name", name);
		}
		if (emp.getBudget() != 0)
		{
			ProjectEntity.setProperty("budget", budget);
		}
		datastore.put(ProjectEntity);

		System.out.println("Project record with ID: " +emp.getId()+ " is updated successfully");
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	/**
	 *  to retrieve a list of all Projects which exist in datastore
	 * 
	 * @return list of Project in JSON format
	 */
	@RequestMapping(value= "/json", method = RequestMethod.GET, produces={"application/json"})
	public @ResponseBody String getAllProjects(){
		
		System.out.println("Retrieving all Projects details");
		
		ArrayList<Project> Project = new ArrayList<Project>(0);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Iterable<Entity> allRows = datastore.prepare(new Query("Project")).asIterable();

		for (Entity entity : allRows) {
			Project.add(new Project((int) entity.getKey().getId(), (String) entity.getProperty("name"), (double) entity.getProperty("budget")));
		}

		if (Project.isEmpty()) {
			System.out.println("No Projects in datastore.");
			throw new RecordNotFoundException();
		}

		final OutputStream outputStream = new ByteArrayOutputStream();
		final ObjectMapper objectMapper = new ObjectMapper();

		try {
			objectMapper.writeValue(outputStream, Project);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Retrieved all records existing in datastore");
		final byte[] jsonData = ((ByteArrayOutputStream) outputStream).toByteArray();

		return new String(jsonData);

	}
	
	@RequestMapping(value = "/xml", method = RequestMethod.GET, produces={"application/xml"})
	public @ResponseBody String getAllXMLProjects() throws JAXBException, IOException, XMLStreamException{
		
		System.out.println("Retrieving all Projects details");	
		ArrayList<Project> Project = new ArrayList<Project>(0);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Iterable<Entity> allRows = datastore.prepare(new Query("Project")).asIterable();

		for (Entity entity : allRows) {
			Project.add(new Project((int) entity.getKey().getId(), (String) entity.getProperty("name"), (double) entity.getProperty("budget")));	
			
		}

		if (Project.isEmpty()) {
			System.out.println("No Projects in datastore.");
			throw new RecordNotFoundException();
		}
		
		String xmlString = "";
		    
		    	for(Project emp: Project){
		    		try { 
		    			JAXBContext context = JAXBContext.newInstance(Project.class);
		    			Marshaller m = context.createMarshaller();
		    			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
		    			StringWriter sw = new StringWriter();
		    			m.marshal(emp, sw);
		    			xmlString += sw.toString();
		    		} catch (JAXBException e) {
		    			e.printStackTrace();
		    		}
		    	}		    			       
		    return xmlString;
	}

	/**
	 * checks whether an entry of Project exists in datastore or not 
	 * @param id
	 * @return status if Project record exist
	 */
	@SuppressWarnings("finally")
	private boolean isProjectExist(int id){

		boolean exist = false;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try{
			datastore.get(KeyFactory.createKey("Project", id));
			exist = true;
		}catch(EntityNotFoundException e){
			exist = false;
		}finally{
			return exist;
		}
	}
}