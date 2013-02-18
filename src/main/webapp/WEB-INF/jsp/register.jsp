<%-- 
    Document   : register
    Created on : Sep 3, 2011, 5:22:10 PM
    Author     : Samuli Penttilä <samuli.penttila@gmail.com>
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    function register() {
        var username = $("#newusername").val();
        var pass1 = $("#newpassword").val();
        var pass2 = $("#newpasswordagain").val();
        if(username.length == 0) {
            registerError("Käyttäjätunnus täytyy antaa");
            return;
        }

        if(pass1.length == 0) {
            registerError("Salasana täytyy antaa");
            return;
        }

        if(pass1 != pass2) {
            registerError("Syöttämäsi salasanat eivät täsmää.");
            return;
        }

        sendRegistration(username, pass1);
        //registerError("Käyttäjätunnus on jo olemassa");
    }

    function registerError(error) {
        $(".contentwidget").css("visibility", "hidden");
        $("#registerbox").css("visibility", "visible");
        $("#registererror").text(error);
        
    }
    
    function sendRegistration(username, password) {
        $.ajax( {
            type: "PUT",
            url: "/uistelu/api/userinfo",
            dataType: "xml",
            contentType: "text/xml",
            data: "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\
                    <userinfo>\n\
                        <username>"+username+"</username>\n\
                        <plaintextpassword>"+password+"</plaintextpassword>\n\
                        <publishlocation>true</publishlocation>\n\
                        <publishplace>true</publishplace>\n\
                        <publishtrip>true</publishtrip>\n\
                        <publishlure>true</publishlure>\n\
                        <publishfish>true</publishfish>\n\
                    </userinfo>",
            success: function(resp) {
                $("#j_username").val(username);
                $("#j_password").val(password);
                login();
            },

            error: function(response) {
                alert("ei onnistunut"+response.responseText);
            }
        });
    }
</script>

<div id="registerbox">
    <div style="position: absolute; width: 400px; left: 35%; top: 300px; padding: 10px;" class="ui-widget ui-widget-content ui-corner-all">
        <div class="ui-dialog-content ui-widget-content" style="background: none; border: 0;">
            <div class="ui-widget" id="registererrorbox">
                <div class="ui-state-error ui-corner-all" style="padding: 0 .7em;"> 
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                     <strong>Ups:</strong>
                     <span id="registererror"></span>
                    </p>
                </div>
            </div>
            <br>
            <div class="ui-widget">
                <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;"> 
                    <p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                    Luodaksesi profiilin annat vain tunnuksen ja salasanan. Muut asetukset voit myöhemmin asettaa profiilistasi.</p>

                    <small>
                    <form action="/uistelu/api/login" method="get">
                        <input id="newusername">käyttäjätunnus</input>
                        <input id="newpassword" type="password">salasana</input>
                        <input id="newpasswordagain" type="password">salasana uudelleen</input><br>
                        <input type="button" onClick="register()" value="rekisteröidy"></input>
                    </form>
                    </small>  
                </div>
            </div>
        </div>
    </div>
</div>
