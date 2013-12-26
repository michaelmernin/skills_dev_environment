<div>
  <table class="table">

    <thead id="peer-review-form-overall-thead">
      <tr><th>Reviewer Composite Scores</th>
        <th>Self</th>
        <th>Counselor</th>
      </tr></thead>

    <tbody id="peer-review-form-overall-tbody">
      <tr><td>Client Engagements</td>
        <td id="rating_client_engagements">3</td>
        <td id="counselor_rating_client_engagements">3</td>
      </tr>
      <tr><td>Technical Abilities</td>
        <td id="rating_technical_abilities">3</td>
        <td id="counselor_rating_technical_abilities">3</td>

      </tr>
      <tr><td>Consulting Skills</td>
        <td id="rating_consulting_skills">3</td>
        <td id="counselor_rating_consulting_skills">3</td>
      </tr>
      <tr><td>Professionalism</td>
        <td id="rating_professionalism">3</td>
        <td id="counselor_rating_professionalism">3</td>
      </tr>
      <tr><td>Leadership</td>
        <td id="rating_leadership">3</td>
        <td id="counselor_rating_leadership">3</td>
      </tr>
      <tr><td>Teamwork</td>
        <td id="rating_teamwork">3</td>
        <td id="counselor_rating_teamwork">3</td>
      </tr>
      <tr><td>Internal Contributions</td>
        <td id="rating_internal_contributions">3.00</td>
        <td id="counselor_rating_internal_contributions">3.00</td>
      </tr>
      <tr><td>All</td>
        <td id="rating_all">3.00</td>
        <td id="counselor_rating_all">3.00</td>
      </tr>
    </tbody>
  </table>
</div>


<script>
  //counselor-rating-3
//assessment-content-value
  var counseleeCoreRatingID = new Array();
  counseleeCoreRatingID[0] = '#assessment-content-value-0';
  counseleeCoreRatingID[1] = '#assessment-content-value-1';
  counseleeCoreRatingID[2] = '#assessment-content-value-2';
  counseleeCoreRatingID[3] = '#assessment-content-value-3';
  counseleeCoreRatingID[4] = '#assessment-content-value-4';
  counseleeCoreRatingID[5] = '#assessment-content-value-5';


  var counseleeInternalRatingID = new Array();
  counseleeInternalRatingID[0] = '#assessment-content-value-6';
  counseleeInternalRatingID[1] = '#assessment-content-value-7';
  counseleeInternalRatingID[2] = '#assessment-content-value-8';
  counseleeInternalRatingID[3] = '#assessment-content-value-9';
  counseleeInternalRatingID[4] = '#assessment-content-value-10';



  var counselorCoreRatingID = new Array();
  counselorCoreRatingID[0] = '#counselor-rating-0';
  counselorCoreRatingID[1] = '#counselor-rating-1';
  counselorCoreRatingID[2] = '#counselor-rating-2';
  counselorCoreRatingID[3] = '#counselor-rating-3';
  counselorCoreRatingID[4] = '#counselor-rating-4';
  counselorCoreRatingID[5] = '#counselor-rating-5';


  var counselorInternalRatingID = new Array();
  counselorInternalRatingID[0] = '#counselor-rating-6';
  counselorInternalRatingID[1] = '#counselor-rating-7';
  counselorInternalRatingID[2] = '#counselor-rating-8';
  counselorInternalRatingID[3] = '#counselor-rating-9';
  counselorInternalRatingID[4] = '#counselor-rating-10';



  var counseleeOverallComositeScoreID = new Array();
  counseleeOverallComositeScoreID[0] = '#rating_client_engagements';
  counseleeOverallComositeScoreID[1] = '#rating_consulting_skills';
  counseleeOverallComositeScoreID[2] = '#rating_technical_abilities';
  counseleeOverallComositeScoreID[3] = '#rating_professionalism';
  counseleeOverallComositeScoreID[4] = '#rating_leadership';
  counseleeOverallComositeScoreID[5] = '#rating_teamwork';
  counseleeOverallComositeScoreID[6] = '#rating_internal_contributions';

  var counseleeOverallAverageScoreID = '#rating_all';



  var counselorOverallCompositeScoreid = new Array();
  counselorOverallCompositeScoreid[0] = '#counselor_rating_client_engagements';
  counselorOverallCompositeScoreid[1] = '#counselor_rating_consulting_skills';
  counselorOverallCompositeScoreid[2] = '#counselor_rating_technical_abilities';
  counselorOverallCompositeScoreid[3] = '#counselor_rating_professionalism';
  counselorOverallCompositeScoreid[4] = '#counselor_rating_leadership';
  counselorOverallCompositeScoreid[5] = '#counselor_rating_teamwork';
  counselorOverallCompositeScoreid[6] = '#counselor_rating_internal_contributions';

  var counselorOverallAverageScoreID = '#counselor_rating_all';

  /**
   * Core competencies vale change trigger event
   * 
   * */
  function counselorCoreCompetenciesSelectChange()
  {
    var value;
    for (var i = 0; i < counselorCoreRatingID.length; i++)
    {
      value = getElementValue(jQuery(counselorCoreRatingID[i]));
      if (IsNum(value) && value != '0')
      {
        jQuery(counselorOverallCompositeScoreid[i]).html(value);
      }
      else
      {
        jQuery(counselorOverallCompositeScoreid[i]).html('N/A');
      }
    }
    var rating = calculateAverageScore(counselorOverallCompositeScoreid);
    jQuery(counselorOverallAverageScoreID).html(rating);
  }


  function CounselorInternalContributionsSelectChange()
  {
    var internalRating = calculateAverageScore(counselorInternalRatingID);
    jQuery(counselorOverallCompositeScoreid[6]).html(internalRating);

    var rating = calculateAverageScore(counselorOverallCompositeScoreid);
    jQuery(counselorOverallAverageScoreID).html(rating);
  }


  function initializeRating()
  {
    //self_score_id
    var value;
    for (var i = 0; i < counseleeCoreRatingID.length; i++)
    {
      value = getElementValue(jQuery(counseleeCoreRatingID[i]));
      jQuery(counseleeOverallComositeScoreID[i]).html(value);
    }

    var internal_rating = calculateAverageScore(counseleeInternalRatingID);
    jQuery(counseleeOverallComositeScoreID[6]).html(internal_rating);

    var self_all_rating = calculateAverageScore(counseleeOverallComositeScoreID);
    jQuery(counseleeOverallAverageScoreID).html(self_all_rating);
  }


  function hideCounselorRating()
  {

    var url = window.location.href;
    if (url.indexOf(viewselfassessmentcontent) != -1)
    {
      jQuery("#peer-review-form-overall-thead tr th:nth-child(3)").hide();
      jQuery("#peer-review-form-overall-tbody tr td:nth-child(3)").hide();
    }

  }

  initializeRating();
  registerSelectOnchangeEvent(counselorCoreRatingID, counselorCoreCompetenciesSelectChange);
  registerSelectOnchangeEvent(counselorInternalRatingID, CounselorInternalContributionsSelectChange);
  counselorCoreCompetenciesSelectChange();
  CounselorInternalContributionsSelectChange();
  hideCounselorRating();

</script>

