<table class="table">
  <caption>
    <h3>Overall Evaluation</h3>
  </caption>

  <thead>
    <tr>
      <th>
        Reviewer Composite Scores
      </th>
      <th>
        Reviewer
      </th>
  </thead>

  <tbody id="peer-review-form-overall-tbody">
  </tbody>
</table>

<script>


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




