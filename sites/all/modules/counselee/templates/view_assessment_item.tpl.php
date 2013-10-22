<?php
/*
  This template is for each survey item. All arguments this template needs are:
  $item_num -- the item number
  $item_question -- the item question
  $item_rating -- the item self rating
  $item_comment -- the item self comment
  $item_peer_comments -- the synthesized peer comments
 */
?>
<div class="webform-submission-info clearfix">
  <!--Self comment-->
  <div class="webform-submission-info clearfix">
    <div id="header-<?php print $item_num; ?>" >
      <?php print $item_question; ?> 
    </div>
    <br>

    <!--PieChart-->
    <div style="float:right; margin: 4px 0px 0px; height: 200px; width: 50%;">

    </div>

    <div style="margin: 4px 0px 0px; height: 17px; width: 350px;">
      <div style="font-weight: 600;float: left">· Self Comment | </div>
      <div id="assessment-content-value-<?php print $item_num; ?>" 
           style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">· Self Rating:
        <font color="red"> <?php print $item_rating; ?>
        </font>
      </div>

    </div>
    <div style="margin: 4px 0px 0px; height: 112px; width: 200px;">
      <div class="additionalbubbleP">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">

          <div id="comment-content-value-<?php print $item_num; ?>" 
               style="margin: 4px 0px 0px; height: 112px; width: 96%;overflow-y:auto;">
                 <?php print $item_comment ?>
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
        · Counselor Comment | · Counselor Rating:
        <select id="counselor-rating-<?php print $item_num; ?>">
          <option value="0"> N/A </option>
          <option value="1"> 1 </option>
          <option value="2"> 2 </option>
          <option value="3" selected="selected"> 3 </option>
          <option value="4"> 4 </option>
          <option value="5"> 5 </option>
        </select>
      </div>

      <div class="additionalbubble">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
          <textarea id="counselor-comment-<?php print $item_num; ?>" 
                    cols="20" rows="5" 
                    style="margin: 4px 0px 0px; height: 112px; width: 98%;">
          </textarea>
        </div>
      </div>
    </div>
  </div>
  <br>
  <!--Display the peer comment message-->
  <div class="webform-submission-info clearfix">

    <div class="webform-submission-info-text">
      <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Peer Comment | ·Peer Rating:
      </div>

      <div class="additionalbubble">
        <div style="padding: 0 10px 5px;margin-bottom: 5px;">
          <textarea id="peer-comment-<?php print $item_num ?>" 
                    cols="20" rows="5" 
                    style="margin: 4px 0px 0px; height: 112px; width: 98%;">

          </textarea>
        </div>
      </div>
    </div>
  </div>
</div>
<hr>

