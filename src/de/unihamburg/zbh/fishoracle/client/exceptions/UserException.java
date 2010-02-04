package de.unihamburg.zbh.fishoracle.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserException extends Exception implements IsSerializable{

	private static final long serialVersionUID = 8814997077261571336L;

	private String message;
	
	public UserException() {
	}
	
	public UserException(String message) {
		super(message);
		this.message=message;
	}
	
	public UserException(Throwable cause) {
		super(cause);
	}
	
	public UserException(String message, Throwable cause) {
		super(message, cause);
		this.message=message;
	}

	public String getMessage() {
		return message;
	}

	
}
