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
  /**
   * srcSequence array desciption the items of the category
   * the first 4 stand for that technical_abilities have 4 items
   * */
  var srcSequence = new Array(4, 4, 4, 2, 2);
  var destSequence = new Array(1, 1, 1, 1, 1);


  /**
   * Set value for Reviewer Composite Scores
   * @param {array} rating The page rating id array 
   * @param {array} overall The Reviewer Composite Scores id array
   * @param {array} arcSequenc The category of the
   * 
   * */
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

/**
 * Change the Reviewer Composite Scores value
 * */
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

  /**
   * Hide the counselor overall rating column
   * When the page is viewselfassessmentcontent
   * There is no counselor rating column
   * */
  function hideCounselorRating()
  {
    var url = window.location.href;
    if (url.indexOf("viewselfassessmentcontent") != -1)
    {
      jQuery("#project-review-overall-thead tr th:nth-child(3)").hide();
      jQuery("#project-review-overall-tbody tr td:nth-child(3)").hide();
    }
  }

  setCounselorOverallRating();
  setCounseleeOverallRating();
  hideCounselorRating();
  addChangeEventForSelect();
  projectReviewRatingOnchage();

</script>
