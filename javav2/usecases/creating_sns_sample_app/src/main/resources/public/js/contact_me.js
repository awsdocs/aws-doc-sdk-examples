/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

$(function() {
    $("#SendButton" ).click(function($e) {

        var body = $('#body').val();
        if (body == '' ){
            alert("Please enter text");
            return;
        }

        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", handleMsg, false);
        xhr.open("POST", "../addMessage", true);   //buildFormit -- a Spring MVC controller
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
        xhr.send("body=" + body );
    } );// END of the Send button click

    function handleMsg(event) {
        var msg = event.target.responseText;
        alert(msg)
        $('#body').val("");

    }
} );

function subEmail(){
    var mail = $('#inputEmail1').val();
    var result = validate(mail)
    if (result == false) {
        alert (mail + " is not valid. Please specify a valid email");
        return;
    }

    // Valid email, post to the server
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", loadItems, false);
    xhr.open("POST", "../addEmail", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("email=" + mail );
 }

function loadItems(event) {

    var subNum = event.target.responseText;
    alert("Subscription validation is "+subNum);
}

function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function validate(email) {
    const $result = $("#result");

    if (validateEmail(email)) {
        return true ;
    } else {
        return false ;
    }
}

function subDelete() {

    $("#myModal").modal();
}

function getSubs(){

    // Valid email, post to the server
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", loadSubs, false);
    xhr.open("GET", "../getSubs", true);
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send();
}

function loadSubs(event) {

    $('.modal-body').empty();
    var xml = event.target.responseText;
    $(xml).find('Sub').each(function ()  {

        var $field = $(this);
        var email = $field.find('email').text();

        // Append this data to the main list.
        $('.modal-body').append("<p><b>"+email+"</b></p>");
      });
    $("#myModal").modal();
}

function postMsg(){

    // Valid email, post to the server
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", loadMsg, false);
    xhr.open("GET", "../getSubs", true);
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send();
}

function loadMsg(event) {

    var msg = event.target.responseText;
    alert(msg);
}

function delSub(event) {
var mail = $('#inputEmail1').val();

 var result = validate(mail)
if (result == false) {
    alert (mail + " is not valid. Please specify a valid email");
    return;
}

// Valid email, post to the server
var xhr = new XMLHttpRequest();
xhr.addEventListener("load", loadItems, false);
xhr.open("POST", "../delSub", true);   //buildFormit -- a Spring MVC controller
xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
xhr.send("email=" + mail );
}

function loadItems(event) {

    var subNum = event.target.responseText;
    alert("Subscription validation is "+subNum);
}
