$(function() {

} );

function ProcessImages() {

    //Post the values to the controller
    var email =  $('#email').val();

    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", handle, false);
    xhr.open("POST", "../report", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("email=" + email);
}

function handle(event) {

    var res = event.target.responseText;
    alert(res) ;
}

function DownloadImage(){

    //Post the values to the controller
    var photo =  $('#photo').val();
    window.location="../downloadphoto?photoKey=" + photo ;
}



