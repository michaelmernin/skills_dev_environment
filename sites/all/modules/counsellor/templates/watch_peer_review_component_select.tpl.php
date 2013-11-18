<div class="webform-submission-info clearfix">
  <div><?php print $component_content->name ?></div>
  <div class="webform-submission-info-text">
    <div style="font-weight: 600;float: left">·Comment | </div>
    <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Rating:</div><div class="color-rating-box"><?php print $component_content->answer ?></div></div>

  <div class="additionalbubble">
    <div style="padding: 0 10px 5px;margin-bottom: 5px;">

      <?php print $component_content->comment ?>
      <input type="hidden" id="hidPeerRviewCid"value=" <?php print $component_content->cid ?>"/>
    </div>
  </div>
</div>
<hr/>