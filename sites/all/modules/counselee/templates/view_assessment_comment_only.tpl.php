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

    <div style="margin: 4px 0px 0px; height: 17px; width: 350px;">
      <div style="font-weight: 600;float: left">· Self Comment:</div>
    </div>
    <div style="margin: 4px 0px 0px; height: 112px; width: 200px;">
      <div class="additionalbubbleP">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">

          <div id="comment-content-value-<?php print $item_num; ?>" 
               style="margin: 4px 0px 0px; height: 112px; width: 96%;overflow-y:auto;">
                 <?php if ($self_comment->comment == '') { print 'No comment!'; } else { print $self_comment->comment;} ?>
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

      <div class="additionalbubble">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
            <textarea id="counselor-comment-<?php print $item_num; ?>" cols="20" rows="5" style="margin: 4px 0px 0px; height: 112px; width: 98%;"></textarea>
        </div>
      </div>
    </div>
  </div>
  <br>
<input type="hidden" id="rreid-<?php print $item_num; ?>" value="<?php print $rreid ?>"/>
<input type="hidden" id="nid-<?php print $item_num; ?>" value="<?php print $nid ?>"/>
<input type="hidden" id="total_item_count-<?php print $item_num; ?>" value="<?php print $total_item_count ?>"/>
</div>
<hr>
