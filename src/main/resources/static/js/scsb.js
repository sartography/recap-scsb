/**
 * Created by SheikS on 6/20/2016.
 */
var intervalID;

jQuery(document).ready(function ($) {
    $('#dateFrom, #dateTo').datepicker({
        format: "yyyy/mm/dd"
    });
    $("#reports-form").submit(function (event) {
        event.preventDefault();
        generateReport();
    });
});

function generateReport() {
    var $form = $('#reports-form');
    $("#report").attr('disabled', 'disabled');
    document.getElementById("reportStatus").value = '';
    var request = $.ajax({
        url: $form.attr('action'),
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $("#report").removeAttr('disabled');
        }
    });
    request.done(function (msg) {
        document.getElementById("reportStatus").value = msg;
    })
}
