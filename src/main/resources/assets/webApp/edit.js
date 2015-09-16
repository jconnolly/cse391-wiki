$(document).ready( function() {
  $("#save").click(function () {

    var wikiText = $("#body").val();
    var title = $(document).find("title").text()
    data = { "title" : title, "body" : wikiText }

    $.ajax({
      url: "/API/"+title,
      data: JSON.stringify(data),
      contentType: 'application/json',
      type: 'POST'
    })
    .done(function(resp){
        console.log(resp);
        $.notify("Successfully " + resp.status + " " + title, "success");
    })
    .fail(function(resp){
      console.log(resp);
      $.notify("Error saving " + title, "error");
    });
  });
});



