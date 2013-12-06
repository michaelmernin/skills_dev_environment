<?php
// dd($item_num, 'item_num: ');
// dd($rreid, 'rreid');
// dd($nid, 'nid');
// dd($reviewee, 'reviewee');
// dd($self_comment, 'self_comment');
?>
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
            <?php
            if (trim($self_comment->comment) != '') {
              print $self_comment->comment;
            }
            else {
              print NO_COMMENT;
            }
            ?>
          </div>
        </div>
      </div>
    </div>

  </div>
  <br>
  <!--Display the counselor comment message-->
  <div class="webform-submission-info clearfix">
    <div class="webform-submission-info-text">
      <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">
        · Counselor Comment:
      </div>
      <div class="view-self-comment-bubble">
        <div class="additionalbubble">
          <div style="padding: 0 5px 5px;margin-bottom: 5px;">
            <div id="counselor-comment-<?php print $item_num; ?>" style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;"><?php
              if (isset($clor_rating_comment) && isset($clor_rating_comment->clor_comment)) {
                if (trim($clor_rating_comment->clor_comment) != '') {
                  print $clor_rating_comment->clor_comment;
                }
                else {
                  print NO_COMMENT;
                }
              }
              else {
                print '';
              }
              ?>
            </div>
          </div>
        </div>
      </div>     
    </div>
  </div>
  <br>
</div>
<hr>

