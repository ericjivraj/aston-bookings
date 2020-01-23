/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


/**
 *
 * @author bastinl
 * Edited and Implemented by Eric Jivraj
 */
public class LessonTimetable {

    private Connection connection = null;
  
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
  
    private Map lessons = null;
  
    private DataSource ds = null;
    
    public LessonTimetable() {

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
                
                // TODO instantiate and populate the 'lessons' HashMap by selecting the relevant infromation from the database
                //query to select all lesson details, prepares statement and executes
                
                // Create query to select lessons information
                // Prepare the statement and execute the statement
                // Instantiate the lessons hashmap
                String query = "SELECT lessonid, startDateTime, endDateTime, level, description from lessons";
                pstmt = connection.prepareStatement(query);
                rs = pstmt.executeQuery(query);
                lessons = new HashMap<String, Lesson>();
                
                // TODO add code here to retrieve the infromation and create the new Lesson objects
                
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
                    lessons.put(lessonid, lesson);
                }
            }
          }
          catch(Exception e)
          {
                System.out.println("Exception: " + e + ": and message:" + e.getMessage());
          }
      
    }
    
   
    /**
     * @return the items
     */
    public Lesson getLesson(String itemID) {
        
        return (Lesson)this.lessons.get(itemID);
    }

    public Map getLessons() {
        
        return this.lessons;
        
    }
    
}
