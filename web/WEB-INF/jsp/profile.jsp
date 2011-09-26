<%-- 
    Document   : profile
    Created on : Aug 14, 2011, 11:25:16 AM
    Author     : Samuli Penttilä <samuli.penttila@gmail.com>
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="selected" value="profile" scope="request" />
<jsp:include page="header.jsp" />
<jsp:include page="register.jsp" />

<script type="text/javascript">
    $.ajax( {
        type: "GET",
        url: "/uistelu/api/userinfo",
        dataType: "xml",
        success: function(resp) {
            $(resp).find("userinfo").each(function() {
                var user = $(this).find("username").text();
                var publocation = $(this).find("publishlocation").text();
                var pubplace = $(this).find("publishplace").text();
                var publure = $(this).find("publishlure").text();
                var pubfish = $(this).find("publishfish").text();
                var pubtrip = $(this).find("publishtrip").text();
                
                $("#username").text(user);
                $("#publocation").attr("checked", publocation=="true");
                $("#pubplace").attr("checked", pubplace=="true");
                $("#publure").attr("checked", publure=="true");
                $("#pubfish").attr("checked", pubfish=="true");
                $("#pubtrip").attr("checked", pubtrip=="true");
                syncfish();
            });
        }
    });
    
    function syncfish() {
        var publocation = $("#publocation").is(":checked");
        var pubplace = $("#pubplace").is(":checked");
        var publure = $("#publure").is(":checked");
        var pubfish = $("#pubfish").is(":checked");
        var pubtrip = $("#pubtrip").is(":checked");
        
        if(pubfish == false) {
           $("#publocation").attr("checked", false);
           $("#publure").attr("checked", false);
           $("#pubplace").attr("checked", false);
        }
        
        $("#publocation").attr("disabled", !pubfish);
        $("#publure").attr("disabled", !pubfish);
        $("#pubplace").attr("disabled", !pubfish);
    }
    
    function setuserinfo() {
        syncfish();
        var publocation = $("#publocation").is(":checked");
        var pubplace = $("#pubplace").is(":checked");
        var publure = $("#publure").is(":checked");
        var pubfish = $("#pubfish").is(":checked");
        var pubtrip = $("#pubtrip").is(":checked");
                
        startAnimation("#loadericon");
        $.ajax( {
            type: "POST",
            url: "/uistelu/api/userinfo",
            dataType: "xml",
            contentType: "text/xml",
            data: "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\
                    <userinfo>\n\
                        <publishlocation>"+publocation+"</publishlocation>\n\
                        <publishplace>"+pubplace+"</publishplace>\n\
                        <publishtrip>"+pubtrip+"</publishtrip>\n\
                        <publishlure>"+publure+"</publishlure>\n\
                        <publishfish>"+pubfish+"</publishfish>\n\
                    </userinfo>",
            success: function(resp) {
                stopAnimation("#loadericon");
            },

            error: function(response) {
                stopAnimation("#loadericon");
                alert("ei onnistunut: "+response.responseText);
            }
        });
    }
    
    function setpassword() {
        
        var password = $("#pass1").val();
        
        if(password.length == 0) {
            alert("et voi antaa tyhjää salasanaa");
            return;
        }
        
        if($("#pass1").val() != $("#pass2").val()) {
            alert("salasanat eivät täsmää");
            return;
        }
               
        
        startAnimation("#passwordloader");
        $.ajax( {
            type: "POST",
            url: "/uistelu/api/userinfo",
            dataType: "xml",
            contentType: "text/xml",
            data: "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\
                    <userinfo>\n\
                        <plaintextpassword>"+password+"</plaintextpassword>\n\
                    </userinfo>",
            success: function(resp) {
                stopAnimation("#passwordloader");
            },

            error: function(response) {
                stopAnimation("#passwordloader");
            }
        });
    }
</script>

<form>       
    <table cellpadding="10">
        <tr><td>
            <div class="ui-widget-header">
                Yksityisyysasetukset
            </div>
            <div class="ui-widget-content">
                <table>
                    <tr>
                        <td>
                            Näytä kalat
                        </td>
                        <td>
                            <input type="checkbox" id="pubfish" onclick="setuserinfo()"/><br>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>koordinaatit</td>
                        <td>
                            <input type="checkbox" id="publocation" onclick="setuserinfo()"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>viehetiedot</td>
                        <td>
                            <input type="checkbox" id="publure" onclick="setuserinfo()"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>kalapaikat</td>
                        <td>
                            <input type="checkbox" id="pubplace" onclick="setuserinfo()"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Näytä reissut
                        </td>
                        <td>                            
                            <input type="checkbox" id="pubtrip" onclick="setuserinfo()"/>
                        </td>
                    </tr>
                </table>
                <span id="loadericon"/>
            </div>
        </td></tr>
    </table>
    
    <table cellpadding="10">
        <tr><td>
            <div class="ui-widget-header">
                Salasanan vaihto
            </div>
            <div class="ui-widget-content">
                <table>
                    <tr>
                        <td>
                            uusi salasana
                        </td>
                        <td>
                            <input id="pass1" type="password"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            salasana uudelleen
                        </td>
                        <td>
                            <input id="pass2" type="password"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="button" value="vaihda" onclick="setpassword()"/>
                        </td>
                        <td>
                            <span id="passwordloader"/>
                        </td>
                    </tr>
                </table>
            </div>
        </td></tr>
    </table>
</form>


<jsp:include page="footer.jsp" />
