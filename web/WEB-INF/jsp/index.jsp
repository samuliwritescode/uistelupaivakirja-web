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
            Uusimmat kalat
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
                <c:forEach var="loop" begin="0" step="1" end="${fishstat.rowCount()-1}">
                    <c:if test="${loop % 2 == 0}"><tr class="tabledataEven"></c:if>
                    <c:if test="${loop % 2 != 0}"><tr class="tabledataOdd"></c:if>
                        
                    <td>
                        ${fishstat.row(loop).get('date')} 
                        ${fishstat.row(loop).get('fish_time')}
                    </td>
                    <td>
                        ${fishstat.row(loop).get('username')}
                    </td>
                    <td>
                        ${fishstat.row(loop).get('fish_species')}
                    </td>
                    <td>
                        ${fishstat.row(loop).get('lure_maker')}
                    </td>
                    <td>
                        ${fishstat.row(loop).get('place_name')}
                    </td>
                    <td>
                        ${fishstat.row(loop).get('fish_weight')}
                    </td>
                    <td>
                        ${fishstat.row(loop).get('fish_length')}
                    </td>
                </tr>
                </c:forEach>                
            </table>
        </div>
    </td>
    <td>

        <div class="ui-widget-header">
            Kookkaimmat kalat
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
                <c:forEach var="loop" begin="0" step="1" end="${fishrecord.rowCount()-1}">
                    <c:if test="${loop % 2 == 0}"><tr class="tabledataEven"></c:if>
                    <c:if test="${loop % 2 != 0}"><tr class="tabledataOdd"></c:if>
                        
                    <td>
                        ${fishrecord.row(loop).get('date')} 
                        ${fishrecord.row(loop).get('fish_time')}
                    </td>
                    <td>
                        ${fishrecord.row(loop).get('username')}
                    </td>
                    <td>
                        ${fishrecord.row(loop).get('fish_species')}
                    </td>
                    <td>
                        ${fishrecord.row(loop).get('fish_weight')}
                    </td>
                    <td>
                        ${fishrecord.row(loop).get('fish_length')}
                    </td>
                    <td>
                        ${fishrecord.row(loop).get('lure_maker')}
                    </td>
                    <td>
                        ${fishrecord.row(loop).get('place_name')}
                    </td>
                </tr>
                </c:forEach>
            </table>
        </div>

    </td></tr>
</table>
<jsp:include page="footer.jsp" />