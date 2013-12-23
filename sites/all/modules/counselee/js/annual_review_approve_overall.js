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


//counselor-comment
//counselor-rating
function checkCounselorComment()
{
    var ratings = getCommonNameId("counselor-rating", "select");
    var comments = getCommonNameId("counselor-comment", "textarea");

    var i, len = ratings.length, score, comment, field, isRight = true, formKey;
    for (i = 0; i < len; i++)
    {
        score = getElementValue(jQuery(ratings[i]));
        comment = getElementValue(jQuery(comments[i]));
        jQuery(comments[i]).attr("style", "margin: 4px 0px 0px; height: 112px; width: 98%;");
        if ((score != '3' || score != 3) && comment.trim().length < 1)
        {
            addErrorMessageArea();
            formKey = jQuery(comments[i]).attr('form-key');
            field = getFormKeyName(formKey);
            li = '<li>' + field + "Comment field is required,because his score is not 3 point." + '</li>';
            jQuery("#error-message").append(li);
            jQuery(comments[i]).attr("style", "margin: 4px 0px 0px; height: 112px; width: 98%;border: 2px solid red;");
            isRight = false;
        }
    }
    return isRight;
}



/**
 * Check Counselor Required Field
 * 
 * */
function checkCounselorRequireField()
{
    var comments = getSameFormKeyId("text", "textarea");
    var i, len = comments.length, comment, field, isRight = true, formKey;
    for (i = 0; i < len; i++)
    {
        comment = getElementValue(jQuery(comments[i]));
        jQuery(comments[i]).attr("style", "margin: 4px 0px 0px; height: 112px; width: 98%;");
        if (comment.trim().length < 1)
        {
            addErrorMessageArea();
            formKey = jQuery(comments[i]).attr('form-key');
            field = getFormKeyName(formKey);
            li = '<li>' + field + " field is required." + '</li>';
            jQuery("#error-message").append(li);
            jQuery(comments[i]).attr("style", "margin: 4px 0px 0px; height: 112px; width: 98%;border: 2px solid red;");
            isRight = false;
        }
    }
    return isRight;
}

initializeRating();
registerSelectOnchangeEvent(counselorCoreRatingID, counselorCoreCompetenciesSelectChange);
registerSelectOnchangeEvent(counselorInternalRatingID, CounselorInternalContributionsSelectChange);
counselorCoreCompetenciesSelectChange();
CounselorInternalContributionsSelectChange();


jQuery(document).ready(
        function() {
            jQuery("input[name='op'][value='Approve']").click(
                    function() {
                        jQuery(".messages.error").remove();
                        var result = checkCounselorComment();
                        result = checkCounselorRequireField() && result;
                        if (!result)
                            goTopEx();
                        return result;

                    });
            jQuery("#counselor_disapprove_btn").click(function() {
                return check_reject_reasons();
            })
        }
);

function check_reject_reasons() {
    var comment = jQuery.trim(jQuery("#counselor_reject_reason").val());
    var len = comment.length;
    if (len < 1) {
        alert('Please write down reason. If you are going to reject, this area cannot be blank!');
        jQuery("#counselor_reject_reason").focus();
        disapprovehidalert();
        return false;
    }
    return true;
}

function disapprovehidalert() {
    jQuery('#counselor_disapprove_btn').removeAttr("disabled");
    jQuery('#disapprove_status_loading_img').hide();
    jQuery('#counselor_disapprove_btn').removeClass();
    jQuery('#counselor_disapprove_btn').addClass("btn btn-danger");
}