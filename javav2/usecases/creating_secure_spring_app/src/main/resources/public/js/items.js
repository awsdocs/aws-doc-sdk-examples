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


function modItem()
{
        var id = $('#id').val();
        var description = $('#description').val();
        var status = $('#status').val();

        if (id == "")
        {
            alert("Please select an item from the table");
            return;
        }

        if (description.length > 350)
        {
            alert("Description has too many characters");
            return;
        }

        if (status.length > 350)
        {
            alert("Status has too many characters");
            return;
        }

        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", loadMods, false);
        xhr.open("POST", "../changewi", true);
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
        xhr.send("id=" + id + "&description=" + description+ "&status=" + status);
    }

function loadMods(event) {

    var msg = event.target.responseText;
    alert("You have successfully modfied item "+msg)

    $('#id').val("");
    $('#description').val("");
    $('#status').val("");

    //Refresh the grid
    GetItems();

}


// Populate the table with work items
function GetItems() {
    var xhr = new XMLHttpRequest();
    var type="active";
    xhr.addEventListener("load", loadItems, false);
    xhr.open("POST", "../retrieve", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("type=" + type);
}

function loadItems(event) {

    // Enable the reportbutton
    $('#reportbutton').prop("disabled",false);
    $('#reportbutton').css("color", "#FFFFFF");

    var xml = event.target.responseText;
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

        //Set the new data
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

}

function ModifyItem() {
    var table = $('#myTable').DataTable();
    var myId="";
    var arr = [];
    $.each(table.rows('.selected').data(), function() {

        var value = this[0];
        myId = value;
    });

    if (myId == "")
    {
        alert("You need to select a row");
        return;
    }

    //Need to check its not an Archive item
    var h3Val =  document.getElementById("info3").innerHTML;
    if (h3Val=="Archive Items")
    {
        alert("You cannot modify an Archived item");
        return;
    }

    // Post to modify
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", onModifyLoad, false);
    xhr.open("POST", "../modify", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");//necessary
    xhr.send("id=" + myId);
}


// Handler for the uploadSave call
function onModifyLoad(event) {

    var xml = event.target.responseText;
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
}


    function Report() {
        var email = $('#manager option:selected').text();

        // Post to report
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", onReport, false);
        xhr.open("POST", "../report", true);
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
        xhr.send("email=" + email);
    }

function onReport(event) {

    var data = event.target.responseText;
    alert(data);
}


function GetArcItems()
{
    var xhr = new XMLHttpRequest();
    var type="archive";
    xhr.addEventListener("load", loadArcItems, false);
    xhr.open("POST", "../retrieve", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("type=" + type);
}

function loadArcItems(event) {

    // Enable the reportbutton
    $('#reportbutton').prop("disabled",true);
    $('#reportbutton').css("color", "#0d010d");

    var xml = event.target.responseText;
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

        //Set the new data
        oTable.fnAddData( [
            id,
            name,
            guide,
            date,
            description,
            status,,]
        );
    });

    document.getElementById("info3").innerHTML = "Archive Items";

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

    if (myId == "")
    {
        alert("You need to select a row");
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", onArch, false);
    xhr.open("POST", "../archive", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");//necessary
    xhr.send("id=" + myId);
}

function onArch(event) {

    var xml = event.target.responseText;
    alert("Item "+xml +" is achived now");
    //Refresh the grid
    GetItems();
}
