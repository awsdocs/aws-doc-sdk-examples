$(function() {

    $('#progress').hide();

    $("#SendButton" ).click(function($e) {

        var title = $('#title').val();
        var body = $('#body').val();

        //invokes the getMyForms POST operation
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", loadNewItems, false);
        xhr.open("POST", "../addPost", true);   //buildFormit -- a Spring MVC controller
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
        xhr.send("title=" + title + "&body=" + body);
    } );// END of the Send button click

    //Handler for the uploadSave call
    //This will populate the Data Table widget
    function loadNewItems(event) {

        var msg = event.target.responseText;
        alert("You have successfully added item "+msg)

        $('#title').val("");
        $('#body').val("");
    }

   } );

  function getDataValue()
  {
    var radioValue = $("input[name='optradio']:checked").val();
   return radioValue;
  }

function getPosts(num){

    $('.xsearch-items').empty()
    $('#progress').show();
    var lang = $('#lang option:selected').text();

    //invokes the getMyForms POST operation
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", loadItems, false);
    xhr.open("POST", "../getPosts", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("lang=" + lang +"&number=" + num );
}

function loadItems(event) {

    //Clear the list
    $('#progress').hide();
    var xml = event.target.responseText;
    $(xml).find('Item').each(function ()  {

        var $field = $(this);
        var id = $field.find('Id').text();
        var date = $field.find('Date').text();
        var title = $field.find('Title').text();
        var body = $field.find('Content').text();
        var author = $field.find('Author').text();
        var vv = "Prime video"

        // Append this data to the main list.

        $('.xsearch-items').append("<className='search-item'>");
        $('.xsearch-items').append("<div class='search-item-content'>");
        $('.xsearch-items').append("<h3 class='search-item-caption'><a href='#'>"+title+"</a></h3>");
        $('.xsearch-items').append("<className='search-item-meta mb-15'>");
        $('.xsearch-items').append("<className='list-inline'>");
        $('.xsearch-items').append("<p><b>"+date+"</b></p>");
        $('.xsearch-items').append("<p><b>'Posted by "+author+"</b></p>");
        $('.xsearch-items').append("<div>");
        $('.xsearch-items').append("<h6>"+body +"</h6>");
        $('.xsearch-items').append("</div>");
     });
}

