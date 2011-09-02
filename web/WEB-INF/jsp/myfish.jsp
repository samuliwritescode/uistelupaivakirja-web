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
    

$.ajax({
  type: "GET",
  url: "/uistelu/api/views/fishmap",
  dataType: "xml",
  success: function(resp) {
    var myLatlng = new google.maps.LatLng(62, 25);
    var myOptions = {
      zoom: 6,
      center: myLatlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

    $(resp).find("wpt").each(function() {

      var lat = $(this).attr("lat");
      var lon = $(this).attr("lon");
      var title = $(this).find("name").text();
      title += "/";
      title += $(this).find("time").text();
      var pos = new google.maps.LatLng(lat, lon);
      var marker = new google.maps.Marker({
          position: pos, 
          map: map,
          title: title
      });
      });          
    } 
});


createTable("/uistelu/api/views/fishstat", 
    "mynewest", 
    "fish",
    ["time", "species","luremaker", "place", "weight", "length"]
);

createTable("/uistelu/api/views/fishrecord", 
    "myrecord", 
    "fish",
    ["time", "species", "weight", "length", "luremaker", "place" ]
);

createTable("/uistelu/api/views/tripstat", 
    "mytrips", 
    "tripstat",
    ["date", "place_name", "fish_amount"]
);
    
  </script>
  
  
   <table width="100%" border="0">
        <tr><td  height="700">
            <div id="map_canvas" class="mapwidget"></div>
        </td>
        <td valign="top" width="450">
            <div class="ui-widget-header">
                Uusimmat kalani
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
            <br>
            <div class="ui-widget-header">
                Kookkaimmat kalani
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
            
            <br>
            <div class="ui-widget-header">
                Uusimmat reissuni
            </div>
            <div class="ui-widget-content">
                <table id="mytrips" class="tabledata" cellspacing="0" width="100%">
                    <tr class="header">
                        <td>aika</td>
                        <td>paikka</td>
                        <td>kaloja</td>
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
