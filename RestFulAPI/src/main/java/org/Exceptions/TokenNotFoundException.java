package org.Exceptions;

public class TokenNotFoundException extends Exception
{
	
	private String errorDetails;
	public TokenNotFoundException(String reason,String errorDetails)
	{
		super(reason);
		this.errorDetails=errorDetails;
	}

	public String getFaultInfo()
	{
		return errorDetails;
	}
}