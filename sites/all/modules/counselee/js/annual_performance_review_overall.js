var coreCompetenciesPre = 'core-competencies-category';
var coreCompetenciesArr = getSamePrefixID(coreCompetenciesPre);


var internalContributionsPre = 'internal-contributions-category';
var internalContributionsArr = getSamePrefixID(internalContributionsPre);

var composite_score_id = new Array();
composite_score_id[0] = '#composite_core_competencies';
composite_score_id[1] = '#composite_internal_contributions';


//notice that the positon #rating_technical_abilities and #rating_consulting_skills
var overall_self_composite_scores_id = new Array();
overall_self_composite_scores_id[0] = '#rating_client_engagements';
overall_self_composite_scores_id[1] = '#rating_consulting_skills';
overall_self_composite_scores_id[2] = '#rating_technical_abilities';
overall_self_composite_scores_id[3] = '#rating_professionalism';
overall_self_composite_scores_id[4] = '#rating_leadership';
overall_self_composite_scores_id[5] = '#rating_teamwork';
overall_self_composite_scores_id[6] = '#rating_internal_contributions';

var overall_self_scores_average_id = '#rating_all';

/**
 * Core competencies vale change trigger event
 * 
 * */
function categoryCoreCcompetenciesValueChange()
{
    modifySelectCategoryValue(coreCompetenciesArr,
            new Array(composite_score_id[0]));

    var value;
    for (var i = 0; i < coreCompetenciesArr.length; i++)
    {
        value = jQuery(coreCompetenciesArr[i]).find('option:selected').val();
        if (value == '0')
            value = "";
        jQuery(overall_self_composite_scores_id[i]).html(value);
    }

    modifyOverallScoreAverage(overall_self_composite_scores_id,
            new Array(overall_self_scores_average_id));

}
function categoryInternalIcontributionsValueChange()
{
    modifySelectCategoryValue(internalContributionsArr,
            new Array(composite_score_id[1], overall_self_composite_scores_id[6]));

    modifyOverallScoreAverage(overall_self_composite_scores_id,
            new Array(overall_self_scores_average_id));
}

jQuery(document).ready(
        function() {
            registerSelectOnchangeEvent(coreCompetenciesArr, categoryCoreCcompetenciesValueChange);
            registerSelectOnchangeEvent(internalContributionsArr, categoryInternalIcontributionsValueChange);
            categoryCoreCcompetenciesValueChange();
            categoryInternalIcontributionsValueChange();
            jQuery("input[name='op'][value='Submit']").click(
                    function() {
                        jQuery(".messages.error").remove();
                        var result = checkComments(coreCompetenciesArr, 'comments')
                        result = checkComments(internalContributionsArr, 'comments') && result;
                        result = checkRequireField() && result;
                        goTopEx();
                        return result;
                    });
        }
);