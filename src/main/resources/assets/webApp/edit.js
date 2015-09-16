$(document).ready( function() {
  $("#save").click(function () {

    var wikiText = $("#body").val();
    var title = $(document).find("title").text()
    data = { "title" : title, "body" : wikiText }

    $.ajax("/API/"+title, {
      data: JSON.stringify(data),
      contentType: 'application/json',
      type: 'POST'
    });
  });
});



