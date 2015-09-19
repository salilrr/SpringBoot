package org.Exceptions;

public class TokenExpiredException extends Exception
{
	
	private String errorDetails;
	public TokenExpiredException(String reason,String errorDetails)
	{
		super(reason);
		this.errorDetails=errorDetails;
	}

	public String getFaultInfo()
	{
		return errorDetails;
	}
}