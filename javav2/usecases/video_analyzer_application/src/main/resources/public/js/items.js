$(function() {

    $('#myTable').DataTable( {
        scrollY:        "500px",
        scrollX:        true,
        scrollCollapse: true,
        paging:         true,
        columnDefs: [
            { width: 200, targets: 0 }
        ],
        fixedColumns: true
    } );


} );


function getVideo() {

    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", handlevideo, false);
    xhr.open("GET", "../getvideo", true);
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send();
}

function handlevideo() {

    var xml = event.target.responseText;
    var oTable = $('#myTable').dataTable();
    oTable.fnClearTable(true);
    $(xml).find('Item').each(function () {

        var $field = $(this);
        var key = $field.find('Key').text();
        var name = $field.find('Owner').text();
        var date = $field.find('Date').text();
        var size = $field.find('Size').text();

        //Set the new data
        oTable.fnAddData( [
            key,
            name,
            date,
            size,,]
        );
    });
}
