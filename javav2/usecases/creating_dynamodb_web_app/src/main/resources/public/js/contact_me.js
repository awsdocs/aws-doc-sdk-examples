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

        $.ajax('/add', {
            type: 'POST',  // http GET method
            data: 'guide=' + guide + '&description=' + description+ '&status=' + status,
            success: function (data, status, xhr) {

                alert("You have successfully added item "+data)
            },
            error: function (jqXhr, textStatus, errorMessage) {
                $('p').append('Error' + errorMessage);
            }
        });

    } );// END of the Send button click
 } );


