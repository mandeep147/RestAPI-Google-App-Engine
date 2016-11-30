package edu.mandeep.cmpe281.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlRootElement(name = "Employee")
@XmlType(propOrder = {"id", "firstName", "lastName"})
@JsonPropertyOrder({"id", "firstName", "lastName"})
public class Employee {
	private String firstName;
	private String lastName;
	private int id; 
	
	public Employee() {
		id = 0;
		firstName = null;
		lastName = null;
	}
	
	public Employee(int id, String fname, String lname) {
		this.id = id;
		firstName = fname;
		lastName = lname;
	}

	@XmlElement
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement
	public String getLastName() {
		return lastName;
	}

	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
