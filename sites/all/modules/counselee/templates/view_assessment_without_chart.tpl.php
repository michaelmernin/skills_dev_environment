<?php
// dd($item_num, 'item_num: ');
// dd($rreid, 'rreid');
// dd($nid, 'nid');
// dd($reviewee, 'reviewee');
// dd($review_info, 'review_info');
// dd($self_dataset, 'self_dataset');
// dd($clor_rating_comment, 'clor_rating_comment');
// dd($overall_rating, 'overall_rating');
?>
<div class="webform-submission-info clearfix">
  <!--Self comment-->
  <div class="webform-submission-info clearfix">
    <div id="header-<?php print $item_num; ?>" value="<?php print $self_dataset->form_key; ?>"  >
      <?php print $self_dataset->question; ?> 
    </div>
    <br>

    <div class="view-self-comment">
      <div class="view-self-comment-title">
        <div style="font-weight: 600;float: left">·Self Comment | </div>
        <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Self Rating:</div>
        <div class="color-rating-box" id="assessment-content-value-<?php print $item_num; ?>"> <?php
          if ($self_dataset->rating != 0) {
            print $self_dataset->rating;
          }
          elseif ($self_dataset->rating == 0) {
            print 'N/A';
          }
          ?></div>
      </div>
      <div class="view-self-comment-bubble">
        <div class="additionalbubble">
          <div style="padding: 0 5px 5px;margin-bottom: 5px;">
            <div id="comment-content-value-<?php print $item_num; ?>" style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;">
              <?php
              if (trim($self_dataset->comment) != '') {
                print $self_dataset->comment;
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
  </div>
  <br>
  <!--Display the counselor comment message-->
  <div class="webform-submission-info clearfix">
    <div class="webform-submission-info-text">
      <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">
        ·Counselor Comment | ·Counselor Rating:
        <select id="counselor-rating-<?php print $item_num; ?>">
          <option value="0">N/A</option>
          <option value="1">1</option>
          <option value="2">2</option>
          <option value="3">3</option>
          <option value="4">4</option>
          <option value="5">5</option>
        </select>
      </div>

      <!--<div class="additionalbubble">-->
      <div>
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
          <textarea id="counselor-comment-<?php print $item_num; ?>"
                    cols="20" rows="5"
                    form-key="<?php print $self_dataset->form_key; ?>"
                    style="margin: 4px 0px 0px; height: 112px; width: 98%;">
                      <?php
            if (isset($clor_rating_comment) && isset($clor_rating_comment->clor_comment)) {
              print $clor_rating_comment->clor_comment;
            }
            else {
              print '';
            }
            ?></textarea>
        </div>
      </div>
    </div>
  </div>
  <br>
  <input type="hidden" id="rreid-<?php print $item_num; ?>" value="<?php print $rreid ?>"/>
  <input type="hidden" id="nid-<?php print $item_num; ?>" value="<?php print $nid ?>"/>
  <input type="hidden" id="total_item_count-<?php print $item_num; ?>" value="<?php print $total_item_count ?>"/>
  <input type="hidden" id="counselor-overall_rating-<?php print $item_num; ?>" value="<?php print $overall_rating->counselor_rating ?>"/>
  <input type="hidden" id="counselee-overall_rating-<?php print $item_num; ?>" value="<?php print $overall_rating->counselee_rating ?>"/>

</div>
<hr>
<script>
  checkValue();
  function checkValue() {
    var range = [0, 1, 2, 3, 4, 5];
    var rating = <?php
            if (isset($clor_rating_comment) && isset($clor_rating_comment->rating)) {
              print $clor_rating_comment->rating;
            }
            else {
              print -1;
            }
            ?>;
    if (inArray(rating, range)) {
      jQuery("#counselor-rating-<?php print $item_num; ?>").val(rating);
    }
    else {
      jQuery("#counselor-rating-<?php print $item_num; ?>").val(3);
    }
  }


  function inArray(needle, haystack) {
    var length = haystack.length;
    for (var i = 0; i < length; ++i) {
      if (haystack[i] == needle) {
        return true;
      }
    }
    return false;
  }
</script>
