$(window).bind('beforeunload', function () {
    return 'Are you sure you want to leave?';
});
var enviado = false;
$(document).ready(function () {

    var dni = sessionStorage.getItem("_DNI");
    var examID = sessionStorage.getItem("_EXAM");

    if (dni == null && examID == null) {
        window.location = "index.html";
    } else {
        getExamen();
    }

    $("#contExam").submit(function () {

        $("#modal").modal("show");
        return false;
    });
    $("#save").click(function () {
        if (!enviado) {
            $("#modalIn").empty();
            $("#modalIn").append($("<img id='loadingsmall' style='display:none;'  height='42' width='42' src='img/smallload.gif' alt='Loading...' />"));
            $("#loadingsmall").fadeIn("slow");
            $("#save").fadeOut("slow");
            enviado = true;
            enviar();
        } else {
            window.location = "resultados.html";
        }
    });
});





function enviar() {
    var dni = sessionStorage.getItem("_DNI");
    var examID = sessionStorage.getItem("_EXAM");
    var emess = "upss";
    var ans_1 = $('input[name="q_01"]:checked').val();
    var ans_2 = $('input[name="q_02"]:checked').val();
    var ans_3 = $("#q_03q_03").val();
    var ans_4 = $("#q_04q_04").val();
    var ans_5 = $('input[name="q_05"]:checked').map(function () {
        return this.value;
    }).get();
    var ans_6 = $('input[name="q_06"]:checked').map(function () {
        return this.value;
    }).get();
    var ans_7 = $("#q_07q_07 option:selected").text();
    var ans_8 = $("#q_08q_08 option:selected").text();
    ;
    var ans_9 = $("select#q_09q_09").val();
    var ans_10 = $("select#q_10q_10").val();

    $.ajax({
        type: "POST",
        url: "PostAnswer",
        data: {dni: dni, examen: examID, q_01: ans_1, q_02: ans_2, q_03: ans_3, q_04: ans_4, q_05: ans_5, q_06: ans_6, q_07: ans_7, q_08: ans_8, q_09: ans_9, q_10: ans_10},
        // data: {dni: dni, examen: examen, answers:[ans_1,  ans_2,  ans_3, ans_4,  ans_5, ans_6,  ans_7,  ans_8, ans_9,  ans_10]},
        success: function (rsp) {
            sessionStorage.setItem("_HASH", rsp.hash);
            $("#modalIn").empty();
            $("#modalIn").append($("<h3>Su clave de correción es: " + rsp.hash + "</h3>"));
            $("#save").text("Ver resultados");
            $("#save").show();

        },
        error: function (e) {
            $("#modalIn").empty();
            $("#modalIn").append($("<h3 style='color: red;';>Ha ocurrido un error</h3>"));
        }
    });
}
function getExamen() {
    var emess = "There was an error, please contact the developers!";
    var dni = sessionStorage.getItem("_DNI");
    var examID = sessionStorage.getItem("_EXAM");
    $.ajax({
        type: "POST",
        url: "GetExamen",
        data: {examID: examID},
        success: function (rsp) {
            $("#loading").fadeIn(1000);
            insertarPreguntas(rsp);
            //insertar boton
            $("#loading").hide();
            $("#demo").fadeIn("slow");
        },
        error: function (e) {
            if (e["responseJSON"] === undefined)
                alert(emess);
            else
                alert(e["responseJSON"]["error"]);
        }
    });
}

function insertarPreguntas(json) {
    $.each(json.question, function (i, pregunta) {
        switch (pregunta.type) {
            case "radio": //metodo
                insertarRadio(pregunta);
                break;
            case "text": //metodo
                insertarText(pregunta);
                break;
            case "checkbox": //metodo
                insertarCheck(pregunta);
                break;
            case "select": //metodo
                insertarSelect(pregunta);
                break;
            case "multiple": //metodo
                insertarMultiple(pregunta);
                break;
        }

    });
}
function insertarRadio(json) {
    var $div = $("<div id='" + json.id + "'/>").addClass("divRadio");
    $div.append($("<h3>" + json.title + "</h3>"));
    var $row = $("<div/>").addClass("row");
    var $col1 = $("<div/>").addClass("col-md-4");
    var $col2 = $("<div/>").addClass("col-md-4");
    var $col3 = $("<div/>").addClass("col-md-4");
    $row.append($col1);
    $.each(json.option, function (i, item) {
        $col2.append($("<label><input type='radio' name='" + json.id + "' required value='" + item + "'>" + item + "</label><br>"));
    });
    $row.append($col2);
    $row.append($col3);
    $div.append($row);
    $("#contExam").append($div);
}

function insertarText(json) {
    var $div = $("<div id='" + json.id + "'/>").addClass("divRadio text-center");
    $div.append($("<h3>" + json.title + "</h3>"));
    $div.append($("<input type='text' id='" + json.id + json.id + "' name='" + json.id + "' required>"));
    $("#contExam").append($div);
}
function insertarCheck(json) {
    var $div = $("<div id='" + json.id + "'/>").addClass("divRadio");
    var $row = $("<div/>").addClass("row");
    var $col1 = $("<div/>").addClass("col-md-4");
    var $col2 = $("<div/>").addClass("col-md-4");
    var $col3 = $("<div/>").addClass("col-md-4");
    $row.append($col1);
    $div.append($("<h3>" + json.title + "</h3>"));
    $.each(json.option, function (i, item) {
        $col2.append($("<label><input type='checkbox' name='" + json.id + "' value='" + item + "'>" + item + "</label><br>"));
    });
    $row.append($col2);
    $row.append($col3);
    $div.append($row);
    $("#contExam").append($div);

}

function insertarSelect(json) {
    var $div = $("<div id='" + json.id + "'/>").addClass("divRadio text-center");
    $div.append($("<h3>" + json.title + "</h3>"));
    var $sel = $("<select required id='" + json.id + json.id + "'/>");
    $sel.append($("<option value=''>Selecciona alguna..</option>"));
    $.each(json.option, function (i, item) {
        $sel.append($("<option value='" + item + "'>" + item + "</option>"));
    });
    $div.append($sel);
    $("#contExam").append($div);
}

function insertarMultiple(json) {
    var $div = $("<div id='" + json.id + "'/>").addClass("divRadio text-center");
    $div.append($("<h3>" + json.title + "</h3>"));
    var $sel = $("<select multiple id='" + json.id + json.id + "' required/>");
    $.each(json.option, function (i, item) {
        $sel.append($("<option value='" + item + "'>" + item + "</option>"));
    });
    $div.append($sel);
    $("#contExam").append($div);
}