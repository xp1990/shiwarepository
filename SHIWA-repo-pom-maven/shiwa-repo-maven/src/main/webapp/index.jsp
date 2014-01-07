<%--
    Document   : index
    Created on : 17-Sep-2010, 17:19:49
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
        </style>
        <title>SHIWA Workflow Repository</title>
    </head>
    <body>
        <img src="resources/images/shiwa-logo-small.png" alt="SHIWA logo"/>
        <h1>Welcome to the SHIWA Workflow Repository</h1>
        <br/>
        Please browse the <a href="public/welcome.xhtml">public</a> page or
        <a href="user/welcome.xhtml">log in</a>.
	<br/>
	<br/>
	<center>
	<div align="left" style="width: 800px">
		The FP7 <a href="http://www.shiwa-workflow.eu/">SHIWA project</a> addresses the challenges of the coarse- and fine-grained workflow interoperability. The project created the SHIWA Simulation Platform which enables users to create and run embedded workflows which incorporate workflows of different workflow systems. The platform consists of the SHIWA Science Gateway and the SHIWA VO. The SHIWA Science Gateway contains the <a href="http://ssp.shiwa-workflow.eu/">SHIWA portal</a>, the SHIWA Workflow Repository, the SHIWA Proxy Server, the GEMLCA Service and the GEMLCA Repository.
To find out more about the SHIWA Simulation Platform, and learn about how to get access to execute the workflows deployed in the repository, please go to the <a href="http://www.cpc.wmin.ac.uk/cpcsite/index.php/Other_projects:SHIWA_SSPv2">SHIWA wiki page</a>.
		<br/>
		The SHIWA Workflow Repository manages workflow descriptions, and implementations and configurations of workflows. The repository can be used by the following types of actors:
                <UL TYPE="SQUARE">
                        <LI><b>E-scientists:</b> They can browse and search the repository to find and download workflows. They can use the repository without registration.</LI>
			<LI><b>Workflow developers:</b> They are the workflow owners who can upload, modify and delete workflows. They should register with the repository.</LI>
			<LI><b>Repository administrator:</b> The actor who manages the repository.</LI>
		</UL>
		<br/>

                To get access, please send an email to the repository administrator, <a href="mailto:N.Weingarten01@westminster.ac.uk"> Noam Weingarten</a>, with the subject "SHIWA Repository account request". Please state in your email your full name, organization, and desired username.
        </div>
        </center>
    </body>
</html>
