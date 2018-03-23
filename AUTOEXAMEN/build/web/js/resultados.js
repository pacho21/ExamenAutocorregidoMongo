/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function () {
    var hash = sessionStorage.getItem("_HASH");
    getResultados();
     $("#volver").click(function () { //al hacer click en ver resultados -> enviar a  -> resultados
        window.location = "index.html";
    });
      $("#encryptForm").submit(function () {
        $("#clave").empty();
        getDniEncrypted();
        getResultados();
        return false;
    });

});
function getDniEncrypted() {
    var enDni = $("#encryptDNI").val();
    $.ajax({
        type: "POST",
        url: "GetEncrypt",
        data: {dni: enDni},
        success: function (rsp) {
            sessionStorage.setItem("_HASH", rsp.dni);
            $("#clave").hide();
            $("#clave").append($("<h3>" + rsp.dni + "</h3>"));
            $("#clave").fadeIn("slow");

        },
        error: function (e) {
            alert("Ha ocurrido un error!");
        }
    });
}

function getResultados() {
    var hash = sessionStorage.getItem("_HASH");
    $.ajax({
        method: "GET",
        url: "GetListado",
        data: {},
        success: function (jsn) {
            $("#notas").empty();
            $.each(jsn, function (i, item) {
                var DNI = item.dni;
                var tipo = item.exam;
                var nota = item.nota;
                if (hash == item.dni) {
                    if (nota > 5) {
                        var row = "<tr style='font-weight: bold;' class='success'><td>" + DNI + "</td><td>" + tipo + "</td><td>" + nota.toFixed(1) + "</td></tr>";
                    } else {
                        var row = "<tr style='font-weight: bold;' class='danger'><td>" + DNI + "</td><td>" + tipo + "</td><td>" + nota.toFixed(1) + "</td></tr>";
                    }
                } else {
                    var row = "<tr><td>" + DNI + "</td><td>" + tipo + "</td><td>" + nota.toFixed(1) + "</td></tr>";
                }

                $("#notas").append(row);

            });
            sortTable();
        },
        error: function (e) {
            alert("ups");
        }
    });
}
function sortTable() {
    var table, rows, switching, i, x, y, shouldSwitch;
    table = document.getElementById("myTable");
    switching = true;
    /*Make a loop that will continue until
     no switching has been done:*/
    while (switching) {
        //start by saying: no switching is done:
        switching = false;
        rows = table.getElementsByTagName("TR");
        /*Loop through all table rows (except the
         first, which contains table headers):*/
        for (i = 1; i < (rows.length - 1); i++) {
            //start by saying there should be no switching:
            shouldSwitch = false;
            /*Get the two elements you want to compare,
             one from current row and one from the next:*/
            x = rows[i].getElementsByTagName("TD")[0];
            y = rows[i + 1].getElementsByTagName("TD")[0];
            //check if the two rows should switch place:
            if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                //if so, mark as a switch and break the loop:
                shouldSwitch = true;
                break;
            }
        }
        if (shouldSwitch) {
            /*If a switch has been marked, make the switch
             and mark that a switch has been done:*/
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
        }
    }
}

function myFunction() {
  var input, filter, table, tr, td, i;
  input = document.getElementById("myInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("myTable");
  tr = table.getElementsByTagName("tr");
  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[0];
    if (td) {
      if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      } else {
        tr[i].style.display = "none";
      }
    }       
  }
}