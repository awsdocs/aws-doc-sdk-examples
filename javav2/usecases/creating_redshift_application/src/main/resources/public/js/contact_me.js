$(function() {

    $('#progress').hide();

    $("#SendButton" ).click(function($e) {

        var title = $('#title').val();
        var body = $('#body').val();

        $.ajax('/addPost', {
            type: 'POST',  // http method
            data: 'title=' + title + '&body=' + body ,  // data to submit
            success: function (data, status, xhr) {

                alert("You have successfully added an item")

                $('#title').val("");
                $('#body').val("");

            }
        });

    } );// END of the Send button click

} );

function getPosts(num){

    $('.xsearch-items').empty()
    $('#progress').show();
    var lang = $('#lang option:selected').text();

    $.ajax('/getPosts', {
        type: 'POST',  // http method
        data: 'lang=' + lang+"&number=" + num ,  // data to submit
        success: function (data, status, xhr) {

            var xml = data;
            $('#progress').hide();
            $(xml).find('Item').each(function () {

                var $field = $(this);
                var id = $field.find('Id').text();
                var date = $field.find('Date').text();
                var title = $field.find('Title').text();
                var body = $field.find('Content').text();
                var author = $field.find('Author').text();

                // Append this data to the main list.
                $('.xsearch-items').append("<className='search-item'>");
                $('.xsearch-items').append("<div class='search-item-content'>");
                $('.xsearch-items').append("<h3 class='search-item-caption'><a href='#'>" + title + "</a></h3>");
                $('.xsearch-items').append("<className='search-item-meta mb-15'>");
                $('.xsearch-items').append("<className='list-inline'>");
                $('.xsearch-items').append("<p><b>" + date + "</b></p>");
                $('.xsearch-items').append("<p><b>'Posted by " + author + "</b></p>");
                $('.xsearch-items').append("<div>");
                $('.xsearch-items').append("<h6>" + body + "</h6>");
                $('.xsearch-items').append("</div>");
            });
        }
    });
 }
