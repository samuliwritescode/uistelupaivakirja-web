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
                
                $("#username").text(user);
                $("#publocation").attr("checked", publocation=="true");
                $("#pubplace").attr("checked", pubplace=="true");
                $("#publure").attr("checked", publure=="true");
                $("#pubfish").attr("checked", pubfish=="true");
            });
        }
    });
    
    function setuserinfo() {
        var publocation = $("#publocation").is(":checked");
        var pubplace = $("#pubplace").is(":checked");
        $.ajax( {
            type: "POST",
            url: "/uistelu/api/userinfo",
            dataType: "xml",
            contentType: "text/xml",
            data: "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\
                    <userinfo>\n\
                        <publishlocation>"+publocation+"</publishlocation>\n\
                        <publishplace>"+pubplace+"</publishplace>\n\
                    </userinfo>",
            success: function(resp) {
            },

            error: function(response) {
                alert("ei onnistunut: "+response.responseText);
            }
        });
    }
</script>

<form>
    <span id="username">
    </span>
    <br>
    Julkaise kalapaikkani koordinaatit
    <input type="checkbox" id="publocation">
    </input>
    <br>
    Julkaise kalapaikkani
    <input type="checkbox" id="pubplace">
    </input>
    
    <input value="päivitä" type="button" onclick="setuserinfo()"></input>
</form>
<jsp:include page="footer.jsp" />
