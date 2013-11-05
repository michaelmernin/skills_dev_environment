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


var overall_reviewer_composite_scores_id = new Array();
overall_reviewer_composite_scores_id[0] = '#rating_technical_abilities';
overall_reviewer_composite_scores_id[1] = '#rating_consulting_skills';
overall_reviewer_composite_scores_id[2] = '#rating_professionalism';
overall_reviewer_composite_scores_id[3] = '#rating_leadership';
overall_reviewer_composite_scores_id[4] = '#rating_teamwork';
overall_reviewer_composite_scores_id[5] = '#rating_all';



function peer_review_technical_value_change()
{
    peer_review_select_value_change(technical_abilities_id, overall_reviewer_composite_scores_id[0]);
}
function peer_review_consulting_value_change()
{
    peer_review_select_value_change(consulting_skills_id, overall_reviewer_composite_scores_id[1]);
}

function peer_review_select_value_change(category, reviewer_composite_scores_id)
{
    var i = 0, count = 0, sum = 0, value;
    for (i = 0; i < category.length; i++)
    {
        value = jQuery(category[i]).find('option:selected').val();
        if (value != '')
        {
            count++;
            sum += parseInt(value);
        }
    }

    if (count != 0)
    {
        var score_rating = sum / count;
        jQuery(reviewer_composite_scores_id).html(score_rating);
    }
    else
    {
        jQuery(reviewer_composite_scores_id).html('');
    }
}

for (var i = 0; i < technical_abilities_id.length; i++)
{
    jQuery(technical_abilities_id[i]).change(peer_review_technical_value_change);
}

for (var i = 0; i < consulting_skills_id.length; i++)
{
    jQuery(consulting_skills_id[i]).change(peer_review_consulting_value_change);
}

