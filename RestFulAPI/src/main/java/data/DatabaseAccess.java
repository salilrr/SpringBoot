package data;

import java.sql.*;
import java.util.*;

public class DatabaseAccess
{

   private String host = "localhost";
   private String port = "8889"; //make sure matches setup 3306 is normal
   private String dbName;
   private String userId = "root";
   private String pswd;
   private Connection conn;
   private final String driver = "com.mysql.jdbc.Driver";
   
  /**
   * Default constructor opens a connection to MySQL on localhost. "USE <i>database</i>" 
   * command must be issued by user (see setData() method).  Assumes use of 
   * "root" id with no password.
   * @throws ClassNotFoundException Thrown if unable to load driver.
   * @throws SQLException Thrown if unable to establish connection. 
   */   
   public DatabaseAccess() throws SQLException, ClassNotFoundException
   {
      connect(this.dbName, this.userId, this.pswd, this.host, this.port);
   }

  /**
   * Constructor opens a connection to MySQL on localhost
   * and sets cuurent database to user-specifid value. Assumes use of
  * "root" id with no password.
   * @param dbName Name of the database to be selected.
   * @throws ClassNotFoundException Thrown if unable to load driver.
   * @throws SQLException Thrown if unable to establish connection.
   */   
   public DatabaseAccess(String dbName) throws SQLException, ClassNotFoundException
   {
      this.dbName = dbName;
      connect(this.dbName, this.userId, this.pswd, this.host, this.port);
   }

 /**
   * Constructor opens a connection to MySQL on localhost
   * and sets current database using specified user id and password.
   * @param dbName Name of the database to be selected.
   * @param userId User Id.
   * @param pswd User password.
   * @throws ClassNotFoundException Thrown if unable to load driver.
   * @throws SQLException Thrown if unable to establish connection.
   */   
   public DatabaseAccess(String dbName, String userId, String pswd) throws SQLException, ClassNotFoundException
   {
      this.dbName = dbName;
      this.userId = userId;
      this.pswd = pswd;
      connect(this.dbName, this.userId, this.pswd, this.host, this.port);
   }

 /**
   * Constructor opens a connection to MySQL on specified host
   * and sets current database using specified userId and password.
   * @param dbName Name of the database to be selected.
   * @param userId User Id.
   * @param pswd User password.
   * @param host IP address (or URL) of database host.
   * @param port Port of database host used for access.
   * @throws ClassNotFoundException Thrown if unable to load driver.
   * @throws SQLException Thrown if unable to establish connection.
   */   
   public DatabaseAccess(String dbName, String userId, String pswd, String host, String port) throws SQLException, ClassNotFoundException
   {
     this.dbName = dbName;
      this.userId = userId;
      this.pswd = pswd;
      this.host = host;
      this.port = port;
      connect (this.dbName, this.userId, this.pswd, this.host, this.port);
   }

 /**
   * Convenience method for connecting to database after instantiation 
   * of this object.
   * @param dbName Name of the database to be selected.
   * @param userId User Id.
   * @param pswd User password.
   * @param host IP address (or URL) of database host.
   * @param port Port of database host used for access.
   * @return true if successful; throws exception otherwise.
   * @throws ClassNotFoundException Thrown if unable to load driver.
   * @throws SQLException Thrown if unable to establish connection.
   */  
   public boolean connect(String dbName, String userId, String pswd, String host, String port) throws SQLException, ClassNotFoundException
   {
      Class.forName(driver);
      host = "jdbc:mysql://" + host;
      if (!port.equals("")) 
      {
         host += ":" + port;
      }

      conn =  DriverManager.getConnection(host,userId,pswd);

      if (!dbName.equals(""))
      {
         Statement stmt = conn.createStatement();
         stmt.executeUpdate("use " + dbName);
         stmt.close();
      }
      return true;
   }
   
 /**
   * Closes database connection.
   * @return true if successful; throws exception otherwise.
   * @throws SQLException if closing the database is unsuccessful.
   */  
    public boolean close()  throws SQLException
    {
      conn.close();
      return true;
    }

 /**
   * Returns the results of excuting a query against the current database.
   * @param sql Properly formatted SQL query statement.
   * @return a 2-dimensional ArrayList (ArrayList fo ArrayLists) representing 
   * the results. Return value of null indicates no data satisfied the query.
   * @throws SQLException Any SQL error is thrown as an SQLException
   */  
   public ArrayList<ArrayList<String>> getData(String sql) throws SQLException
   {
      Statement stmt = conn.createStatement();
      stmt.setEscapeProcessing(true);  
      ResultSet rs = stmt.executeQuery(sql);
      ArrayList<ArrayList<String>> rv = new ArrayList<ArrayList<String>>();

      ResultSetMetaData rsmd = rs.getMetaData();
      int colCount=rsmd.getColumnCount();
//    rsmd.close();
      
      while(rs.next()) 
      {
         ArrayList<String> row = new ArrayList<String>();
         for (int i=1; i<=colCount; i++)
            row.add(rs.getString(i));
         rv.add(row); 
      }
     
      rs.close();
      stmt.close();
      return rv;
   }
   
 /**
   * Returns the results of excuting a query against the current database.
   * @param sql Properly fromatted SQL query statement.
   * @return an ArrayList containing a HashMap representing 
   * the results. Return value of null indicates no data satisfied the query.
   * @throws SQLException Any SQL error is thrown as an SQLException
   */  
   public ArrayList<HashMap<String,String>> getDataWithColNames(String sql) throws SQLException
   {
      Statement stmt = conn.createStatement();
      stmt.setEscapeProcessing(true);  
      ResultSet rs = stmt.executeQuery(sql);
      ArrayList<HashMap<String,String>> rv = new ArrayList<HashMap<String,String>>();

      ResultSetMetaData rsmd = rs.getMetaData();
      int colCount=rsmd.getColumnCount();
//    rsmd.close();
      
      while(rs.next()) 
      {
         HashMap<String,String> row = new HashMap<String,String>();
         for (int i=1; i<=colCount; i++)
            row.put(rsmd.getColumnName(i),rs.getString(i));
         rv.add(row); 
      }
      
      rs.close();
      stmt.close();
      return rv;
   }
   
 /**
   * Executes the specified SQL command against the current database.
   * @param sql Properly formatted SQL statement.
   * @return true on success; Throws SQLException otherwise.
   * @throws SQLException Any SQL error is thrown as an SQLException
   */  
   public boolean setData(String sql) throws SQLException
   {
      Statement stmt = conn.createStatement();
      stmt.setEscapeProcessing(true);  
      stmt.executeUpdate(sql);
      stmt.close();
      return true;
   }
   
   protected void finalize() throws SQLException
   {
      close();
   }
   
   //private method that binds the parameters to the placeholders in the parameterized query.
   private PreparedStatement parseSql(String sql, ArrayList<String> params) throws SQLException {
         PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i=0; i <params.size(); i++) {
            String val = params.get(i);
            pstmt.setString(i+1, val);
         }
         return pstmt;
   }
   
  /**
   * Returns the number of rows affected by executing a parameterized query statement 
   * against the current database (Insert/Update/Delete).
   * @param sql Properly formatted  SQL parameterized Insert/Update/Delete query statement.
   * @param params an ArrayList<String> of parameters to match the placeholders in the parameterized query
   * @return an int representing the number of rows that were affected by the query 
   * the results. Return value of null indicates no data satisfied the query.
   * @throws SQLException Any SQL error is thrown as an SQLException
   */  
   
   public int nonSelect(String sql, ArrayList<String> params) throws SQLException {
         PreparedStatement pstmt = parseSql(sql,params);
         int numRows = pstmt.executeUpdate();
         pstmt.close();
        return numRows;
   }
 
  /**
   * Returns the results of excuting a parameterized query statement against the current database.
   * @param sql Properly formatted  SQL parameterized query statement.
   * @param params an ArrayList<String> of parameters to match the placeholders in the parameterized query
   * @return an ArrayList containing a HashMap representing 
   * the results. Return value of null indicates no data satisfied the query.
   * @throws SQLException Any SQL error is thrown as an SQLException
   */  
   public ArrayList<ArrayList<String>> getDataPS (String sql, ArrayList<String> params) throws SQLException {
      PreparedStatement pstmt = parseSql(sql,params);
      ResultSet rs = pstmt.executeQuery(); 
      ArrayList<ArrayList<String>> rv = new ArrayList<ArrayList<String>>();

      ResultSetMetaData rsmd = rs.getMetaData();
      int colCount=rsmd.getColumnCount();

      while(rs.next()) 
      {
         ArrayList<String> row = new ArrayList<String>();
         for (int i=1; i<=colCount; i++)
            row.add(rs.getString(i));
         rv.add(row); 
      }
      
      rs.close();
      pstmt.close();
      if (rv.size() > 0) {
         return rv;
      } else {
         return null;
      }
   }

  /**
   * Returns the results of excuting a parameterized query statement against the current database.
   * @param sql Properly formatted  SQL parameterized query statement.
   * @param params an ArrayList<String> of parameters to match the placeholders in the parameterized query
   * the results. Return value of null indicates no data satisfied the query.
   * @throws SQLException Any SQL error is thrown as an SQLException
   */  
  public ArrayList<HashMap<String,String>>  getDataPSWithColNames (String sql, ArrayList<String> params) throws SQLException {
      PreparedStatement pstmt = parseSql(sql,params);
      ResultSet rs = pstmt.executeQuery(); 
      ArrayList<HashMap<String,String>> rv = new ArrayList<HashMap<String,String>>();

      ResultSetMetaData rsmd = rs.getMetaData();
      int colCount=rsmd.getColumnCount();

      while(rs.next()) 
      {
         HashMap<String,String> row = new HashMap<String,String>();
         for (int i=1; i<=colCount; i++)
            row.put(rsmd.getColumnName(i),rs.getString(i));
         rv.add(row); 
      }
      
      rs.close();
      pstmt.close();
      if (rv.size() > 0) {
         return rv;
      } else {
         return null;
      }
   }
   
}