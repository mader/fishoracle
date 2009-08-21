package de.unihamburg.zbh.fishoracle.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchException extends Exception implements IsSerializable{

	private static final long serialVersionUID = -3699417545301820872L;
	private String message;
	
	public SearchException() {
	}
	
	public SearchException(String message) {
		super(message);
		this.message=message;
	}
	
	public SearchException(Throwable cause) {
		super(cause);
	}
	
	public SearchException(String message, Throwable cause) {
		super(message, cause);
		this.message=message;
	}

	public String getMessage() {
		return message;
	}

}
