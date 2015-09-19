
package org.DataLayer;
import java.util.*;

//import java.sql.*;
import data.*;

/*
 * 
 * class that acts as the service layer for the beer service.
 */
public class DataLayer
{
	String dbName ="srr7890";
	String user = "srr7890";
	String pswd = "mh04de3683";
	String host = "localhost";
	String port = "3306";

/*
 * Given the username and password and the legal age the metdho returns the user from the database.
 */
	public ArrayList<ArrayList<String>> getUser(String UserName,String Password,String Age)
	{
		ArrayList<ArrayList<String>>result=null;
		try
		{
			DatabaseAccess db=new DatabaseAccess(dbName,user,pswd,host,port);
			String sql="Select * from user where UserName=? and Password=? and Age>?";
			ArrayList<String>param=new ArrayList<>();
			param.add(UserName);
			param.add(Password);
			param.add(Age);
			result=db.getDataPS(sql, param);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * 
	 * method to return the conents of the beers table from the database.
	 */
	public ArrayList<ArrayList<String>> getBeers()
	{
		ArrayList<ArrayList<String>>result=null;
		try
		{
			DatabaseAccess db=new DatabaseAccess(dbName,user,pswd,host,port);
			String sql="Select * from beers";
			result=db.getData(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * Method that returns the list of methods.
	 */
	public ArrayList<String>getMethods()
	{
		ArrayList<String>result=new ArrayList<String>();
		result.add("getToken()");
		result.add("getMethods()");
		result.add("getPrice()");
		result.add("setPrice()");
		result.add("getBeers()");
		result.add("getCheapest()");
		result.add("getCostliest");

		return result;
	}

	/*
	 * Method to store the token in the database.
	 */
	public boolean setToken(String UserName,String token,String ExpTime)
	{
		int result=0;
		try
		{
			DatabaseAccess db=new DatabaseAccess(dbName,user,pswd,host,port);
			String sql="INSERT INTO `Tokens`(`UserName`, `Token`, `ExpTime`) VALUES (?,?,?)";
			ArrayList<String>param=new ArrayList<>();
			param.add(UserName);
			param.add(token);
			param.add(ExpTime);
			result=db.nonSelect(sql, param);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(result>0)
			return true;
		else
			return false;
	}

	/*
	 * Given a token this method returns its record to the business logic layer.
	 */
	public ArrayList<ArrayList<String>> getToken(String Token)
	{
		ArrayList<ArrayList<String>>result=null;
		try
		{
			DatabaseAccess db=new DatabaseAccess(dbName,user,pswd,host,port);
			String sql="Select * from Tokens where Token=?";
			ArrayList<String>param=new ArrayList<>();
			param.add(Token);
			result=db.getDataPS(sql, param);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}
	
	/*
	 * 
	 * Method to delete a token from the database.
	 */

	public boolean deleteToken(String token)
	{
		int result=0;
		try
		{
			DatabaseAccess db=new DatabaseAccess(dbName,user,pswd,host,port);
			String sql="DELETE  FROM `Tokens` WHERE Token=?";
			ArrayList<String>param=new ArrayList<>();
			param.add(token);			
			result=db.nonSelect(sql, param);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(result>0)
			return true;
		else
			return false;
	}
	
	/*
	 * The method checks if the given user has admin rights.
	 */
	public boolean checkIsAdmin(String UserName)
	{
		boolean flag=true;
		ArrayList<ArrayList<String>>result=null;
		try
		{
			DatabaseAccess db=new DatabaseAccess(dbName,user,pswd,host,port);
			String sql="Select * from user where UserName=? and Admin=1";
			ArrayList<String>param=new ArrayList<>();
			param.add(UserName);
			result=db.getDataPS(sql, param);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(result==null)
			flag=false;
		
		return flag;
	

	}
	
	/*
	 * this method sets the price of a given beer in the database.
	 */

	public boolean setPrice(String beerName, double beerPrice) {

		int result=0;
		try
		{
			DatabaseAccess db=new DatabaseAccess(dbName,user,pswd,host,port);
			String sql="UPDATE `beers` SET`beerprice`=?"+
			" WHERE  `beername`=?;";
			ArrayList<String>param=new ArrayList<>();
			
			String bPrice=""+beerPrice;
			param.add(bPrice);
			param.add(beerName);
			result=db.nonSelect(sql, param);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(result>0)
			return true;
		else
			return false;
	}

	

}