<%-- 
    Document   : menu
    Created on : Aug 14, 2011, 10:56:47 AM
    Author     : Samuli Penttilä <samuli.penttila@gmail.com>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table cellpadding="0" cellspacing="0" border="0">
    <tr>
        <td width="24" height="56" background="/uistelu/static/menu-left.png" class="stretch">
        </td>
        
        <td height="56" background="/uistelu/static/menu<c:if test="${selected eq 'mainpage'}">-selected</c:if>.png" class="stretch">
            &nbsp;&nbsp;<a href="/uistelu/" class="menulink">Pääsivu</a>&nbsp;&nbsp;
        </td>
        <td height="56" background="/uistelu/static/menu<c:if test="${selected eq 'myfish'}">-selected</c:if>.png" class="stretch">
            &nbsp;&nbsp;<a href="/uistelu/myfish/" class="menulink">Omat kalat</a>&nbsp;&nbsp;
        </td>
        <td height="56" background="/uistelu/static/menu<c:if test="${selected eq 'profile'}">-selected</c:if>.png" class="stretch">
            &nbsp;&nbsp;<a href="/uistelu/profile/" class="menulink">Profiili</a>&nbsp;&nbsp;
        </td>
        <td width="24" height="56" background="/uistelu/static/menu-right.png" class="stretch">
        </td>
    </tr>
</table>