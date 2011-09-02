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
<table width="100%" height="300" class="contentwidget" cellspacing="15" border="0">
    <tr>
        <td>            
            <img src="/uistelu/static/howto.png" class="floatimage"></img>
            <div class="normaltext">
            <span class="capital">U</span><span class="highlighttext">istelupäiväkirja webissä jakaa tiedon kalareissuistasi sekä saaliistasi toisille ja mahdollistaa
                puhelinversiolla kirjattujen reissujen synkronoinnin ilman kaapelia verkon yli.</span>
            <p>
            <table id="downloadbox">
                <tr><td>
                    <div id="downloadboxheader" class="ui-widget-header">
                    Lataa PC:lle ja puhelimeen
                    </div>
                    <div class="ui-widget-content">
                        <center>
                        <a href="http://sourceforge.net/projects/uistelu/files/Windows/"><img src="/uistelu/static/windows.png"></img></a>
                        <a href="https://sourceforge.net/projects/uistelu/files/Mac%20OS%20X/"><img src="/uistelu/static/mac.png"></img></a>
                        <a href="https://sourceforge.net/projects/uistelu/files/Source/Desktop/"><img src="/uistelu/static/linux.png"></a>                        
                        <a href="https://sourceforge.net/projects/uistelu/files/Android/"><img src="/uistelu/static/android.png"></img></a>  
                        </center>
                    </div>
                </td></tr>
            </table>
            <p>
                
            Ja näin se toimii: kirjaa saaliisi puhelimella tai PC:llä kirjanpitoon ja
            saaliisi synkronoituvat automaattisesti verkkoon. Muut käyttäjät näkevät uusimmat reissusi ja saaliisi sekä pääset vertailemaan
            ennätyskalojasi muiden kanssa. 

            <p>

            Halutessasi voit estää saalistietojesi näkymisen muille käyttäjille profiilisi yksityisasetuksilla.

            </div>
            



            <div class="twitter">
                <script src="http://widgets.twimg.com/j/2/widget.js"></script>
                <script>
                new TWTR.Widget({     
                  version: 2,
                  type: 'profile',
                  rpp: 9,
                  interval: 6000,
                  width: 'auto',
                  height: 400,
                  theme: {
                    shell: {
                      background: '#E5E5E5',
                      color: '#000000'
                    },
                    tweets: {
                      background: '#F5F5F5',
                      color: '#3b5a4a',
                      links: '#3b5a4a'
                    }
                  },
                  features: {
                    scrollbar: false,
                    loop: false,
                    live: false,
                    hashtags: true,
                    timestamp: true,
                    avatars: false,
                    behavior: 'all'
                  }
                }).render().setUser('capeismi').start();
                </script>                     
            </div>
        </td>
        <td width="500">
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
            <p>
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
            <p>
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
    </tr>
</table>
<jsp:include page="footer.jsp" />