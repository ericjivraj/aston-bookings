<%-- 
    Document   : index
    Created on : 15-Mar-2010, 14:47:22
    Author     : bastinl
    Edited and Implemented by Eric Jivraj
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    
    <script language="javascript">
           // Declare variable req
           // And then we have the function exists that takes in a username and validates the length of the username
           // Inside we instantiate the request object to be of type XMLHttpRequest
           // And open a POST request, and sends it with the username as a parameter that is appended to th request
           // And disable register button
           // It will throw an alert if username is below 7 characters
           var req;
           function checkUsername(username)
           {
                if(username.length > 7)
                {
                    req = new XMLHttpRequest();
                    if(req != null)
                    {
                        req.open("POST", "http://localhost:8084/coursework/do/checkName", true);
                        req.onreadystatechange = function()
                        {
                            processResponse();
                        };
                        req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                        req.send("newUsername=" +(username));
                        document.getElementById("signUp").disabled = false;
                    } 
                }
                else
                { 
                    alert("Username must be longer than 7 characters!");
                    document.getElementById("signUp").disabled = true;
                }
            }
     

            // This is the process response function used in the callback for the function above
            // It will verify if the state is 4 and the status is 200, and if that is the case it will proceed as normal
            // Will check if response is false, if it is, then username is valid and register button will become enabled
            // It will throw an alert if username already exists
            function processResponse()
            {
                if(req.readyState == 4 && req.status == 200)
                {
                    var res = req.responseXML.documentElement;
                    var node = res.childNodes[0].data;
                    if (node == "false")
                    {
                        document.getElementById("signUp").disabled = false;
                    }
                    else
                    {
                        alert("This username already exists!");
                        document.getElementById("signUp").disabled = true;                        
                    }
                }
            }
            
            // This is the checkUsername function which is used to check the username length
            // Will do straightforward username validation, similar to the exists function
            // Except in here we are validating the actual login details length and not the sign up like we have done above
            // 
            function validateUsername()
            {
                    var username = document.getElementById("usernameLogin").value;
                    if(username.length < 7)
                    {
                        alert("Username must be longer than 7 characters!");
                        document.getElementById("login").disabled = true;
                    }
                    else
                    {
                        document.getElementById("login").disabled = false;
                    }
            }
    </script>
    
    <head>
       
        <title>Login / signup page</title>
        
    </head>
    <body>
        
        <h2>Please log in!</h2>
        <form method="POST" action="http://localhost:8084/coursework/do/login">
            Username:<input type="text" name="username" id="usernameLogin" value="" onchange="validateUsername()" />----
                Password:<input type="password" name="password" value="" />        
        <input type="submit" id="login" value="Click to log in" disabled />
        </form>
        
        <form method="POST" action="http://localhost:8084/coursework/do/addUser">
            <h2> Don't yet have an account? </h2>
            Username:<input type="text" name="newUsername" id="usernameRegister" value="" onchange="checkUsername(this.value)" />----
            Password:<input type="password" name="newPassword" value="" />      
            <input type="submit" id="signUp" value="Sign up as a new user" disabled/>
        </form>
        
    </body>
</html>
