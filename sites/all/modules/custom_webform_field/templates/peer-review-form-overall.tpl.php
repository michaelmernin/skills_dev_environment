<table class="table">
  <caption>
    <h3>Overall Evaluation</h3>
  </caption>

  <thead id='peer-review-form-overall-thead'>

  </thead>

  <tbody id="peer-review-form-overall-tbody">
  </tbody>
</table>

<script>


  var head_json = '<?php print json_encode($table_head) ?>';
  var head = eval(head_json);
  var thead = '<tr><th>' + head[0] + '</th><th>' + head[1] + '</th>';
  jQuery('#peer-review-form-overall-thead').html(thead);

  var peer_review_overall_json = '<?php print json_encode($score_overall); ?>';
  var peer_review_overall = eval(peer_review_overall_json);

  var i = 0, obj, tr, tbody = '';
  for (i = 0; i < peer_review_overall.length; i++)
  {
    obj = peer_review_overall[i];
    tr = '<tr><td>' + obj.category + '</td><td id=' + obj.tdid + '> ' + obj.rating + '</td></tr>';
    tbody += tr;
  }
  jQuery('#peer-review-form-overall-tbody').html(tbody);

</script>




