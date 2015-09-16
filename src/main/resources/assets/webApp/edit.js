$(document).ready( function() {
  $("#save").click(function () {

    var wikiText = $("#body").val();
    var title = $(document).find("title").text()
    dataJson = { "title" : title, "body" : wikiText }
    data = new FormData();
    data.append('data', JSON.stringify(dataJson));

    $.ajax({
      url: "/API/"+title,
      data: data,
      async: false,
      cache: false,
      contentType: false,
      processData: false,
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



