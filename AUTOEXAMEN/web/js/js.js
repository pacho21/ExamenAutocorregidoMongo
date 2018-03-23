/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    $("#dniForm").submit(function () {
        var dni = $("#inpDNI").val();
        var exam = $('input[name="examenes"]:checked').val();
        sessionStorage.setItem("_DNI", dni);
        sessionStorage.setItem("_EXAM", exam);
        window.location = "examen.html";
        return false;
    });

    $("#encryptForm").submit(function () {
        $("#clave").empty();
        getDniEncrypted();
        return false;
    });

    $("#verResultados").click(function () { //al hacer click en ver resultados -> enviar a  -> resultados
        window.location = "resultados.html";
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




