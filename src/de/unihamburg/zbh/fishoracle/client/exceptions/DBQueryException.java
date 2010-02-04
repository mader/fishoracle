package de.unihamburg.zbh.fishoracle.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DBQueryException extends Exception implements IsSerializable {

	private static final long serialVersionUID = -4501997700412460194L;
	private String message;
	
	public DBQueryException() {
	}
	
	public DBQueryException(String message) {
		super(message);
		this.message=message;
	}
	
	public DBQueryException(Throwable cause) {
		super(cause);
	}
	
	public DBQueryException(String message, Throwable cause) {
		super(message, cause);
		this.message=message;
	}

	public String getMessage() {
		return message;
	}
	
}
