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

<script>
  initialOverScore();
</script>


<?php
$counselee_path = drupal_get_path('module', 'counselee');
drupal_add_js($counselee_path . '/js/calculate.js');
?>