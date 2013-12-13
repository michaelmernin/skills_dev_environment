//counselor-rating-3
//assessment-content-value
var self_score_id = new Array();
self_score_id[0] = '#assessment-content-value-0';
self_score_id[1] = '#assessment-content-value-1';
self_score_id[2] = '#assessment-content-value-2';
self_score_id[3] = '#assessment-content-value-3';
self_score_id[4] = '#assessment-content-value-4';
self_score_id[5] = '#assessment-content-value-5';


var self_internal_score_id = new Array();
self_internal_score_id[0] = '#assessment-content-value-6';
self_internal_score_id[1] = '#assessment-content-value-7';
self_internal_score_id[2] = '#assessment-content-value-8';
self_internal_score_id[3] = '#assessment-content-value-9';
self_internal_score_id[4] = '#assessment-content-value-10';



var counselor_score_id = new Array();
counselor_score_id[0] = '#counselor-rating-0';
counselor_score_id[1] = '#counselor-rating-1';
counselor_score_id[2] = '#counselor-rating-2';
counselor_score_id[3] = '#counselor-rating-3';
counselor_score_id[4] = '#counselor-rating-4';
counselor_score_id[5] = '#counselor-rating-5';


var counselor_internal_score_id = new Array();
counselor_internal_score_id[0] = '#counselor-rating-6';
counselor_internal_score_id[1] = '#counselor-rating-7';
counselor_internal_score_id[2] = '#counselor-rating-8';
counselor_internal_score_id[3] = '#counselor-rating-9';
counselor_internal_score_id[4] = '#counselor-rating-10';



var overall_self_composite_scores_id = new Array();
overall_self_composite_scores_id[0] = '#rating_client_engagements';
overall_self_composite_scores_id[1] = '#rating_consulting_skills';
overall_self_composite_scores_id[2] = '#rating_technical_abilities';
overall_self_composite_scores_id[3] = '#rating_professionalism';
overall_self_composite_scores_id[4] = '#rating_leadership';
overall_self_composite_scores_id[5] = '#rating_teamwork';
overall_self_composite_scores_id[6] = '#rating_internal_contributions';

var overall_self_scores_average_id = '#rating_all';



var counselor_composite_id = new Array();
counselor_composite_id[0] = '#counselor_rating_client_engagements';
counselor_composite_id[1] = '#counselor_rating_consulting_skills';
counselor_composite_id[2] = '#counselor_rating_technical_abilities';
counselor_composite_id[3] = '#counselor_rating_professionalism';
counselor_composite_id[4] = '#counselor_rating_leadership';
counselor_composite_id[5] = '#counselor_rating_teamwork';
counselor_composite_id[6] = '#counselor_rating_internal_contributions';

var counselor_composite_average_id = '#counselor_rating_all';



/**
 * Get page element value
 * 
 * @param {object} ele Object Element
 * */
function getElementValue(ele) {
    var type = ele[0].tagName;

    if (type == "INPUT")
        return ele.val();
    else if (type == "TEXTAREA")
        return ele.val();
    else if (type == "SELECT")
        return ele.find('option:selected').val();
    else if (type == "DIV")
        return ele.html();


    return ele.val();
}


function IsNum(str)
{
    if (str != null && str != "" && str != ' ')
    {
        return !isNaN(str);
    }
    return false;
}

function self_review_select_value_change(category, index)
{
    var i = 0, count = 0, sum = 0, value;
    for (i = 0; i < category.length; i++)
    {
        value = getElementValue(jQuery(category[i]));
        if (IsNum(value) && value != '0')
        {
            count++;
            sum += parseInt(value);
        }
    }

    var score_rating = '';
    if (count != 0)
    {
        score_rating = (sum / count).toFixed(2);
    }
    if (index == 1)
    {
        jQuery(counselor_composite_id[6]).html(score_rating);
    }


    //peer_review_overall_score_calculate(overall_reviewer_composite_scores_id, overall_reviewer_scores_average_id);
}

/**
 * Rejister select value onchange function
 * */
function register_self_form_select_onchange_event(category, onchangeEvent)
{
    for (var i = 0; i < category.length; i++)
    {
        jQuery(category[i]).change(onchangeEvent);
    }
}

/**
 * Core competencies vale change trigger event
 * 
 * */
function counselor_core_competencies_value_change()
{
    var value;
    for (var i = 0; i < counselor_score_id.length; i++)
    {
        value = getElementValue(jQuery(counselor_score_id[i]));
        if (IsNum(value) && value != '0')
        {
            jQuery(counselor_composite_id[i]).html(value);
        }
        else
        {
            jQuery(counselor_composite_id[i]).html('');
        }
    }
    var rating = calculate_element_value_average(counselor_composite_id);
    jQuery(counselor_composite_average_id).html(rating);
}


function self_review_internal_contributions()
{
    self_review_select_value_change(counselor_internal_score_id, 1);

    var rating = calculate_element_value_average(counselor_composite_id);
    jQuery(counselor_composite_average_id).html(rating);
}


/**
 * Calculate the overall score average 
 *
 * */
function calculate_element_value_average(category)
{
    var i = 0, count = 0, sum = 0, value;
    for (i = 0; i < category.length; i++)
    {
        value = jQuery(category[i]).html();
        if (IsNum(value) && value != '0')
        {
            count++;
            sum += parseFloat(value);
        }
    }

    var score_rating = '';
    if (count != 0)
    {
        score_rating = (sum / count).toFixed(2);
//        jQuery(overall_self_scores_average_id).html(score_rating);
    }
    else
    {
//        jQuery(overall_self_scores_average_id).html('');
    }
    return  score_rating;
}


function initialize_self_score_rating()
{
    //self_score_id
    var value;
    for (var i = 0; i < self_score_id.length; i++)
    {
        value = getElementValue(jQuery(self_score_id[i]));
        if ((IsNum(value)))
            jQuery(overall_self_composite_scores_id[i]).html(value);
        else
            jQuery(overall_self_composite_scores_id[i]).html("");

    }

    var internal_rating = calculate_element_value_average(self_internal_score_id);
    jQuery(overall_self_composite_scores_id[6]).html(internal_rating);

    var self_all_rating = calculate_element_value_average(overall_self_composite_scores_id);
    jQuery(overall_self_scores_average_id).html(self_all_rating);
}



function getCommonNameId(same, type)
{
    var id, idArr = new Array();
    var arr = jQuery(type);
    for (var i = 0; i < arr.length; i++)
    {
        id = arr[i].getAttribute("id");
        if (id != null && id.indexOf(same) != -1)
        {
            idArr[idArr.length] = "#" + id;
        }
    }
    return idArr;
}


//counselor-comment
//counselor-rating
function checkComment()
{
    var ratings = getCommonNameId("counselor-rating", "select");
    var comments = getCommonNameId("counselor-comment", "textarea");

    var i, len = ratings.length, score, comment, isRight = true;
    for (i = 0; i < len; i++)
    {
        score = jQuery(ratings[i]).find('option:selected').val();
        comment = jQuery(comments[i]).val();

        jQuery(comments[i]).removeClass().addClass("form-textarea");
        if ((score != '3' || score != 3) && comment.length < 1)
        {
            alert(comments[i]);
//            addErrorMessageArea();
//            field = getCategoryChildName(category[i]);
//            li = '<li>' + field + "Comment field is required,because his score is not 3 point." + '</li>';
//            jQuery("#error-message").append(li);

            jQuery(comments[i]).css("border", "2px");
            jQuery(comments[i]).css("solid", "red");
            isRight = false;
        }
    }
    return isRight;
}




initialize_self_score_rating();
register_self_form_select_onchange_event(counselor_score_id, counselor_core_competencies_value_change);
register_self_form_select_onchange_event(counselor_internal_score_id, self_review_internal_contributions);
counselor_core_competencies_value_change();
self_review_internal_contributions();


jQuery(document).ready(
        function() {
            jQuery("input[name='op'][value='Approve']").click(
                    function() {
//                        checkComment();

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

function disapprovehidalert(){
    
    jQuery('#counselor_disapprove_btn').removeAttr("disabled");
    jQuery('#disapprove_status_loading_img').hide();
    jQuery('#counselor_disapprove_btn').removeClass();
    jQuery('#counselor_disapprove_btn').addClass("btn btn-danger");
    
}