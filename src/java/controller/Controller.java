/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Lesson;

import model.LessonTimetable;
import model.LessonSelection;
import model.Users;

/**
 *
 * @author bastinl
 * Edited and Implemented by Eric Jivraj
 */
public class Controller extends HttpServlet {

   private Users users;
   private LessonTimetable availableLessons;
   private LessonSelection lessonSelection;
   
   private final String loginAction = "/login";
   private final String addUserAction = "/addUser";
   private final String checkNameAction = "/checkName";
   private final String chooseLessonAction = "/chooseLesson";
   private final String finaliseBookingAction = "/finaliseBooking";
   private final String logoutAction = "/logout";
   private final String cancelLessonAction = "/cancelLesson";
   
   private final String loginView = "/login.jsp";
   private final String lessonTimetableView = "/LessonTimetableView.jspx";
   private final String lessonSelectionView = "/LessonSelectionView.jspx";
   private final String confirmedBookingView = "/confirmedBooking.jspx";
   private final String logoutView = "/logout.jsp";
   

    public void init() {
         users = new Users();
         availableLessons = new LessonTimetable();
         // TODO Attach the lesson timetable to an appropriate scope
         getServletContext().setAttribute("availableLessons", availableLessons);
        
    }
    
    public void destroy() {
        
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Gets the path info related to the user performed action
        // Declare our dispatcher and initialize it to null
        // Declare our PrintWriter
        String action = request.getPathInfo();
        RequestDispatcher requestDispatcher = null;
        PrintWriter out;
        
        // Verify if the action is equals to "login" 
        if(action.equals(loginAction))
        {
            // Get username parameter from what the user input
            // Get password parameter from what the user input
            // Validate the user credentials, if valid, return the client id, if invalid, return -1 (which means the clientid doesnt exist 
            // Because db doesnt have any negative clientids in there
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            int clientId = users.isValid(username, password);
            
        // If user credentials are valid    
        if(clientId != -1)
        {
            // Get the current session
            // Store the username in the session so it remembers it
            HttpSession currentSession = request.getSession();
            currentSession.setAttribute("user", username);
            
            // Instantiating lesson selection bean that holds the lessons user has selected
            // Storing lesson selection in the session so it remembers it
            lessonSelection = new LessonSelection(clientId);
            currentSession.setAttribute("lessonSelection", lessonSelection);
            
            // Dispatch and redirect to the Lesson Timetable View
            requestDispatcher = this.getServletContext().getRequestDispatcher(lessonTimetableView);         
            requestDispatcher.forward(request, response);
        } 
        
        // Else, if user credentials are invalid
        else if(clientId == -1)
        {
            // Dispatch and redirect to the Login View
            requestDispatcher = this.getServletContext().getRequestDispatcher(loginView);
        }
       }
       // Verify if the action is equals to "addUser" 
       else if(action.equals(addUserAction))
       {
           // Get the new username from parameters
           // Get the new password from parameters
           // Both of which come from user input
           // Add the user into the database
            String newUsername = request.getParameter("newUsername");
            String newPassword = request.getParameter("newPassword");
            users.addUser(newUsername, newPassword);
            
            // Dispatches and redirects the user to the Login View
            requestDispatcher = this.getServletContext().getRequestDispatcher(loginView);
            requestDispatcher.forward(request, response);
       } 
       // Verify if the action is equals to "checkName" 
       else if(action.equals(checkNameAction))
       {
           // Get new username from parameters, from user input
           // Verify if user exists
           // Print out the response and close writer
            String newUsername = request.getParameter("newUsername");
            Boolean temp = users.userExists(newUsername);

            response.setContentType("application/xml; charset-UTF-8");
            out = response.getWriter();
            
            try
            { 
                out.print("<?xml version=\"1.0\"?><response>" + temp + "</response>");   
            }
            catch(Exception e)
            {
                System.out.println("Exception message is " + e.getMessage());
            }
            finally
            {
                out.close();
            }      
        } 
            // Verify if the action is equals to "chooseLesson" 
            else if(action.equals(chooseLessonAction))
            {
                // Gets lesson id and stores it in variable 
                // Gets the current session 
                // Sets the attribute of lesson id to the sessions lesson id
                String lessonID = request.getParameter("lessonId");
                HttpSession currentSession = request.getSession();
                currentSession.setAttribute("lessonId", lessonID);
                
                // Gets available lessons
                // Verifies if lesson has already been selected
                // If it doesn't exist it will add it to the lesson selection
                Lesson chosenLesson = availableLessons.getLesson(lessonID);
                if(lessonSelection.getLesson(lessonID) == null)
                {
                    lessonSelection.addLesson(chosenLesson);
                    currentSession.setAttribute("chosenLessons", chosenLesson);
                }
                
                // Dispatches and redirects user to the Lesson Selection View
                requestDispatcher = this.getServletContext().getRequestDispatcher(lessonSelectionView);
                requestDispatcher.forward(request, response);
                
            }
            // Verify if the action is equals to "finaliseBooking" 
            else if(action.equals(finaliseBookingAction))
            {
                // Delete any previous existing booking in database
                // Proceeds to then store new lessons booked in to the database
                // It does the above for the given session user currently logged in
                lessonSelection.updateBooking();
                
                // Dispatches and redirects Confirmed Booking View 
                requestDispatcher = this.getServletContext().getRequestDispatcher(confirmedBookingView);
                requestDispatcher.forward(request, response);
            }
            // Verify if the action is equals to "logout" 
            else if(action.equals(logoutAction))
            {
                // Get the current session
                // Session becomes invalidated
                HttpSession currentSession = request.getSession();
                currentSession.invalidate();
                
                // Dispatches and redirects user to Logout View, which will then enable user to return to login page from there
                requestDispatcher = this.getServletContext().getRequestDispatcher(logoutView);
                requestDispatcher.forward(request, response);
            }
            // Verify if the action is equals to "cancelLesson" 
            else if(action.equals(cancelLessonAction))
            {
                // Get the lesson id associated with the button from parameters
                // Remove the lesson from the lesson selection
                String lesson = request.getParameter("cancelLesson");
                lessonSelection.removeLesson(lesson);
                
                // Dispatches and redirects user to the Lesson Selection View
                requestDispatcher = this.getServletContext().getRequestDispatcher(lessonSelectionView);
                
                
            }
                
        requestDispatcher.forward(request, response);   
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
