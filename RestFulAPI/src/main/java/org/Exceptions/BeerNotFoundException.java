package org.Exceptions;

public class BeerNotFoundException extends Exception
{
	
	private String errorDetails;
	public BeerNotFoundException(String reason,String errorDetails)
	{
		super(reason);
		this.errorDetails=errorDetails;
	}

	public String getFaultInfo()
	{
		return errorDetails;
	}
}