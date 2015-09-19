package org.Exceptions;

public class BeerServiceClosedException extends Exception
{
	
	private String errorDetails;
	public BeerServiceClosedException(String reason,String errorDetails)
	{
		super(reason);
		this.errorDetails=errorDetails;
	}

	public String getFaultInfo()
	{
		return errorDetails;
	}
}