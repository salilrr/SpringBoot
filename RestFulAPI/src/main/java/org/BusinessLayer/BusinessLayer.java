package org.BusinessLayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Iterator;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.Exceptions.BeerNotFoundException;
import org.Exceptions.BeerServiceClosedException;
import org.Exceptions.InsufficientPrivilegesException;
import org.Exceptions.InvalidCredentialsException;


import org.Exceptions.TokenExpiredException;
import org.Exceptions.TokenNotFoundException;
import org.DataLayer.*;
/*
 * Class that implemets the business logic for the Beer Service.
 * @author:Salil Rajadhyaksha.
 */
public class BusinessLayer {

	DataLayer dl;
	final String AGE_LIMIT="21";
	final int TOKEN_EXPIRATION_TIME=5;
	final String START_HOURS="10:00";
	final String END_HOURS="23:59";
	final String OPENING_HOURS="10:00";

	public BusinessLayer()
	{
		dl=new 	DataLayer();
	}

	
/*
 * Method to check if current time is between the allowed working hours.
 * Code referenced and modified from stackoverflow.com.	
 */
	public boolean workingHours()
	{
		 boolean flag=true;
		try
		{
		// Start Time
		Date startTime = new SimpleDateFormat("HH:mm").parse(START_HOURS);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startTime);//set the starting hours.

		// Current Time
		Calendar curCal=Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		String currTime=df.format(curCal.getTime());//set the current time

		Date currentTime = new SimpleDateFormat("HH:mm").parse(currTime);
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTime(currentTime);

		// End Time
		java.util.Date endTime = new SimpleDateFormat("HH:mm").parse(END_HOURS);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endTime);//set the closing time.

		//
		if (currentTime.compareTo(endTime) < 0) {

			//currentCalendar.add(Calendar.DATE, 1);
			currentTime = currentCalendar.getTime();

		}

		if (startTime.compareTo(endTime) < 0) {

			//startCalendar.add(Calendar.DATE, 1);
			startTime = startCalendar.getTime();

		}
		//
		if (currentTime.before(startTime)) {

			flag=false;
		} else {

			if (currentTime.after(endTime)) {
				//endCalendar.add(Calendar.DATE, 1);
				endTime = endCalendar.getTime();

			}


			if (currentTime.before(endTime)) {
				flag=true;
			} else {
				flag=false;
			}



		} 
		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return flag;//return true if time is between working hours,else return false.
	}

	
	/*
	 * 
	 * Function to verify if the given token exists in the DB.It also checks if the token has expired or is still valid.
	 * 
	 */
	
	public boolean verifyToken(String token) throws BeerServiceClosedException, TokenNotFoundException
	{
		boolean flag=false;
		
		
		if(this.workingHours()==false)
			throw new BeerServiceClosedException("BeerService is Closed", "Service not operational  from 00:00 to 10:00");
		
		ArrayList<ArrayList<String>>res=dl.getToken(token);
		if(res==null)
			throw new TokenNotFoundException("Token not found", "This token is not valid.");
		try
		{//checking if token has expired or is still valid.
			String tokenTime=res.get(0).get(2);
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			Date tokenDate = df.parse(tokenTime); 
			Calendar tokenCal = Calendar.getInstance();


			tokenCal.setTime(tokenDate);
			Calendar curCal=Calendar.getInstance();

			String currTime=df.format(curCal.getTime());
			Date curDate = df.parse(currTime);

			curCal.setTime(curDate);

			if((tokenDate.getTime()-curDate.getTime())>0)
			{
				
				if((tokenDate.getTime()-curDate.getTime())<=(TOKEN_EXPIRATION_TIME*60*1000))
						flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
		return flag;
	}

	//function that returns the list of methods in the webservice.
	public ArrayList<String> getMethds()	
	{

		ArrayList<String> result=dl.getMethods();
		return result;
	}

	//function that generates a token and returns it to the calling client.	
	public String getToken(String UserName,String password) throws  InvalidCredentialsException  
	{

		ArrayList<ArrayList<String>>result=dl.getUser(UserName,password,AGE_LIMIT);//check the credentials of the user and age limit

		//user not found or user is under-age.
		if(result==null)
		{
			throw new InvalidCredentialsException("Invalid Input", "You don not have access to the system");
		}
		else
		{
			Random rand=new Random();
			String token=UserName+rand.nextInt(Integer.MAX_VALUE);//random token
			Calendar cal=Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			cal.add(Calendar.MINUTE,TOKEN_EXPIRATION_TIME);
			String ExpTime=sdf.format(cal.getTime());
			dl.setToken(UserName, token, ExpTime);//store token in database.
			return token;
		}
	}

	/*
	 * Method that returns the list of beers to the service layer.
	 */
	
	public ArrayList<String> getBeers(String Token) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException
	{

		boolean tokenValidity=verifyToken(Token);//check for validity of token.

		if(tokenValidity==false)
		{
			dl.deleteToken(Token);
			throw new TokenExpiredException("Token has expired", "Token valid for only "+
		                                     TOKEN_EXPIRATION_TIME+" after being generated.");
		}

		ArrayList<ArrayList<String>>res=dl.getBeers();//get the beers from database.

		Iterator<ArrayList<String>>itr=res.iterator();

		ArrayList<String>temp=new ArrayList<String>();
		ArrayList<String>result=new ArrayList<String>();
		while(itr.hasNext())
		{
			temp=itr.next();
			result.add(temp.get(1));

		}
		return result;//return list of beers to the Service layer.
	}

	/*
	 * 
	 * Method to return the name of the cheapest beer.
	 * 
	 */
	
	public String getCheapest(String Token) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException
	{
		boolean tokenValidity=verifyToken(Token);

		if(tokenValidity==false)
		{
			dl.deleteToken(Token);
			throw new TokenExpiredException("Token has expired", "Token valid for only "+
		                                     TOKEN_EXPIRATION_TIME+" after being generated.");
		}
		ArrayList<ArrayList<String>>res=dl.getBeers();
		Iterator<ArrayList<String>>itr=res.iterator();

		ArrayList<String>temp=new ArrayList<String>();
		double result=Double.MAX_VALUE;
		String resultString=null;
		//iterating to find the cheapest beer.
		while(itr.hasNext())
		{
			temp=itr.next();
			double tempCost=Double.parseDouble(temp.get(2));
			if(tempCost<result)
			{
				result=tempCost;
				resultString=temp.get(1);
			}

		}
		return resultString;

	}

	/*
	 * 
	 * Method that returns the costliest beer.
	 * 
	 */
	
	public String getCostliest(String Token) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException
	{
		boolean tokenValidity=verifyToken(Token);

		if(tokenValidity==false)
		{
			dl.deleteToken(Token);
			throw new TokenExpiredException("Token has expired", "Token valid for only "+
		                                     TOKEN_EXPIRATION_TIME+" after being generated.");
		}

		ArrayList<ArrayList<String>>res=dl.getBeers();
		Iterator<ArrayList<String>>itr=res.iterator();

		ArrayList<String>temp=new ArrayList<String>();
		double result=0.0;
		String resultString=null;
		//iterating to find the costliest beer.
		while(itr.hasNext())
		{
			temp=itr.next();
			double tempCost=Double.parseDouble(temp.get(2));
			if(tempCost>result)
			{
				result=tempCost;
				resultString=temp.get(1);
			}

		}
		return resultString;

	}

	/*
	 * Given the name of a beer and a valid token the method finds the price of the beer from the database.
	 */
	public double getPrice(String beer,String Token) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException, BeerNotFoundException
	{
		double beerPrice=0.0;
		boolean tokenValidity=verifyToken(Token);

		if(tokenValidity==false)
		{
			dl.deleteToken(Token);
			throw new TokenExpiredException("Token has expired", "Token valid for only "+
		                                     TOKEN_EXPIRATION_TIME+" after being generated.");
		}

		ArrayList<ArrayList<String>>res=dl.getBeers();
		Iterator<ArrayList<String>>itr=res.iterator();

		ArrayList<String>temp=new ArrayList<String>();
		//finding the cost of the given beer.
		while(itr.hasNext())
		{
			temp=itr.next();

			if(temp.get(1).equals(beer))
			{
				beerPrice=Double.parseDouble(temp.get(2));
			}

		}
		if(beerPrice==0)
		{
			dl.deleteToken(Token);
			throw new TokenExpiredException("Token has expired", "Token valid for only "+
		                                     TOKEN_EXPIRATION_TIME+" after being generated.");
		}		
		return beerPrice;	


	}

	/*
	 * 
	 * Method to set the price of a given beer.
	 * the method allows  a user with sufficient rights and a valid token to change the price of the beer.
	 * returns true or false depending on whether the price was set.
	 */
	
	public boolean setPrice(String Token,String BeerName,double BeerPrice) throws TokenExpiredException, BeerServiceClosedException, TokenNotFoundException, InsufficientPrivilegesException
	{
		boolean tokenValidity=verifyToken(Token);

		if(tokenValidity==false)
		{
			dl.deleteToken(Token);
			throw new TokenExpiredException("Token has expired", "Token valid for only "+
		                                     TOKEN_EXPIRATION_TIME+" after being generated.");
		}
			ArrayList<ArrayList<String>>res=dl.getToken(Token);
			
			String user=res.get(0).get(0);
			
			boolean isAdmin=dl.checkIsAdmin(user);//check if user has privliges.
			
			if(isAdmin==false)
				throw new InsufficientPrivilegesException("Insufficient Privileges",
						"The user does not have acces to make changes to the price");
			
			boolean priceSet=dl.setPrice(BeerName,BeerPrice);
				

		return priceSet;
	}
	

}
