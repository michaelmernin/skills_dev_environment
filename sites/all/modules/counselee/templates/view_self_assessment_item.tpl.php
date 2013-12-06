<div class="webform-ssion-info clearfix">
  <!--Self comment-->
  <div class="webform-submission-info clearfix">
    <div id="header-<?php print $item_num; ?>" value="<?php print $self_dataset->form_key; ?>">
      <?php print $self_dataset->question; ?>
    </div>
    <br>
    <div class="view-self-comment-chart">
      <div style="margin: 4px 0px 0px; height: 17px; width: 50%;">
        <div style="font-weight: 600;float: left">·Self Comment |</div>
        <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Self Rating:</div>
        <div class="color-rating-box" id="assessment-content-value-<?php print $item_num; ?>">
          <?php
          if ($self_dataset->rating != 0) {
            print $self_dataset->rating;
          }
          elseif ($self_dataset->rating == 0) {
            print 'N/A';
          }
          ?>
        </div>


      </div>
      <div class="view-self-comment-bubble">
        <div class="additionalbubble">
          <div style="padding: 0 5px 5px;margin-bottom: 5px;">
            <div id="comment-content-value-<?php print $item_num; ?>" style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;">
              <?php print $self_dataset->comment ?>
            </div>
          </div>
        </div>
    </div>
    </div>
  </div>
 
</div>
<input type="hidden" id="counselee-overall_rating-<?php print $item_num; ?>" value="<?php print $overall_rating->counselee_rating ?>"/>

<hr>
