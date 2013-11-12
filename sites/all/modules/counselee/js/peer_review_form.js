
//edit-submitted-performance-evaluation-technical-abilities-productivity-comment
var technical_abilities_id_pre = '#edit-submitted-performance-evaluation-technical-abilities-';
var technical_abilities_id = new Array();
technical_abilities_id[0] = technical_abilities_id_pre + 'productivity';
technical_abilities_id[1] = technical_abilities_id_pre + 'quality';
technical_abilities_id[2] = technical_abilities_id_pre + 'results';
technical_abilities_id[3] = technical_abilities_id_pre + 'technical-skills';

var consulting_skills_id_pre = '#edit-submitted-performance-evaluation-consulting-skills-';
var consulting_skills_id = new Array();
consulting_skills_id[0] = consulting_skills_id_pre + 'adaptability';
consulting_skills_id[1] = consulting_skills_id_pre + 'communication ';
consulting_skills_id[2] = consulting_skills_id_pre + 'interpersonal-skills';
consulting_skills_id[3] = consulting_skills_id_pre + 'core-consuling-skills';


// edit-submitted-performance-evaluation-professionalism-category-attitude
var professionalism_id_pre = '#edit-submitted-performance-evaluation-professionalism-category-';
var professionalism_id = new Array();
professionalism_id[0] = professionalism_id_pre + 'attitude';
professionalism_id[1] = professionalism_id_pre + 'dependability';
professionalism_id[2] = professionalism_id_pre + 'initiative';
professionalism_id[3] = professionalism_id_pre + 'professionalism';


//edit-submitted-performance-evaluation-leadership-category-decision-making
var leadership_id_pre = '#edit-submitted-performance-evaluation-leadership-category-';
var leadership_id = new Array();
leadership_id[0] = leadership_id_pre + 'decision-making';
leadership_id[1] = leadership_id_pre + 'leadership';


//edit-submitted-performance-evaluation-teamwork-category-teamwork
var teamwork_id_pre = '#edit-submitted-performance-evaluation-teamwork-category-';
var teamwork_id = new Array();
teamwork_id[0] = teamwork_id_pre + 'teamwork';
teamwork_id[1] = teamwork_id_pre + 'customer-focus';

var composite_score_id = new Array();
composite_score_id[0] = '#composite_technical_abilities';
composite_score_id[1] = '#composite_consulting_skills';
composite_score_id[2] = '#composite_professionalism';
composite_score_id[3] = '#composite_leadership';
composite_score_id[4] = '#composite_teamwork';

var overall_reviewer_composite_scores_id = new Array();
overall_reviewer_composite_scores_id[0] = '#rating_technical_abilities';
overall_reviewer_composite_scores_id[1] = '#rating_consulting_skills';
overall_reviewer_composite_scores_id[2] = '#rating_professionalism';
overall_reviewer_composite_scores_id[3] = '#rating_leadership';
overall_reviewer_composite_scores_id[4] = '#rating_teamwork';
var overall_reviewer_scores_average_id = '#rating_all';
/**
 * Judge the string is number
 * */
function IsNum(str)
{
    if (str != null && str != "" && str != ' ' && str != '0')
    {
        return !isNaN(str);
    }
    return false;
}


/**
 * Calculate the average of the score
 * 
 * @type array catetory  The category that need to calculate;
 * @type string reviewer_composite_scores_id  The html element that need to change;
 * 
 * */
function peer_review_select_value_change(category, index)
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
    if (count != 0)
    {
        var score_rating = (sum / count).toFixed(2);
        jQuery(composite_score_id[index]).html(score_rating);
        jQuery(overall_reviewer_composite_scores_id[index]).html(score_rating);
    }
    else
    {
        jQuery(composite_score_id[index]).html('');
        jQuery(overall_reviewer_composite_scores_id[index]).html('');
    }
    peer_review_overall_score_calculate(overall_reviewer_composite_scores_id, overall_reviewer_scores_average_id);
}

/**
 * Calculate the overall score average
 * */
function peer_review_overall_score_calculate(category, overall_rating)
{
    var i = 0, count = 0, sum = 0, value;
    for (i = 0; i < category.length; i++)
    {
        value = jQuery(category[i]).html();
        if (IsNum(value))
        {
            count++;
            sum += parseFloat(value);
        }
    }
    if (count != 0)
    {
        var score_rating = (sum / count).toFixed(2);
        jQuery(overall_rating).html(score_rating);
    }
    else
    {
        jQuery(overall_rating).html('');
    }

}

/**
 * Register the select onchange event!
 * 
 * */

function register_peer_review_select_onchange_event(category, onchangeEvent)
{
    for (var i = 0; i < category.length; i++)
    {
        jQuery(category[i]).change(onchangeEvent);
    }
}

/**
 * 
 * 
 **/
function peer_review_technical_value_change()
{
    peer_review_select_value_change(technical_abilities_id, 0);
}
function peer_review_consulting_value_change()
{
    peer_review_select_value_change(consulting_skills_id, 1);
}
function peer_review_professionalism_value_change()
{
    peer_review_select_value_change(professionalism_id, 2);
}
function peer_review_leadership_value_change()
{
    peer_review_select_value_change(leadership_id, 3);
}
function peer_review_teamwork_value_change()
{
    peer_review_select_value_change(teamwork_id, 4);
}

var composite_score_technical = '<table class="table"><thead><tr><th>Composite Score: Core Competencies</th><th id = "peer_review_composite_technical"></th></tr></thead></table>';
jQuery('#peer_review_composite_score_technical').html(composite_score_technical);

register_peer_review_select_onchange_event(technical_abilities_id, peer_review_technical_value_change);
register_peer_review_select_onchange_event(consulting_skills_id, peer_review_consulting_value_change);
register_peer_review_select_onchange_event(professionalism_id, peer_review_professionalism_value_change);
register_peer_review_select_onchange_event(leadership_id, peer_review_leadership_value_change);
register_peer_review_select_onchange_event(teamwork_id, peer_review_teamwork_value_change);

peer_review_technical_value_change();
peer_review_consulting_value_change();
peer_review_professionalism_value_change();
peer_review_leadership_value_change();
peer_review_teamwork_value_change();

function check_comments(category)
{
    var value, comment, len;
    for (var i = 0; i < category.length; i++)
    {
        value = jQuery(category[i]).find('option:selected').val();
//        alert(value);
        if (value != '3')
        {
            comment = jQuery(category[i] + "-comment").val();
            len = comment.length;
            if (len < 1) {
                alert('The socre is not 3 points.Please enter comment!');
                jQuery(category[i] + "-comment").focus();
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
                        return  check_comments(technical_abilities_id)
                                && check_comments(consulting_skills_id)
                                && check_comments(professionalism_id)
                                && check_comments(leadership_id)
                                && check_comments(teamwork_id);
                    });
        }
);


