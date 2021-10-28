// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

$(function () {
    $("#SendButton").click(function ($e) {

        var body = $('#body').val();
        var lang = $('#lang option:selected').text();
        if (body == '') {
            alert("Please enter text");
            return;
        }

        $.ajax('home/PublishMessage', {
            type: 'POST',
            data: 'lang=' + lang + '&body=' + body,
            success: function (data, status, xhr) {

                alert(data)
                $('#body').val("");
            },
            error: function (jqXhr, textStatus, errorMessage) {
                $('p').append('Error' + errorMessage);
            }
        });
    });
});

function subEmail() {
    var mail = $('#inputEmail1').val();
    var result = validate(mail)
    if (result == false) {
        alert(mail + " is not valid. Please specify a valid email.");
        return;
    }

    $.ajax('home/AddEmailSub', {
        type: 'POST',
        data: 'email=' + mail,
        success: function (data, status, xhr) {
            alert("Subscription validation is " + data)
        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}

function delSub(event) {
    var mail = $('#inputEmail1').val();
    var result = validate(mail)

    if (result == false) {
        alert(mail + " is not valid. Please specify a valid email");
        return;
    }

    $.ajax('home/RemoveEmailSub', {
        type: 'POST',  // http GET method
        data: 'email=' + mail,
        success: function (data, status, xhr) {

            alert(data);
        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}

function subscribe() {

    $.ajax({
        url: 'home/GetAjaxValue',
        success: function (data, status, xhr) {

            $('.modal-body').empty();
            var xml = data;
            $(xml).find('Sub').each(function () {

                var $field = $(this);
                var email = $field.find('email').text();

                // Append this data to the main list.
                $('.modal-body').append("<p><b>" + email + "</b></p>");
            });
            $("#myModal").modal();
        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
}

function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function validate(email) {
    const $result = $("#result");

    if (validateEmail(email)) {
        return true;
    } else {
        return false;
    }
}