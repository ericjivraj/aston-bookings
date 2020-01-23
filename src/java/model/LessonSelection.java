/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author bastinl
 * Edited and Implemented by Eric Jivraj
 */
public class LessonSelection  {
    
    private HashMap<String, Lesson> chosenLessons;
    private int ownerID;
    private ArrayList clientsList = new ArrayList();
    private ArrayList lessonsList = new ArrayList();
    private HashMap dataMap = new HashMap();
    private DataSource ds = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
    private Connection connection = null;

    public LessonSelection(int owner) {
        
        chosenLessons = new HashMap<String, Lesson>();
        this.ownerID = owner;

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
        
        try 
        {
            if(connection == null)
            {
                connection = ds.getConnection();
            }
            if (connection != null) 
            {
                
                // TODO get the details of any lessons currently selected by this user
                // One way to do this: create a join query which:
                // 1. finds rows in the 'lessons_booked' table which relate to this clientid
                // 2. links 'lessons' to 'lessons_booked' by 'lessonid
                // 3. selects all fields from lessons for these rows
                
                // If you need to test your SQL syntax you can do this in virtualmin
                
                // For each one, instantiate a new Lesson object,
                // and add it to this collection (use 'LessonSelection.addLesson()' )
                
                    // Create query to select information from lessons
                    // Prepare and execute statement
                    String query = "SELECT * from lessons_booked left join lessons on lessons_booked.lessonid = lessons.lessonid where clientid = ?";

                    pstmt = connection.prepareStatement(query);
                    pstmt.setInt(1,owner);
                    rs = pstmt.executeQuery();
                    
                    // While there are records, get all the relevant values and store them in the respective variables
                    // Then instantiate a lesson object and pass in the arguments we just stored info against
                    // Populate the hashmap
                    while(rs.next())
                    {
                        String lessonid = rs.getString("lessonid");
                        
                        Timestamp startDateAndTime = rs.getTimestamp("startDateTime");
                        Timestamp endDateAndTime = rs.getTimestamp("endDateTime");
                        
                        int level = rs.getInt("level");
                        String description = rs.getString("description");
                        
                        Lesson lesson = new Lesson(description, startDateAndTime, endDateAndTime, level, lessonid);
                        chosenLessons.put(lessonid, lesson);
                    }
            }
        }
        catch(Exception e)
        {
           System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
        }
        
    }

    /**
     * @return the items
     */
    public Set <Entry <String, Lesson>> getItems() {
        return chosenLessons.entrySet();
    }

    public void addLesson(Lesson l) {
       
        Lesson i = new Lesson(l);
        this.chosenLessons.put(l.getId(), i);
       
    }

    public Lesson getLesson(String id){
        return this.chosenLessons.get(id);
    }
    
    public int getNumChosen(){
        return this.chosenLessons.size();
    }

    public int getOwner() {
        return this.ownerID;
    }
    
    public int getSize()
    {
        return this.chosenLessons.size();
    }
    
    public boolean lessonExists(String id)
    {
        if (chosenLessons.containsKey(id))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void removeLesson(String id){
        this.chosenLessons.remove(id);
    }
    
    public void updateBooking() {
        
        // A tip: here is how you can get the ids of any lessons that are currently selected
        Object[] lessonKeys = chosenLessons.keySet().toArray();
        //        for (int i=0; i<lessonKeys.length; i++) {
        //                    
        //              // Temporary check to see what the current lesson ID is....
        //              System.out.println("Lesson ID is : " + (String)lessonKeys[i]);
        //        }
      
        // TODO get a connection to the database as in the method above
        // TODO In the database, delete any existing lessons booked for this user in the table 'lessons_booked'
        // REMEMBER to use executeUpdate, not executeQuery
        // TODO - write and execute a query which, for each selected lesson, will insert into the correct table:
                    // the owner id into the clientid field
                    // the lesson ID into the lessonid field
                    
        try 
        {
            if(connection == null)
            {
                connection = ds.getConnection();
            }
            try
            {
                int owner = getOwner();
                if(connection != null)
                {
                    // Create two queries, one for deleting the lessons booked for a given client id
                    // Second query for inserting values into lessons booked
                    String query1 = "DELETE FROM lessons_booked WHERE clientid = ?";
                    String query2 = "INSERT INTO lessons_booked (clientid, lessonid) VALUES (?, ?)";

                    pstmt = connection.prepareStatement(query1);
                    pstmt.setInt(1,owner);
                    pstmt.executeUpdate();
                    
                    for(int i = 0; i < lessonKeys.length; i++)
                    {
                        pstmt = connection.prepareStatement(query2);
                        pstmt.setInt(1,owner);
                        pstmt.setString(2,(String)lessonKeys[i]);
                        pstmt.execute();
                    }
                    
                }
            }
            catch(SQLException e)
            {
                System.out.println("Exception: " + e + ": and message:" + e.getMessage());
            }
        }
        catch(Exception e)
        {           
            System.out.println("Exception: " + e + ": and message:" + e.getMessage());
        }   
    }
    
    public String getCurrentLesson()
    {                     
       // Retrieve the current lessons
       // By iterating over the records (provided they exist, and while there are records)
       // And populate the relevant lists and hashmap with the respective information
        try
        {
           if(connection == null)
           {
                connection = ds.getConnection();
           }
           
           if(connection != null)
           {
             String query = "SELECT lessonid, COUNT(DISTINCT, c.clientid) AS totalClients,"
                     + "FROM lessons_booked";
             pstmt = connection.prepareStatement(query);
             rs = pstmt.executeQuery();
             
             while(rs.next())
             {
                 lessonsList.add(rs.getInt("lessonid"));
                 clientsList.add(rs.getInt("totalClients"));
             }
             dataMap.put("lessons", lessonsList);
             dataMap.put("clients", clientsList);
           }
        }
        catch(SQLException e)
        {
            System.out.println("Exception: " + e + ": and message:" + e.getMessage());
        }  
        return dataMap.toString();
    }

}
