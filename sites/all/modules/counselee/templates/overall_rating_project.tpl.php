<div>
  <table class="table">

    <thead id="project-review-overall-thead">
      <tr><th>Reviewer Composite Scores</th>
        <th>Self</th>
        <th>Counselor</th>
      </tr></thead>

    <tbody id="project-review-overall-tbody">

      <tr><td>Technical Abilities</td>
        <td id="counselee_rating_technical_abilities">3</td>
        <td id="counselor_rating_technical_abilities">3</td>
      </tr>

      <tr><td>Consulting Skills</td>
        <td id="counselee_rating_consulting_skills">3</td>
        <td id="counselor_rating_consulting_skills">3</td>
      </tr>

      <tr><td>Professionalism</td>
        <td id="counselee_rating_professionalism">3</td>
        <td id="counselor_rating_professionalism">3</td>
      </tr>

      <tr><td>Leadership</td>
        <td id="counselee_rating_leadership">3</td>
        <td id="counselor_rating_leadership">3</td>
      </tr>

      <tr><td>Teamwork</td>
        <td id="counselee_rating_teamwork">3</td>
        <td id="counselor_rating_teamwork">3</td>
      </tr>

      <tr><td>All</td>
        <td id="counselee_average">3.00</td>
        <td id="counselor_average">3.00</td>
      </tr>

    </tbody>
  </table>
</div>


<script>

  var counseleeOverall = getCommonNameId("counselee_rating", 'td');
  var counselorOverall = getCommonNameId("counselor_rating", 'td');
  var counseleeRating = getCommonNameId("assessment-content-value", 'div');
  var counselorRating = getCommonNameId("counselor-rating-", 'div');
  var srcSequence = new Array(4, 4, 4, 2, 2);
  var destSequence = new Array(1, 1, 1, 1, 1);

  function calculateCatetoryRating(rating, overall, srcSequence, destSequence)
  {
    var srcStart = 0, destStart = 0;
    for (var len = overall.length, i = 0; i < len; i++)
    {
      modifyCategoryValue(rating.slice(srcStart, srcStart + srcSequence[i]),
              overall.slice(destStart, destStart + destSequence[i]));
      srcStart += srcSequence[i];
      destStart += destSequence[i];
    }
  }

  function projectReviewRatingOnchage()
  {
    calculateCatetoryRating(counseleeRating, counseleeOverall, srcSequence, destSequence);
    calculateCatetoryRating(counselorRating, counselorOverall, srcSequence, destSequence);

    modifyCategoryValue(counseleeOverall, getCommonNameId('counselee_average', 'td'));
    modifyCategoryValue(counselorOverall, getCommonNameId('counselor_average', 'td'));
  }

  function addChangeEventForSelect()
  {
    if (counselorRating.length < 1)
    {
      counselorRating = getCommonNameId("counselor-rating-", 'select');
      registerSelectOnchangeEvent(counselorRating, projectReviewRatingOnchage);
    }
  }


  function hideCounselorRating()
  {

    var url = window.location.href;
    if (url.indexOf(viewselfassessmentcontent) != -1)
    {
      jQuery("#project-review-overall-thead tr th:nth-child(3)").hide();
      jQuery("#project-review-overall-tbody tr td:nth-child(3)").hide();
    }

  }

  addChangeEventForSelect();
  projectReviewRatingOnchage();
  hideCounselorRating();

</script>
