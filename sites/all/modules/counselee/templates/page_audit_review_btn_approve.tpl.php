
<div>
  <table class="table">

    <thead id="peer-review-form-overall-thead">
      <tr><th>Reviewer Composite Scores</th>
        <th>Self</th>
        <th>Counselor</th>
      </tr></thead>

    <tbody id="peer-review-form-overall-tbody">
      <tr><td>Client Engagements</td>
        <td id="rating_client_engagements"></td>
        <td id="counselor_rating_client_engagements"></td>
      </tr>
      <tr><td>Technical Abilities</td>
        <td id="rating_technical_abilities"></td>
        <td id="counselor_rating_technical_abilities"></td>

      </tr>
      <tr><td>Consulting Skills</td>
        <td id="rating_consulting_skills"></td>
        <td id="counselor_rating_consulting_skills"></td>
      </tr>
      <tr><td>Professionalism</td>
        <td id="rating_professionalism">3</td>
        <td id="counselor_rating_professionalism">3</td>
      </tr>
      <tr><td>Leadership</td>
        <td id="rating_leadership"></td>
        <td id="counselor_rating_leadership"></td>
      </tr>
      <tr><td>Teamwork</td>
        <td id="rating_teamwork"></td>
        <td id="counselor_rating_teamwork"></td>
      </tr>
      <tr><td>Internal Contributions</td>
        <td id="rating_internal_contributions"></td>
        <td id="counselor_rating_internal_contributions"></td>
      </tr>
      <tr><td>All</td>
        <td id="rating_all"></td>
        <td id="counselor_rating_all"></td>
      </tr>
    </tbody>
  </table>
</div>




<div class="form-actions">
  <a id="modal-89759" href="#modal-container-89759" role="button" 
     class="btn btn-large" data-toggle="modal">
    Disapprove
  </a>
  <a id="modal-89758" href="#modal-container-89758" role="button" 
     class="btn btn-large btn-primary" data-toggle="modal">
    Approve
  </a>
</div>


<div id="modal-container-89759" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
    <h3 id="myModalLabel">
      Disagree the review result:
    </h3>
  </div>
  <div class="modal-body">
    <p>
      Are you sure not to agree with the review result?
    </p>
  </div>
  <div class="modal-footer">
    <img class="loading_img" id="status_loading_img_89759"
         title="loading..." 
         style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> 
    <button class="btn btn-danger" id="disapprove_button" 
            onclick="disapprove_review_result(<?php print $rreid; ?>)">
      Submit
    </button>
  </div>
</div>

<div id="modal-container-89758" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
    <h3 id="myModalLabel">
      Confirm to review result
    </h3>
  </div>
  <div class="modal-body">
    <p>
      Are you sure to agree with the review result?
    </p>
  </div>
  <div class="modal-footer">
    <img class="loading_img" id="status_loading_img_89758" title="loading..." 
         style="width: 25px; height: 25px; display: none" 
         src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> 
    <button class="btn btn-danger" 
            id="approve_button" 
            onclick="approve_review_result(<?php print $rreid; ?>)">
      Submit
    </button>
  </div>
</div>

<!--modal-89757-->
<div id="modal-container-89757" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
    <h3 id="myModalLabel">
      Confirm to submit review
    </h3>
  </div>
  <div class="modal-body">
    <p>
      Are you sure to submit the review?
    </p>
  </div>
  <div class="modal-footer">
    <img class="loading_img" id="status_loading_img_89757" 
         title="loading..." 
         style="width: 25px; height: 25px; display: none"
         src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> 
    <button class="btn btn-danger" id="submit_button"
            onclick="submit_review_result(<?php print $rreid; ?>)">
      Submit
    </button>
  </div>
</div>

<script type="text/javascript">

              function IsNum(str)
              {
                if (str != null && str != "" && str != ' ')
                {
                  return !isNaN(str);
                }
                return false;
              }

              function disapprove_review_result(id)
              {
                jQuery('#status_loading_img_89759').css('display', 'inline');
                var bathpath = '<?php print get_curPage_base_url(); ?>';
                jQuery.ajax({
                  type: "POST",
                  url: bathpath + 'audit-review/disapprove-review-result',
                  data: {'rreid': id},
                  success: function(date) {
                    location.reload();
                  }
                });
              }

              function approve_review_result(id)
              {
                jQuery('#status_loading_img_89758').css('display', 'inline');
                var bathpath = '<?php print get_curPage_base_url(); ?>';
                jQuery.ajax({
                  type: "POST",
                  url: bathpath + 'audit-review/approve-review-result',
                  data: {'rreid': id},
                  success: function(date) {
                    location.reload();
                  }
                });
              }

              function submit_review_result(id)
              {
                jQuery('#status_loading_img_89757').css('display', 'inline');
                var bathpath = '<?php print get_curPage_base_url(); ?>';
                jQuery.ajax({
                  type: "POST",
                  url: bathpath + 'audit-review/submit-review-result',
                  data: {'rreid': id},
                  success: function(date) {
                    location.reload();
                  }
                });
              }


              function calculate_average_score(category, elementType)
              {
                var i = 0, count = 0, sum = 0, value, averageScore;

                for (i = 0; i < category.length; i++)
                {
                  if (elementType == 'select')
                    value = jQuery(category[i]).find('option:selected').val();
                  else if (elementType == 'html')
                    value = jQuery(category[i]).html();

                  if (IsNum(value) && value != '0')
                  {
                    count++;
                    sum += parseInt(value);
                  }
                }

                var averageScore = '';
                if (count != 0)
                {
                  averageScore = (sum / count).toFixed(2);
                }
                return averageScore;
              }


              function initialOverScore()
              {
                var category = new Array();
                category[0] = 'client_engagements';
                category[1] = 'technical_abilities';
                category[2] = 'consulting_skills';
                category[3] = 'professionalism';
                category[4] = 'leadership';
                category[5] = 'teamwork';

                for (var i = 0; i < category.length; i++)
                {
                  jQuery('#rating_' + category[i]).html(jQuery('#src_self_' + category[i]).html());
                  jQuery('#counselor_rating_' + category[i]).html(jQuery('#src_counselor_' + category[i]).html());
                }

                var selfPre = '#src_self_';
                var self_internal = new Array();
                self_internal[0] = selfPre + 'business_development';
                self_internal[1] = selfPre + 'career_counseling';
                self_internal[2] = selfPre + 'recruiting_assistance';
                self_internal[3] = selfPre + 'internal_contributions';
                self_internal[4] = selfPre + 'perficient_basics';

                var self_internal = calculate_average_score(self_internal, 'html');
                //rating_internal_contributions
                jQuery('#rating_internal_contributions').html(self_internal);
                
                
                
                var counselorPre = '#src_counselor_'; 
                var counselor_internal = new Array();
                counselor_internal[0] = counselorPre + 'business_development';
                counselor_internal[1] = counselorPre + 'career_counseling';
                counselor_internal[2] = counselorPre + 'recruiting_assistance';
                counselor_internal[3] = counselorPre + 'internal_contributions';
                counselor_internal[4] = counselorPre + 'perficient_basics';
                
                
                var counselor_internal = calculate_average_score(counselor_internal, 'html');
                jQuery('#counselor_rating_internal_contributions').html(counselor_internal);

              }
              initialOverScore();


</script>