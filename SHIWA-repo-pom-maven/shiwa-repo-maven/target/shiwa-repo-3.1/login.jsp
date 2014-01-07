<%-- 
    Document   : login
    Created on : 11-Oct-2010, 16:18:32
    Author     : zsolt
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <style type="text/css">
            body{
                width: 800px;
                margin-left: auto;
                margin-right: auto;
                background-color: #ffffff;
                font-size: 12px;
                font-family: Verdana, "Verdana CE",  Arial, "Arial CE", "Lucida Grande CE", lucida, "Helvetica CE", sans-serif;
                color: #000000;
                text-align: center;
            }
            table{
                margin-left: auto;
                margin-right: auto;
                text-align: right;
            }
        </style>
        <title>SHIWA Workflow Repository</title>
    </head>
    <body>
        <img src="../resources/images/shiwa-logo-small.png" alt="SHIWA logo"/>
        <h1>Welcome to the SHIWA Workflow Repository</h1>
        <h3>Please log in below</h3>
        <form method="POST" action="j_security_check">
            <table>
                <tr>
                    <td>User name:</td><td><input type="text" name="j_username" /></td>
                </tr>
                <tr>
                    <td>Password:</td><td><input type="password" name="j_password" /></td>
                </tr>
                <tr>
                    <td colspan="2" style="margin-left: auto; margin-right: auto; text-align: center"><input type="submit" value="Login"/></td>
                </tr>
            </table>
        </form>
    </body>
</html>
