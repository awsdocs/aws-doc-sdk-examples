$(function() {

 populateChat()


} );

function populateChat() {

    //Post the values to the controller
     var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", handle, false);
    xhr.open("GET", "../populate", true);   
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send();
}

function handle(event) {

    var xml = event.target.responseText;

    $("#messages").children().remove();

    $(xml).find('Message').each(function () {

        var $field = $(this);
        var body = $field.find('Data').text();
        var name = $field.find('User').text();

        //Set the view
        var userText = body +'<br><br><b>' + name  ;

        var myTextNode = $("#base").clone();

        myTextNode.text(userText) ;
        var image_url;

        var n = name.localeCompare("Scott");
        if (n == 0)
            image_url = "../images/av1.png";
        else
            image_url = "../images/av2.png";
        var images_div = "<img src=\"" +image_url+ "\" alt=\"Avatar\" class=\"right\" style=\"\"width:100%;\"\">";

        myTextNode.html(userText) ;
        myTextNode.append(images_div);
        $("#messages").append(myTextNode);
    });

}

function purge() {

    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", purgeItems, false);
    xhr.open("GET", "../purge", true); 
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send();
}

function purgeItems(event) {

    var msg = event.target.responseText;
    alert(msg);
    populateChat();
}


function pushMessage() {

    var user =  $('#username').val();
    var message = $('#textarea').val();

    //Post the values to the controller
    //invokes the getMyForms POST operation
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", loadNewItems, false);
    xhr.open("POST", "../add", true);   
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("user=" + user + "&message=" + message);
}

function loadNewItems(event) {

    var xml = event.target.responseText;

    $("#messages").children().remove();

    $(xml).find('Message').each(function () {

        var $field = $(this);
        var body = $field.find('Data').text();
        var name = $field.find('User').text();

        //Set the view
        var userText = body +'<br><br><b>' + name  ;

        var myTextNode = $("#base").clone();

        myTextNode.text(userText) ;
        var image_url;

        var n = name.localeCompare("Scott");
        if (n == 0)
            image_url = "../images/av1.png";
        else
           image_url = "../images/av2.png";
        var images_div = "<img src=\"" +image_url+ "\" alt=\"Avatar\" class=\"right\" style=\"\"width:100%;\"\">";

        myTextNode.html(userText) ;
        myTextNode.append(images_div);
        $("#messages").append(myTextNode);
    });

}

