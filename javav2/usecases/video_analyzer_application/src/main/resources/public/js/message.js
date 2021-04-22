$(function() {

    $('#spinner').hide();

} );

function ProcessImages() {

    //Post the values to the controller
    var email =  $('#email').val();
    $('#spinner').show();
    $('#button').prop("disabled",true);


    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", handle, false);
    xhr.open("POST", "../report", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("email=" + email);
}

function handle(event) {

    var res = event.target.responseText;
    $('#spinner').hide();
    $('#button').prop("disabled",false);
    alert(res) ;
}


