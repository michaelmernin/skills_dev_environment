//edit-submitted-performance-evaluation-category-core-competencies-category-client-engagements
var core_competencies_id_pre = '#edit-submitted-performance-evaluation-category-core-competencies-category-';
var core_competencies_id = new Array();
core_competencies_id[0] = core_competencies_id_pre + 'client-engagements';
core_competencies_id[1] = core_competencies_id_pre + 'technical-abilities';
core_competencies_id[2] = core_competencies_id_pre + 'consulting-skills';
core_competencies_id[3] = core_competencies_id_pre + 'professionalism';
core_competencies_id[4] = core_competencies_id_pre + 'leadership';
core_competencies_id[5] = core_competencies_id_pre + 'teamwork';


var core_competencies_comment_id = new Array();
core_competencies_comment_id[0] = core_competencies_id_pre + 'client-engagements-comments';
core_competencies_comment_id[1] = core_competencies_id_pre + 'technical-abilities-comments';
core_competencies_comment_id[2] = core_competencies_id_pre + 'consulting-skills-comments';
core_competencies_comment_id[3] = core_competencies_id_pre + 'professionalism--comments';
core_competencies_comment_id[4] = core_competencies_id_pre + 'leadership-comments';
core_competencies_comment_id[5] = core_competencies_id_pre + 'teamwork-comments';

//edit-submitted-performance-evaluation-category-internal-contributions-category-business-development
var internal_contributions_id_pre = '#edit-submitted-performance-evaluation-category-internal-contributions-category-';
var internal_contributions_id = new Array();
internal_contributions_id[0] = internal_contributions_id_pre + 'business-development';
internal_contributions_id[1] = internal_contributions_id_pre + 'career-counseling';
internal_contributions_id[2] = internal_contributions_id_pre + 'recruiting-assistance';
internal_contributions_id[3] = internal_contributions_id_pre + 'internal-contributions';
internal_contributions_id[4] = internal_contributions_id_pre + 'perficient-basics';

var internal_contributions_comment_id = new Array();
internal_contributions_comment_id[0] = internal_contributions_id_pre + 'business-development-comments';
internal_contributions_comment_id[1] = internal_contributions_id_pre + 'career-counseling-comments';
internal_contributions_comment_id[2] = internal_contributions_id_pre + 'recruiting-assistance-comments';
internal_contributions_comment_id[3] = internal_contributions_id_pre + 'internal-contributions-comments';
internal_contributions_comment_id[4] = internal_contributions_id_pre + 'perficient-basics-comments';



var composite_score_id = new Array();
composite_score_id[0] = '#composite_core_competencies';
composite_score_id[1] = '#composite_internal_contributions';


var overall_self_composite_scores_id = new Array();
overall_self_composite_scores_id[0] = '#rating_client_engagements';
overall_self_composite_scores_id[1] = '#rating_technical_abilities';
overall_self_composite_scores_id[2] = '#rating_consulting_skills';
overall_self_composite_scores_id[3] = '#rating_professionalism';
overall_self_composite_scores_id[4] = '#rating_leadership';
overall_self_composite_scores_id[5] = '#rating_teamwork';
overall_self_composite_scores_id[6] = '#rating_internal_contributions';

var overall_self_scores_average_id = '#rating_all';



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
        value = jQuery(category[i]).find('option:selected').val();
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
        jQuery(composite_score_id[index]).html(score_rating);
    }
    else
    {
        jQuery(composite_score_id[index]).html('');
    }
    if (index == 1)
    {
        jQuery(overall_self_composite_scores_id[6]).html(score_rating);
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
function self_review_core_competencies_value_change()
{
    self_review_select_value_change(core_competencies_id, 0);
    var value;
    for (var i = 0; i < core_competencies_id.length; i++)
    {
        value = jQuery(core_competencies_id[i]).find('option:selected').val();
        if (IsNum(value) && value != '0')
        {
            jQuery(overall_self_composite_scores_id[i]).html(value);
        }
        else
        {
            jQuery(overall_self_composite_scores_id[i]).html('');
        }
    }
    calculate_element_value_average(overall_self_composite_scores_id);
}
function self_review_internal_contributions()
{
    self_review_select_value_change(internal_contributions_id, 1);
    calculate_element_value_average(overall_self_composite_scores_id);
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
        jQuery(overall_self_scores_average_id).html(score_rating);
    }
    else
    {
        jQuery(overall_self_scores_average_id).html('');
    }
}

register_self_form_select_onchange_event(core_competencies_id, self_review_core_competencies_value_change);
register_self_form_select_onchange_event(internal_contributions_id, self_review_internal_contributions);
self_review_core_competencies_value_change();
self_review_internal_contributions();



function check_comments(category, comments)
{
    var value, comment, len;
    for (var i = 0; i < category.length; i++)
    {
        value = jQuery(category[i]).find('option:selected').val();
//        alert(value);
        if (value != '3')
        {
            comment = jQuery(comments[i]).val();
            len = comment.length;
            if (len < 2) {
                alert('The socre is not 3 points.Please enter comment!');
                jQuery(comments[i]).focus();
                return false;
            }
        }
    }
    return true;
}

jQuery(document).ready(
        function() {
            jQuery("input[name='op'][value='Submit']").click(
                    function() {
                        var result = check_comments(core_competencies_id, core_competencies_comment_id)
                                && check_comments(internal_contributions_id, internal_contributions_comment_id);
                        alert(result);
                        return result;
                    });
        }
);