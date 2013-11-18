<div class="webform-submission-info clearfix">
  <legend class="bubble">Peer review information</legend>
  <div class="webform-submission-info-text">
    <div>
      <b style="font-weight: 700">Title:</b>
      <?php print $basic_info->title ?>
    </div>
    <div>
      <b style="font-weight: 700">Submitted By:</b>
      <?php print $basic_info->user ?>
    </div>
    <div>
      <b style="font-weight: 700">Submitted Time:</b>
      <?php print $basic_info->creatdate ?>
    </div>
    <div>
      <b style="font-weight: 700">Submitted From:</b>
      <?php print $basic_info->ip ?>
    </div>
  </div>
</div>
<input type="hidden" id="hidPeerReviewNid" value="<?php print $basic_info->nid ?>"/>
<hr />