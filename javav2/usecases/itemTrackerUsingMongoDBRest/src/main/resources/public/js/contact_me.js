$(function() {

    $("#SendButton" ).click(function($e) {

        var guide = $('#guide').val();
        var description = $('#description').val();
        var status = $('#status').val();

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
    xhr.addEventListener("load", loadNewItems, false);
    xhr.open("POST", "../add", true);   
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("guide=" + guide + "&description=" + description+ "&status=" + status);
} );// END of the Send button click

// Handler for the click SendButton call
function loadNewItems(event) {

    var msg = event.target.responseText;
    alert("You have successfully added item "+msg);

	}

  });