$(function() {



    $("#SendButton" ).click(function($e) {

        var guide = $('#guide').val();
        var description = $('#description').val();
        var status = $('#status').val();

        //var description = $("textarea#description").val();
        if (description.length > 350)
        {
            alert("Description has too many characters");
            return;
        }

        //var status = $("textarea#status").val();
        if (status.length > 350)
        {
            alert("Status has too many characters");
            return;
        }

        //invokes the getMyForms POST operation
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", loadNewItems, false);
        xhr.open("POST", "../add", true);   //buildFormit -- a Spring MVC controller
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
        xhr.send("guide=" + guide + "&description=" + description+ "&status=" + status);
    } );// END of the Send button click

    //Handler for the uploadSave call
    //This will populate the Data Table widget
    function loadNewItems(event) {

        var msg = event.target.responseText;
        alert("You have successfully added item "+msg)

    }

   } );



  function getDataValue()
  {
    var radioValue = $("input[name='optradio']:checked").val();
   return radioValue;
  }







