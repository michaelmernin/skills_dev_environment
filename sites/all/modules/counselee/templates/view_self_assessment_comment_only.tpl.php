<div class="webform-submission-info clearfix">
  <!--Self comment-->
  <div class="webform-submission-info clearfix">
    <div id="header-<?php print $item_num; ?>" value="<?php print $self_comment->form_key; ?>"  >
      <?php print $self_comment->question; ?> 
    </div>
    <br>
    
    <div style="font-weight: 600;float: left">·Self Comment : </div>
    <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;"></div>
    <div class="view-self-comment-bubble">
        <div class="additionalbubble">
          <div style="padding: 0 5px 5px;margin-bottom: 5px;">
            <div id="comment-content-value-<?php print $item_num; ?>" style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;">
              <?php print $self_comment->comment ?>
            </div>
          </div>
        </div>
    </div>
  </div>
  <br>
</div>
<hr>

