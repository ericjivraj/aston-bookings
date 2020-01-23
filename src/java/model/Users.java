/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.sql.PreparedStatement;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author bastinl
 * Edited and Implemented by Eric Jivraj
 */
public class Users {
  
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
    DataSource ds = null;
    private Connection connection = null;
   
    public Users() {
        
        // You don't need to make any changes to the try/catch code below
        try {
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // Look up our data source
            ds = (DataSource)envCtx.lookup("jdbc/LessonDatabase");
        }
            catch(Exception e) {
            System.out.println("Exception message is " + e.getMessage());
        }
        
    }

    public int isValid(String username, String password) 
    {
       int value = -1;
        
       try 
       {
            if(connection == null)
            {
                connection = ds.getConnection();  
            }
            
            if (connection != null) 
            {

               //TODO: implement this method so that if the user does not exist, it returns -1.
               // If the username and password are correct, it should return the 'clientID' value from the database.
               
               // Create a query to select username and password from clients, prepares the statement,
               // Execute the query
               String query = "SELECT clientid from clients WHERE username ='" + username + "' AND password = '" + password + "';";
                pstmt = connection.prepareStatement(query);
                rs = pstmt.executeQuery(query);
                
                // If records exist then
                if(rs.next())
                {
                    value = rs.getInt(1);
                }
            }
            // Otherwise
            else 
            {
                return -1;                
            }
        } 
        catch(SQLException e) 
        {          
            System.out.println("Exception: " + e + ": and message:" + e.getMessage());
            return -1;
        }
       
        return value;
    }
    
    public boolean userExists(String username)
    {
        // Boolean flag to check it exists by default set to false
        // Query to select and verify if username exists
        // Prepare and execute the query
        // Flag logically set to true if user exists, otherwise false
        boolean exists = false;
        try
        {
            if(connection == null)
            {
                connection = ds.getConnection();
            }
            
            if(connection != null)
            {
                String query = "SELECT username FROM clients WHERE username = ?";
                pstmt = connection.prepareStatement(query);
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();
                
                if(rs.next())
                { 
                    exists = true;
                }
            }
        }
        catch(SQLException e)
        {
            System.out.println("Exception is ;"+e + ": message is " + e.getMessage());   
        }
        
        return exists;
    }
    
    public void addUser(String username, String password) 
    {
        // Create a query that inserts username and password into the clients table in database
        // Prepare the statement and execute it
         try 
         {
            if(connection == null)
            {
                connection = ds.getConnection();
            }
            
            if (connection != null)
            {
                String query = "INSERT INTO clients (username, password) VALUES (?,?)";
                pstmt = connection.prepareStatement(query);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeUpdate();            
            }
         }
         catch(SQLException e) 
         {
            System.out.println("Exception message is " + e.getMessage());
         }
    }
}


