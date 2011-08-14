<%-- 
    Document   : header
    Created on : Aug 14, 2011, 10:27:07 AM
    Author     : Samuli Penttilä <samuli.penttila@gmail.com>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="viewport" content="initial-scale=1.0, user-scalable=yes" />
        <link type="text/css" href="/uistelu/static/jquery-ui/css/smoothness/jquery-ui-1.8.15.custom.css" rel="stylesheet" />	
        <script type="text/javascript" src="/uistelu/static/jquery-ui/js/jquery-1.6.2.min.js"></script>
        <script type="text/javascript" src="/uistelu/static/js/ajax.js"></script>
        <script type="text/javascript" src="/uistelu/static/jquery-ui/js/jquery-ui-1.8.15.custom.min.js"></script>
        <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false"></script>
        <link type="text/css" href="/uistelu/static/css/style.css" rel="stylesheet" />
    </head>
    <body onload="initialize()">

        <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td width="28" height="158" class="stretch" background="/uistelu/static/back-top-left.png" >

                </td>
                <td background="/uistelu/static/back-top.png" class="stretch" height="157">
                    <table border="0" width="100%">
                        <tr>
                            <td width="150">
                                <img src="/uistelu/static/logo.png"></img>
                            </td>
                            <td>
                                <jsp:include page="menu.jsp" />
                               
                                <div class="titletext">Uistelupäiväkirja</div>
                            </td>
                            <td align="right">
                                <div id="loginbox"></div>
                            </td>                        
                        </tr>
                    </table>

                </td>
                <td width="28" height="158" class="stretch" background="/uistelu/static/back-top-right.png">

                </td>
            </tr>
            <tr>
                <td class="stretch" background="/uistelu/static/back-mid-left.png">

                </td>
                <td>
                    <table border="1" cellpadding="0" cellspacing="5" width="100%">
                        <tr>
                            <td>
