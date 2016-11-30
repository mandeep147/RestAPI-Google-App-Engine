package edu.mandeep.cmpe281.beans;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlRootElement(name = "Project")
@XmlType(propOrder = {"id", "name", "budget"})
@JsonPropertyOrder({"id", "name", "budget"})
public class Project {

	private int id;
	private String name;
	private double budget;

	public Project(){
		id = 0;
		name = null;
		budget = 0;
	}
	
	public Project(int id, String name, double budget) {
		super();
		this.id = id;
		this.name = name;
		this.budget = budget;
	}
	
	@XmlElement
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@XmlElement
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public double getBudget() {
		return budget;
	}
	
	public void setBudget(double budget) {
		this.budget = budget;
	}
}