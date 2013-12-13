<div class="webform-submission-info clearfix">
  <!--Self comment-->
  <div class="webform-submission-info clearfix">
    <div id="header-17" value="achievements_text">
     <?php print ucfirst($node['description']); ?>
    </div>
    <br>

    <div style="font-weight: 600;float: left">·Self Comment : </div>
    <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;"></div>
    <div class="view-self-comment-bubble">
      <div class="additionalbubble">
        <div style="padding: 0 5px 5px;margin-bottom: 5px;">
          <div id="comment-content-value-17" style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;">
           <?php print display_comment($node['self_description']); ?>
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
            <div id="counselor-comment-17" style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;">
            <?php print display_comment($node['counselor_description']); ?>
            </div>
          </div>
        </div>
      </div>     
    </div>
  </div>
  <br>
</div>

<hr>