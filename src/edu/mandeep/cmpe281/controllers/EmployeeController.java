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

import edu.mandeep.cmpe281.beans.Employee;
import edu.mandeep.cmpe281.exception.RecordNotFoundException;

@Controller
@RequestMapping(value = "cmpe281Mandeep969/rest/employee")
public class EmployeeController {

	/**
	 * to retrieve an employee detail with given id
	 * @param id
	 * @return JSON of the specified employee
	 */
	@RequestMapping(value = "json/{id}", method = RequestMethod.GET,produces={"application/json"})
	public ResponseEntity<Employee> getEmployee(@PathVariable("id") int id){

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try{
			System.out.println("Retrieving employee details with ID" +id);

			Entity employeeDatastoreEntity = datastore.get(KeyFactory.createKey("Employee", id));

			int empId = (int) employeeDatastoreEntity.getKey().getId();
			String firstName = (String) employeeDatastoreEntity.getProperty("firstName");
			String lastName = (String) employeeDatastoreEntity.getProperty("lastName");
			Employee emp = new Employee(empId, firstName, lastName);
			
			return new ResponseEntity<Employee>( emp, HttpStatus.OK);

		}catch(EntityNotFoundException e){

			System.out.println("Employee ID: " +id+ "not found " +e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		}		
	}
	
	@RequestMapping(value = "xml/{id}", method = RequestMethod.GET,produces={"application/xml"})
	public ResponseEntity<Employee> getXMLEmployee(@PathVariable("id") int id){

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try{
			System.out.println("Retrieving employee details with ID" +id);

			Entity employeeDatastoreEntity = datastore.get(KeyFactory.createKey("Employee", id));

			int empId = (int) employeeDatastoreEntity.getKey().getId();
			String firstName = (String) employeeDatastoreEntity.getProperty("firstName");
			String lastName = (String) employeeDatastoreEntity.getProperty("lastName");
			Employee emp = new Employee(empId, firstName, lastName);
			
			return new ResponseEntity<Employee>( emp, HttpStatus.OK);

		}catch(EntityNotFoundException e){

			System.out.println("Employee ID: " +id+ "not found " +e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		}		
	}

	/**
	 * to create a new entry of an employee
	 * @param Employee 
	 * @return  Http Status depending on the status.
	 */
	@RequestMapping(method = RequestMethod.POST, produces={"application/json", "application/xml"},
	        consumes={"application/json", "application/xml"})
	public ResponseEntity<Void> createEmployee(@RequestBody Employee emp, UriComponentsBuilder ucb){

		System.out.println("Creating employee with ID: "+emp.getId());

		if( isEmployeeExist( emp.getId() ) ){
			System.out.println("Employee with ID: " +emp.getId()+ " already exists in datastore");
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity employeeDatastoreEntity = new Entity("Employee", emp.getId());

		employeeDatastoreEntity.setProperty("firstName", emp.getFirstName());
		employeeDatastoreEntity.setProperty("lastName", emp.getLastName());
		datastore.put(employeeDatastoreEntity);

		UriComponents  uric = ucb.path("cmpe281Mandeep969/rest/employee/{id}").buildAndExpand(emp.getId());

		HttpHeaders header = new HttpHeaders();
		header.setLocation(uric.toUri());

		System.out.println("Employee with ID: "+ emp.getId() + " created");
		return new ResponseEntity<Void>(header,HttpStatus.CREATED);
	}

	/**
	 *  to delete the record of an employee having the specified id
	 * @param id
	 * @return status whether employee is deleted or not from datastore
	 */

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteEmployee(@PathVariable("id") int id){

		System.out.println("Deleting Employee record with ID: " +id);

		if(!isEmployeeExist(id)){
			System.out.println("Employee with ID : "+id+" not found in datastore");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.delete(KeyFactory.createKey("Employee", id));

		System.out.println("Deleted Employee with id: " +id );

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * to update the details of an employee based on the id specified
	 * @param id
	 * @return http status depending whether update was successful or not
	 */

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}",produces={"application/json", "application/xml"},
	        consumes={"application/json", "application/xml"})
	public ResponseEntity<Void> updateEmployee(@PathVariable("id") int id, @RequestBody Employee emp){

		System.out.println("Updating employee record having ID: " +id);

		if( id != emp.getId() ){

			System.out.println("Employee ID in URL and Request Body must be same");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

		}

		if( !isEmployeeExist( emp.getId() ) ){

			System.out.println("Employee record with ID: " +emp.getId()+" doesn't exists");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity employeeEntity = new Entity("Employee", emp.getId());

		String firstName = emp.getFirstName();
		String lastName = emp.getLastName();

		if(emp.getFirstName() != null)
		{
			employeeEntity.setProperty("firstName", firstName);
		}
		if (emp.getLastName() != null)
		{
			employeeEntity.setProperty("lastName", lastName);
		}
		datastore.put(employeeEntity);

		System.out.println("Employee record with ID: " +emp.getId()+ " is updated successfully");
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	/**
	 *  to retrieve a list of all employees which exist in datastore
	 * 
	 * @return list of employee in JSON format
	 */
	@RequestMapping(value= "/json", method = RequestMethod.GET, produces={"application/json"})
	public @ResponseBody String getAllEmployees(){
		
		System.out.println("Retrieving all employees details");
		
		ArrayList<Employee> employee = new ArrayList<Employee>(0);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Iterable<Entity> allRows = datastore.prepare(new Query("Employee")).asIterable();

		for (Entity entity : allRows) {
			employee.add(new Employee((int) entity.getKey().getId(), (String) entity.getProperty("firstName"), (String) entity.getProperty("lastName")));
		}

		if (employee.isEmpty()) {
			System.out.println("No employees in datastore.");
			throw new RecordNotFoundException();
		}

		final OutputStream outputStream = new ByteArrayOutputStream();
		final ObjectMapper objectMapper = new ObjectMapper();

		try {
			objectMapper.writeValue(outputStream, employee);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Retrieved all records existing in datastore");
		final byte[] jsonData = ((ByteArrayOutputStream) outputStream).toByteArray();

		return new String(jsonData);

	}
	
	@RequestMapping(value = "/xml", method = RequestMethod.GET, produces={"application/xml"})
	public @ResponseBody String getAllXMLEmployees() throws JAXBException, IOException, XMLStreamException{
		
		System.out.println("Retrieving all employees details");	
		ArrayList<Employee> employee = new ArrayList<Employee>(0);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Iterable<Entity> allRows = datastore.prepare(new Query("Employee")).asIterable();

		for (Entity entity : allRows) {
			employee.add(new Employee((int) entity.getKey().getId(), (String) entity.getProperty("firstName"), (String) entity.getProperty("lastName")));
		}

		if (employee.isEmpty()) {
			System.out.println("No employees in datastore.");
			throw new RecordNotFoundException();
		}
		
		String xmlString = "";
		    
		    	for(Employee emp: employee){
		    		try { 
		    			JAXBContext context = JAXBContext.newInstance(Employee.class);
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
	 * checks whether an entry of employee exists in datastore or not 
	 * @param id
	 * @return status if employee record exist
	 */
	@SuppressWarnings("finally")
	private boolean isEmployeeExist(int id){

		boolean exist = false;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try{
			datastore.get(KeyFactory.createKey("Employee", id));
			exist = true;
		}catch(EntityNotFoundException e){
			exist = false;
		}finally{
			return exist;
		}
	}
}