<%-- 
    Document   : logout
    Created on : 12-Oct-2010, 19:17:24
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
            a{
                margin-left: auto;
                margin-right: auto;
                text-align: center;
            }
        </style>
        <title>SHIWA Workflow Repository</title>
    </head>
    <body>
        <%if (session.isNew()==true) {response.sendRedirect(response.encodeRedirectURL("user/welcome.xhtml"));} else {session.invalidate();}%>
        <h1>Goodbye!</h1>

        <br/>        
        You can still browse <a href="public/welcome.xhtml">public</a> page or
        <a href="user/welcome.xhtml">log in</a> again.

    </body>
</html>
