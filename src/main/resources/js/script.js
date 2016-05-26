window.onload = function() {
  $("#same-hide").click(function() {
        $("tr.same").toggle();
  });
  $("#deleted-hide").click(function() {
        $("tr.deleted").toggle();
  });
  $("#added-hide").click(function() {
        $("tr.added").toggle();
  });
  $("#renamed-hide").click(function() {
        $("tr.renamed").toggle();
  });
  $("#diff-hide").click(function() {
        $("tr.diff").toggle();
  });
}
