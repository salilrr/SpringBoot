package org.Exceptions;

public class InsufficientPrivilegesException extends Exception
{
	
	private String errorDetails;
	public InsufficientPrivilegesException(String reason,String errorDetails)
	{
		super(reason);
		this.errorDetails=errorDetails;
	}

	public String getFaultInfo()
	{
		return errorDetails;
	}
}