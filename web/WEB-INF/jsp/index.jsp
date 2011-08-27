<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="selected" value="mainpage" scope="request" />
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
</script>
<table width="100%" height="300">
    <tr>
        <td>
            <div id="map_canvas" class="mainwidget"></div>
        </td>
    </tr>
</table>

<table width="100%" cellspacing="5">
    <tr>
    <td valign="top">
        <div class="ui-widget-header">
            Uudet saaliit
        </div>
        <div class="ui-widget-content">
            <table id="mynewest" class="tabledata" cellspacing="0" width="100%">
                <tr class="header">
                    <td>aika</td>
                    <td>saaja</td>
                    <td>laji</td>
                    <td>viehe</td>
                    <td>paikka</td>
                    <td>paino</td>
                    <td>pituus</td>
                </tr>
                <c:forEach var="row" items="${fishstat.table}" varStatus="loop">
                    <tr class="${loop.index%2 == 0?'tabledataEven':'tabledataOdd'}">                        
                    <td>
                        ${row['date']}
                        ${row['fish_time']}
                    </td>
                    <td>
                        ${row['username']}
                    </td>
                    <td>
                        ${row['fish_species']}
                    </td>
                    <td>
                        ${row['lure_maker']}
                    </td>
                    <td>
                        ${row['place_name']}
                    </td>
                    <td>
                        ${row['fish_weight']}
                    </td>
                    <td>
                        ${row['fish_length']}
                    </td>
                </tr>
                </c:forEach>                
            </table>
        </div>
    </td>
    <td>

        <div class="ui-widget-header">
            Kalaennätykset
        </div>
        <div class="ui-widget-content">
            <table class="tabledata" cellspacing="0" width="100%">
                <tr class="header">
                    <td>aika</td>
                    <td>saaja</td>
                    <td>laji</td>
                    <td>paino</td>
                    <td>pituus</td>
                    <td>viehe</td>
                    <td>paikka</td>
                </tr>
                <c:forEach var="row" items="${fishrecord.table}" varStatus="loop">
                    <tr class="${loop.index%2 == 0?'tabledataEven':'tabledataOdd'}">  
                        
                    <td>
                        ${row['date']}
                        ${row['fish_time']}
                    </td>
                    <td>
                        ${row['username']}
                    </td>
                    <td>
                        ${row['fish_species']}
                    </td>
                    <td>
                        ${row['fish_weight']}
                    </td>
                    <td>
                        ${row['fish_length']}
                    </td>
                    <td>
                        ${row['lure_maker']}
                    </td>
                    <td>
                        ${row['place_name']}
                    </td>
                </tr>
                </c:forEach>    
            </table>
        </div>

    </td>
    
        <td>

        <div class="ui-widget-header">
            Uudet reissut
        </div>
        <div class="ui-widget-content">
            <table class="tabledata" cellspacing="0" width="100%">
                <tr class="header">
                    <td>päivä</td>
                    <td>kalamies</td>
                    <td>paikkakunta</td>
                    <td>kaloja</td>
                </tr>
                <c:forEach var="row" items="${tripstat.table}" varStatus="loop">
                    <tr class="${loop.index%2 == 0?'tabledataEven':'tabledataOdd'}">  
                        
                    <td>
                        ${row['date']}
                    </td>
                    <td>
                        ${row['username']}
                    </td>
                    <td>
                        ${row['place_name']}
                    </td>
                    <td>
                        ${row['fish_amount']}
                    </td>
                </tr>
                </c:forEach>    
                
            </table>
        </div>

    </td>
    
    </tr>
</table>
<jsp:include page="footer.jsp" />