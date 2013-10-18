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
        <td id="rating_professionalism"></td>
        <td id="counselor_rating_professionalism"></td>
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

<script>
  function initialOverScore()
  {
    var category = new Array();
    category[0] = 'client_engagements';
    category[1] = 'technical_abilities';
    category[2] = 'consulting_skills';
    category[3] = 'professionalism';
    category[4] = 'leadership';
    category[5] = 'teamwork';

    var selfRate, counselorRate;
    for (var i = 0; i < category.length; i++)
    {
      selfRate = jQuery('#src_self_' + category[i]).html();
//      if (IsNum(selfRate))
      jQuery('#rating_' + category[i]).html(selfRate);

      counselorRate = jQuery('#src_counselor_' + category[i]).html();
//      if (IsNum(counselorRate))
      jQuery('#counselor_rating_' + category[i]).html(counselorRate);
    }

    var selfPre = '#src_self_';
    var self_internal = new Array();
    self_internal[0] = selfPre + 'business_development';
    self_internal[1] = selfPre + 'career_counseling';
    self_internal[2] = selfPre + 'recruiting_assistance';
    self_internal[3] = selfPre + 'internal_contributions';
    self_internal[4] = selfPre + 'perficient_basics';

    var self_internal = calculateAverageScore(self_internal);
    //rating_internal_contributions
    jQuery('#rating_internal_contributions').html(self_internal);

    var counselorPre = '#src_counselor_';
    var counselor_internal = new Array();
    counselor_internal[0] = counselorPre + 'business_development';
    counselor_internal[1] = counselorPre + 'career_counseling';
    counselor_internal[2] = counselorPre + 'recruiting_assistance';
    counselor_internal[3] = counselorPre + 'internal_contributions';
    counselor_internal[4] = counselorPre + 'perficient_basics';

    var counselor_internal = calculateAverageScore(counselor_internal);
    jQuery('#counselor_rating_internal_contributions').html(counselor_internal);

    category[6] = 'internal_contributions';

    var self_all = new Array();
    var counselor_all = new Array()
    for (var i = 0; i < category.length; i++)
    {
      self_all[i] = '#rating_' + category[i];
      counselor_all[i] = '#counselor_rating_' + category[i];
    }

    var self_all_score = calculateAverageScore(self_all);
    jQuery('#rating_all').html(self_all_score);

    var counselor_all_score = calculateAverageScore(counselor_all);
    jQuery('#counselor_rating_all').html(counselor_all_score);

  }

  initialOverScore();
</script>


<?php
$counselee_path = drupal_get_path('module', 'counselee');
drupal_add_js($counselee_path . '/js/calculate.js');
?>