<?php
 ?>
<div class="sec-peerfieldset">
  <?php if ($review_info_content_num == 0): ?> 
		<h6>  Project Roles And Responsibilities </h6>
  <?php endif; ?>
</div>
<div class="fieldset-wrapper">
  <div id="webform-component-wfm" class="wfm-container form-wrapper">
    <div class="wfm-item">
      <fieldset class="webform-component-fieldset webform-component--project-roles-and-responsibilities-category--wfm--0 form-wrapper">
        <div class="fieldset-wrapper">
          <div class="form-item webform-component webform-component-textfield webform-component--project-roles-and-responsibilities-category--wfm--0--clientdate" style="width: 30%">
            <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;width: 25%">·Client:</div>
            <div class="color-rating-box" id="client-name" style="width: 60%"><?php if($clientdate != '' && !isset($client)) print $clientdate; else print $client; ?></div>
          </div>					
          <div class="form-item webform-component webform-component-textfield webform-component--project-roles-and-responsibilities-category--wfm--0--startdate"  style="width: 25%">
            <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·StartDate:</div>
            <div class="color-rating-box" id="client-start-date"><?php print $startdate; ?></div>

          </div>					
          <div class="form-item webform-component webform-component-textfield webform-component--project-roles-and-responsibilities-category--wfm--0--enddate" style="width: 25%">
            <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·EndDate:</div>
            <div class="color-rating-box" id="client-end-date"><?php print $enddate; ?></div>
          </div>					
        </div>
				<?php if (isset($project) && $project != ''): ?>
					<div class="form-item webform-component webform-component-textfield webform-component--project-roles-and-responsibilities-category--wfm--0--project" style="width: 25%">
						<div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Project:</div>
						<div class="color-rating-box" id="client-project"><?php print $project; ?></div>
					</div>					
				<?php endif; ?>

        <div class="form-item webform-component webform-component-textarea webform-component--project-roles-and-responsibilities-category--wfm--0--project-roles-and-responsibilities">

          <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Project Roles And Responsibilities</div>
          <div class="view-self-comment-bubble">
            <div class="additionalbubble">
              <div style="padding: 0 5px 5px;margin-bottom: 5px;">
                <div id="comment-content-value" style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;">
                  <?php print filter_xss_and_check_plain($project_roles_and_responsibilities); ?>
                </div>
              </div>
            </div>
          </div>
        </div>
      </fieldset>
    </div>
  </div>
</div>
