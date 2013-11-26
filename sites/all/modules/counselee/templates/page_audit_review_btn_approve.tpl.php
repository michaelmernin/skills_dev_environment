<div class="form-actions">
  <a id="modal-89759" href="#modal-container-89759" role="button" 
     class="btn btn-large" data-toggle="modal">
    Disapprove
  </a>
  <a id="modal-89758" href="#modal-container-89758" role="button" 
     class="btn btn-large btn-primary" data-toggle="modal">
    Approve
  </a>
</div>


<div id="modal-container-89759" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
    <h3 id="myModalLabel">
      Disagree the review result:
    </h3>
  </div>
  <div class="modal-body">
    <p>
      Are you sure not to agree with the review result?
    </p>
  </div>
  <div class="modal-footer">
    <img class="loading_img" id="status_loading_img_89759"
         title="loading..." 
         style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> 
    <button class="btn btn-danger" id="disapprove_button" 
            onclick="disapprove_review_result(<?php print $rreid; ?>)">
      Submit
    </button>
  </div>
</div>

<div id="modal-container-89758" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
    <h3 id="myModalLabel">
      Confirm to review result
    </h3>
  </div>
  <div class="modal-body">
    <p>
      Are you sure to agree with the review result?
    </p>
  </div>
  <div class="modal-footer">
    <img class="loading_img" id="status_loading_img_89758" title="loading..." 
         style="width: 25px; height: 25px; display: none" 
         src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> 
    <button class="btn btn-danger" 
            id="approve_button" 
            onclick="approve_review_result(<?php print $rreid; ?>)">
      Submit
    </button>
  </div>
</div>

<!--modal-89757-->
<div id="modal-container-89757" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
    <h3 id="myModalLabel">
      Confirm to submit review
    </h3>
  </div>
  <div class="modal-body">
    <p>
      Are you sure to submit the review?
    </p>
  </div>
  <div class="modal-footer">
    <img class="loading_img" id="status_loading_img_89757" 
         title="loading..." 
         style="width: 25px; height: 25px; display: none"
         src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> 
    <button class="btn btn-danger" id="submit_button"
            onclick="submit_review_result(<?php print $rreid; ?>)">
      Submit
    </button>
  </div>
</div>

<script>
              function disapprove_review_result(id)
              {
                jQuery('#status_loading_img_89759').css('display', 'inline');
                var bathpath = '<?php print get_curPage_base_url(); ?>';
                jQuery.ajax({
                  type: "POST",
                  url: bathpath + 'audit-review/disapprove-review-result',
                  data: {'rreid': id},
                  success: function(date) {
                    location.reload();
                  }
                });
              }

              function approve_review_result(id)
              {
                jQuery('#status_loading_img_89758').css('display', 'inline');
                var bathpath = '<?php print get_curPage_base_url(); ?>';
                jQuery.ajax({
                  type: "POST",
                  url: bathpath + 'audit-review/approve-review-result',
                  data: {'rreid': id},
                  success: function(date) {
                    location.reload();
                  }
                });
              }

              function submit_review_result(id)
              {
                jQuery('#status_loading_img_89757').css('display', 'inline');
                var bathpath = '<?php print get_curPage_base_url(); ?>';
                jQuery.ajax({
                  type: "POST",
                  url: bathpath + 'audit-review/submit-review-result',
                  data: {'rreid': id},
                  success: function(date) {
                    location.reload();
                  }
                });
              }
</script>