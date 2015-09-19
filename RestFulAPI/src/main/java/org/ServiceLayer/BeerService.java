
package org.ServiceLayer;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import org.Exceptions.*;
import org.BusinessLayer.BusinessLayer;



import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/*
 * Class that acts as the Service Layer.
 * @author:Salil Rajadhyaksha
 */
@RestController
public class BeerService {

	BusinessLayer bl;
	public BeerService()
	{
		bl=new BusinessLayer();
	}

	@RequestMapping(method=RequestMethod.GET,value="/getToken")	
	public  String getToken(@RequestParam(value="UserName") String UserName,@RequestParam(value="Password")String Password) throws InvalidCredentialsException
	{

			String token=bl.getToken(UserName,Password);//call to Business logic layer.
		

		return token;//return result returned by business logic layer.
	}

	
	@RequestMapping(method=RequestMethod.GET,value="/getBeers")
	public ArrayList<String> getBeers(@RequestParam(value="token")String token) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException
	{
		

		return bl.getBeers(token);//Call to business logic layer.
	}

	
	@RequestMapping(method=RequestMethod.GET,value="/getCheapestBeer")
	public String getCheapest(@RequestParam(value="token")String token) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException
	{

		return bl.getCheapest(token);//call to business logic layer.

	}

	@RequestMapping(method=RequestMethod.GET,value="/getCostliest")
	public String getCostliest(@RequestParam(value="token")String token) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException
	{
		return bl.getCostliest(token);//call to business logic layer.
	}

	@RequestMapping(method=RequestMethod.GET,value="/getPrice")
	public double getPrice(@RequestParam(value="Beer_Name")String beer,@RequestParam(value="token")
	String token) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException, BeerNotFoundException
	{
		double beerPrice=bl.getPrice(beer,token);//call to business logic layer.
		
		return beerPrice;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/getMethods")
	public ArrayList<String> getMethods()
	{
		return bl.getMethds();//call to business logic layer.
	}
	
	@WebMethod(operationName="Set_Price")
	@WebResult(name="PriceModified")
	@RequestMapping(method=RequestMethod.POST,value="/setPrice")
	public boolean setPrice(@RequestParam(value="token")String token,@RequestParam(value="Beer_Name")String BeerName,@RequestParam(value="New_Price")double newPrice)
			throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException, InsufficientPrivilegesException
	{
		boolean priceModified=bl.setPrice(token, BeerName, newPrice);//call to business logic layer.
		
		return priceModified;
	} 

		
	}

