<div class="form-actions">
  <input class="webform-draft form-submit btn btn-large" type="button" name="op" value="Approve" onclick="approve_review_result(<?php print $rreid; ?>)">
  <input class="webform-draft form-submit btn btn-large" type="button" name="op" value="Disapprove" onclick="disapprove_review_result(<?php print $rreid; ?>)">
</div>

<script>
    function disapprove_review_result(id)
    {
      var reason = prompt("Please input the disapprove reason:", "");
      if (reason.length < 1)
      {
        alert('You can\'t leave Disapprove Reason empty.');
        disapprove_review_result();
      }
      else
      {
        var bathpath = '<?php print get_curPage_base_url(); ?>';
        jQuery.ajax({
          type: "POST",
          url: bathpath + 'audit-review/disapprove-review-result',
          data: {'rreid': id},
          success: function(date) {

          }
        });
      }
    }


    function approve_review_result(id)
    {
      var isAgree = window.confirm("Are you sure to agree the review result?");
      if (isAgree == false)
        return;

      var bathpath = '<?php print get_curPage_base_url(); ?>';
      jQuery.ajax({
        type: "POST",
        url: bathpath + 'audit-review/approve-review-result',
        data: {'rreid': id},
        success: function(date) {

        }
      });
    }
</script>