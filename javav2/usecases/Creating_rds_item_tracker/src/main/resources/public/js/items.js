$(function() {

    $( "#dialogtemplate2" ).dialog();

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

    var table = $('#myTable').DataTable();
    $('#myTable tbody').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }
        else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    } );


    // Disable the reportbutton
    $('#reportbutton').prop("disabled",true);
    $('#reportbutton').css("color", "#0d010d");

});


function modItem() {
        var id = $('#id').val();
        var description = $('#description').val();
        var status = $('#status').val();

        if (id == "") {
            alert("Please select an item from the table");
            return;
        }

        if (description.length > 350) {
            alert("Description has too many characters");
            return;
        }

        if (status.length > 350) {
            alert("Status has too many characters");
            return;
        }

    $.ajax('/changewi', {
        type: 'POST',
        data: 'id=' + id + '&description=' + description+ '&status=' + status,
        success: function (data, status, xhr) {

            var msg = event.target.responseText;
            alert("You have successfully modfied item "+msg)

            $('#id').val("");
            $('#description').val("");
            $('#status').val("");

            //Refresh the grid.
            GetItems();

        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}


// Populate the table with work items.
function GetItems() {
    var xhr = new XMLHttpRequest();
    var type="active";

    $.ajax('/retrieve', {
        type: 'POST',
        data: 'type=' + type,
        success: function (data, status, xhr) {

            // Enable the buttons.
            $('#singlebutton').prop("disabled",false);
            $('#updatebutton').prop("disabled",false);
            $('#reportbutton').prop("disabled",false);
            $('#reportbutton').css("color", "#FFFFFF");
            $('#singlebutton').css("color", "#FFFFFF");
            $('#updatebutton').css("color", "#FFFFFF");
            $('#archive').prop("disabled",false);
            $('#archive').css("color", "#FFFFFF");

            $("#modform").show();

            var xml = data;
            var oTable = $('#myTable').dataTable();
            oTable.fnClearTable(true);

            $(xml).find('Item').each(function () {

                var $field = $(this);
                var id = $field.find('Id').text();
                var name = $field.find('Name').text();
                var guide = $field.find('Guide').text();
                var date = $field.find('Date').text();
                var description = $field.find('Description').text();
                var status = $field.find('Status').text();

                //Set the new data.
                oTable.fnAddData( [
                    id,
                    name,
                    guide,
                    date,
                    description,
                    status,,]
                );
            });

            document.getElementById("info3").innerHTML = "Active Items";

        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}

function ModifyItem() {
    var table = $('#myTable').DataTable();
    var myId="";
    var arr = [];
    $.each(table.rows('.selected').data(), function() {

        var value = this[0];
        myId = value;
    });

    if (myId == "") {
        alert("You need to select a row");
        return;
    }

    //Need to check its not an Archive item.
    var h3Val =  document.getElementById("info3").innerHTML;
    if (h3Val=="Archive Items") {
        alert("You cannot modify an Archived item");
        return;
    }

    $.ajax('/modify', {
        type: 'POST',
        data: 'id=' + myId,
        success: function (data, status, xhr) {

            var xml = data;
            $(xml).find('Item').each(function () {

                var $field = $(this);
                var id = $field.find('Id').text();
                var description = $field.find('Description').text();
                var status = $field.find('Status').text();

                //Set the fields
                $('#id').val(id);
                $('#description').val(description);
                $('#status').val(status);
            });
        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}

function Report() {
   var email = $('#manager option:selected').text();
   $.ajax('/report', {
        type: 'POST',
        data: 'email=' + email,
        success: function (data, status, xhr) {
            alert(data);

        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}


function GetArcItems()
{
    var type="archive";
    $.ajax('/retrieve', {
        type: 'POST',
        data: 'type=' + type,
        success: function (data, status, xhr) {
            // Disable buttons
            $('#reportbutton').prop("disabled", true);
            $('#reportbutton').css("color", "#0d010d");
            $('#singlebutton').prop("disabled", true);
            $('#singlebutton').css("color", "#0d010d");
            $('#updatebutton').prop("disabled", true);
            $('#updatebutton').css("color", "#0d010d");
            $('#archive').prop("disabled", true);
            $('#archive').css("color", "#0d010d");


            $("#modform").hide();

            var xml = data;
            var oTable = $('#myTable').dataTable();
            oTable.fnClearTable(true);

            $(xml).find('Item').each(function () {

                var $field = $(this);
                var id = $field.find('Id').text();
                var name = $field.find('Name').text();
                var guide = $field.find('Guide').text();
                var date = $field.find('Date').text();
                var description = $field.find('Description').text();
                var status = $field.find('Status').text();

                //Set the new data.
                oTable.fnAddData([
                    id,
                    name,
                    guide,
                    date,
                    description,
                    status, ,]
                );
            });

            document.getElementById("info3").innerHTML = "Archive Items";

        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}

function archiveItem()
{
    var table = $('#myTable').DataTable();
    var myId="";
    var arr = [];
    $.each(table.rows('.selected').data(), function() {
        var value = this[0];
        myId = value;
    });

    if (myId == "") {
        alert("You need to select a row");
        return;
    }

    $.ajax('/archive', {
        type: 'POST',
        data: 'id=' + myId,
        success: function (data, status, xhr) {
            alert("Item " + data + " is achived now");
            //Refresh the grid
            GetItems();

        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}
