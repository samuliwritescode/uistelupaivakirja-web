<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="viewport" content="initial-scale=1.0, user-scalable=yes" />
        <link type="text/css" href="static/jquery-ui/css/smoothness/jquery-ui-1.8.15.custom.css" rel="stylesheet" />	
        <script type="text/javascript" src="static/jquery-ui/js/jquery-1.6.2.min.js"></script>
        <script type="text/javascript" src="static/jquery-ui/js/jquery-ui-1.8.15.custom.min.js"></script>
        <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false"></script>
        <style type="text/css">
            .menulink { font-size: small; color: #000000; text-decoration: none;}
            .titletext {font-family: "Futura"; font-size: xx-large;}
            .stretch {background-repeat: no-repeat; background-size: 100% 100%;}
            .mainwidget { width:100%; height:500px;}
            body{ margin: 0px;}
        </style>
    </head>

    <body>

        <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td width="28" height="158" class="stretch" background="static/back-top-left.png" >

                </td>
                <td background="static/back-top.png" class="stretch" height="157">
                    <table border="0" width="100%">
                        <tr>
                            <td width="150">
                                <img src="static/logo.png"></img>
                            </td>
                            <td>
                                <table cellpadding="0" cellspacing="0" border="0">
                                    <tr>
                                        <td width="24" height="56" background="static/menu-left.png" class="stretch">
                                        </td>
                                        <td height="56" background="static/menu.png" class="stretch">
                                            <a href="" class="menulink">Pääsivu</a>&nbsp;&nbsp;
                                        </td>
                                        <td height="56" background="static/menu-selected.png" class="stretch">
                                            &nbsp;&nbsp;<a href="" class="menulink">Omat kalat</a>&nbsp;&nbsp;
                                        </td>
                                        <td height="56" background="static/menu.png" class="stretch">
                                            &nbsp;&nbsp;<a href="" class="menulink">Profiili</a>
                                        </td>
                                        <td width="24" height="56" background="static/menu-right.png" class="stretch">
                                        </td>
                                    </tr>
                                </table>
                               
                                <div class="titletext">Uistelupäiväkirja</div>
                            </td>
                            <td align="right">
                                <div id="loginbox"></div>
                            </td>                        
                        </tr>
                    </table>

                </td>
                <td width="28" height="158" class="stretch" background="static/back-top-right.png">

                </td>
            </tr>
            <tr>
                <td class="stretch" background="static/back-mid-left.png">

                </td>
                <td>
                    <table border="1" cellpadding="0" cellspacing="5" width="100%">
                        <tr>
                            <td>

                                <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>

                            </td>
                        </tr>
                    </table>

            </td>
            <td class="stretch" background="static/back-mid-right.png">
            </td>
            </tr>
            <tr>
                <td width="28" class="stretch">
                    <img src="static/back-bottom-left.png" width="100%" height="100%"></img>
                </td>
                <td background="static/back-bottom.png" height="180" class="stretch" valign="top">                   
                    <center>
                        <small><br>Uistelupäiväkirja. Samuli Penttilä. 2011.</small>
                    </center>
                </td>
                <td class="stretch"><img src="static/back-bottom-right.png" width="100%" height="100%"></img></td>
            </tr>
        </table>
        
    </body>
</html>
