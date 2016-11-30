package edu.mandeep.cmpe281.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No Record Found")  
public class RecordNotFoundException extends RuntimeException {

}