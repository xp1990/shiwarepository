<%-- 
    Document   : reactivate
    Created on : 13-Oct-2010, 12:18:29
    Author     : zsolt
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SHIWA Workflow Repository</title>
    </head>
    <body>
        <h1>Please enter your user name and e-mail address:</h1>
        <form method="POST" action="j_security_check">
            <table>
                <tr>
                    <td>User name:</td><td><input type="text" name="username" /></td>
                </tr>
                <tr>
                    <td>E-mail address:</td><td><input type="text" name="email" /></td>
                </tr>
                <tr>
                    <td><input type="reactivate" value="Reactivate" /></td>
                </tr>
            </table>
        </form>>
    </body>
</html>
