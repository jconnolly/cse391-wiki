$(document).ready(
  $("#save").click(function () {
    var textAreaValue = $("#body").text();
    alert(textAreaValue); //do POST to /API/{{title}} here
  });
);



