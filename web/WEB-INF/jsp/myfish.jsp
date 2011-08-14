<%-- 
    Document   : myfish
    Created on : Aug 14, 2011, 11:25:12 AM
    Author     : Samuli Penttilä <samuli.penttila@gmail.com>
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="selected" value="myfish" scope="request" />
<jsp:include page="header.jsp" />

    <script type="text/javascript">
    function initialize() {
        var latlng = new google.maps.LatLng(62, 25);
        var myOptions = {
          zoom: 8,
          center: latlng,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var map = new google.maps.Map(document.getElementById("map_canvas"),
            myOptions);
    }
    

      
    var req = createRequest();
    // Create the callback:
    req.onreadystatechange = function() {
      if (req.readyState != 4) return; // Not there yet
      if (req.status != 200) {
        // Handle request failure here...
        loginbox();
        return;
      }
      // Request successful, read the response

      var myLatlng = new google.maps.LatLng(62, 25);
      var myOptions = {
        zoom: 8,
        center: myLatlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
      }

      var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

      var resp = req.responseXML;
      for(var loop=0; loop < resp.getElementsByTagName("wpt").length; loop++) {
        var node = resp.getElementsByTagName("wpt")[loop];

        var lat = node.attributes["lat"].nodeValue;
        var lon = node.attributes["lon"].nodeValue;
        var title = node.getElementsByTagName("name")[0].childNodes[0].nodeValue;
        title += "/";
        title += node.getElementsByTagName("time")[0].childNodes[0].nodeValue;
        var pos = new google.maps.LatLng(lat, lon);
        var marker = new google.maps.Marker({
            position: pos, 
            map: map,
            title: title
        }); 
      }
      logoutbox();
    }
    req.open("GET", "/uistelu/api/fishmap",  true); 
    req.send(null); 
    
    
    var reqStats = createRequest();
    reqStats.onreadystatechange = function() {
      if (reqStats.readyState != 4) return;
      if (reqStats.status != 200) {
        return;
      }
      var resp = reqStats.responseXML;
      var newlist = document.getElementById("mynewest");

      for(var loop=0; loop < resp.getElementsByTagName("fish").length; loop++) {
          var node = resp.getElementsByTagName("fish")[loop];
          
          var luremaker = getNodeText(node.getElementsByTagName("luremaker"));
          var place = getNodeText(node.getElementsByTagName("place"));
          var time = new Date(getNodeText(node.getElementsByTagName("time")));
          var weight = getNodeText(node.getElementsByTagName("weight"));
          var length = getNodeText(node.getElementsByTagName("length"));
          var tr = document.createElement("tr");
          if(loop%2 == 0) {
            tr.setAttribute("class", "tabledataEven");
          }
          else {
            tr.setAttribute("class", "tabledataOdd");
          }
          var tdTime = document.createElement("td");
          var tdLure = document.createElement("td");
          var tdPlace = document.createElement("td");
          var tdWeight = document.createElement("td");
          var tdLength = document.createElement("td");
          tdTime.appendChild(document.createTextNode(time.toLocaleDateString()));
          tdLure.appendChild(document.createTextNode(luremaker));
          tdPlace.appendChild(document.createTextNode(place));
          tdLength.appendChild(document.createTextNode(length));
          tdWeight.appendChild(document.createTextNode(weight));
          
          tr.appendChild(tdTime);
          tr.appendChild(tdLure);
          tr.appendChild(tdPlace);
          tr.appendChild(tdWeight);
          tr.appendChild(tdLength);
          newlist.appendChild(tr);
      }
      
    }
    reqStats.open("GET", "/uistelu/api/fishstat",  true); 
    reqStats.send(null);
    
    function getNodeText(node) {
        if(node == null) {
            return "-";
        }
        if(node.length == 0)
            return "-";
        
        return node[0].childNodes[0].nodeValue;
    }
    
  </script>
  
  
   <table width="100%">
        <tr><td width="75%" height="500">
            <div id="map_canvas" class="mainwidget"></div>
        </td>
        <td valign="top">
            <div class="ui-widget-header">
                Uusimmat kalani
            </div>
            <div class="ui-widget-content">
                <table id="mynewest" class="tabledata" cellspacing="0" width="100%">
                    <tr class="header">
                        <td >aika</td>
                        <td>viehe</td>
                        <td>paikka</td>
                        <td>paino</td>
                        <td>pituus</td>
                    </tr>
                </table>
            </div>
            <!--
            <div style="position: absolute; width: 400px; height: 200px;left: 35%; top: 100px; padding: 10px;" class="ui-widget ui-widget-content ui-corner-all">
                <div class="ui-dialog-content ui-widget-content" style="background: none; border: 0;">
                    <form action="/uistelu/api/login" method="get">
                        <input name="j_username"></input>
                        <input name="j_password" type="password"></input>
                        <input type="submit" value="kirjaudu"></input>
                    </form>
                    Ole hyvä ja kirjaudu sisään tai rekisteröidy.
                </div>
            </div>-->
        </td></tr>
    </table>
  
<jsp:include page="footer.jsp" />
