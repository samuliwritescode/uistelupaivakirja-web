/* 
 * Copyright (C) 2011 Samuli Penttilä <samuli.penttila@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function createTable(url, renderTo, root, columns) {
    $.ajax( {
        type: "GET",
        url: url,
        dataType: "xml",
        success: function(resp) {
            var newlist = document.getElementById(renderTo);

            var loop=0;
            $(resp).find(root).each(function(){
                loop++;

                var tr = document.createElement("tr");
                if(loop%2 == 0) {
                    tr.setAttribute("class", "tabledataEven");
                }
                else {
                    tr.setAttribute("class", "tabledataOdd");
                }

                for(var hdr in columns) {
                    var td = document.createElement("td");                          
                      var data = $(this).find(columns[hdr]).text();
                      if(columns[hdr] == "time") {
                          data = new Date(data).toLocaleDateString();
                      }
                      td.appendChild(document.createTextNode(data));
                      tr.appendChild(td);
                  }

                newlist.appendChild(tr);                   
            });

        }
    });
}

function loginbox() {
    var loginBox = document.getElementById("loginbox");
    var form = document.createElement("form");
    var username = document.createElement("input");
    var password = document.createElement("input");
    var submit = document.createElement("input");
    username.setAttribute("name", "j_username");
    password.setAttribute("name", "j_password");
    submit.setAttribute("type", "submit");
    submit.setAttribute("value", "kirjaudu");
    form.setAttribute("method", "get");
    form.setAttribute("action", "/uistelu/api/login");
    form.appendChild(username);
    form.appendChild(password);
    form.appendChild(submit);
    loginBox.appendChild(form); 
}

function logoutbox() {
  var loginBox = document.getElementById("loginbox");
  var link = document.createElement("A");
  link.setAttribute("href", "/uistelu/api/logout");
  link.appendChild(document.createTextNode("logout"));
  loginBox.appendChild(link);
}