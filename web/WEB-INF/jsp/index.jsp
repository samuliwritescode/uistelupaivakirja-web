<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="selected" value="mainpage" scope="request" />
<jsp:include page="header.jsp" />
<table width="100%">
    <td valign="top">
        <div class="ui-widget-header">
            Uusimmat kalat
        </div>
        <div class="ui-widget-content">
            <table id="mynewest" class="tabledata" cellspacing="0" width="100%">
                <tr class="header">
                    <td>aika</td>
                    <td>laji</td>
                    <td>viehe</td>
                    <td>paikka</td>
                    <td>paino</td>
                    <td>pituus</td>
                </tr>
            </table>
        </div>
    </td>
    <td>
        <c:out value="${fishrecord.name}"></c:out>
        <div class="ui-widget-header">
            Kookkaimmat kalat
        </div>
        <div class="ui-widget-content">
            <table id="myrecord" class="tabledata" cellspacing="0" width="100%">
                <tr class="header">
                    <td>aika</td>
                    <td>laji</td>
                    <td>paino</td>
                    <td>pituus</td>
                    <td>viehe</td>
                    <td>paikka</td>
                </tr>
            </table>
        </div>

    </td></tr>
</table>
<jsp:include page="footer.jsp" />