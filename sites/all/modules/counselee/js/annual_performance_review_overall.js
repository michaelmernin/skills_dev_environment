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
    modifyCategoryValue(coreCompetenciesArr,
            new Array(composite_score_id[0]));

    var value;
    for (var i = 0; i < coreCompetenciesArr.length; i++)
    {
        value = jQuery(coreCompetenciesArr[i]).find('option:selected').val();
        if (value == '0')
            value = "";
        jQuery(overall_self_composite_scores_id[i]).html(value);
    }

    modifyCategoryValue(overall_self_composite_scores_id,
            new Array(overall_self_scores_average_id));

}
function categoryInternalIcontributionsValueChange()
{
    modifyCategoryValue(internalContributionsArr,
            new Array(composite_score_id[1], overall_self_composite_scores_id[6]));

    modifyCategoryValue(overall_self_composite_scores_id,
            new Array(overall_self_scores_average_id));
}

function convertTextToDate()
{
    var startDate = getCommonNameId("start-date", 'input');
    var endDate = getCommonNameId("end-date", 'input');
    textEleAddDateInput(startDate);
    textEleAddDateInput(endDate);
}

function textEleAddDateInput(arr)
{
    for (var i = 0; i < arr.length; i++)
    {
        jQuery(arr[i]).attr("readonly", true);
        jQuery(arr[i]).css('color', '#34495E');
        jQuery(arr[i]).css('background', '#ffffff');
        jQuery(arr[i]).datepicker({
            firstDay: 1,
            changeMonth: true,
            changeYear: true
        });
    }
}

jQuery(document).ajaxComplete(
        function() {
            convertTextToDate();
        }
);


function checkProjectStartEndDate()
{
    var startDate = getCommonNameId("start-date", 'input');
    var endDate = getCommonNameId("end-date", 'input');
    var len = startDate.length;
    var start, end, startStr, endStr, li, isLegal = true;
    for (i = 0; i < len; i++)
    {
        startStr = jQuery(startDate[i]).val();
        endStr = jQuery(endDate[i]).val();
        if (startStr == '' || endStr == '')
            continue;

        start = new Date(startStr);
        end = new Date(endStr);

        if (start >= end)
        {
            isLegal = false;
            addErrorMessageArea();
            li = '<li>' + "Project Roles And Responsibilities " + (i + 1) + " Start Date must early than End Date." + '</li>';
            jQuery("#error-message").append(li);
            jQuery(startDate[i]).removeClass().addClass('form-text required form-textarea error');
            jQuery(endDate[i]).removeClass().addClass('form-text required form-textarea error');
        }
    }
    return isLegal;
}


jQuery(document).ready(
        function() {
            registerSelectOnchangeEvent(coreCompetenciesArr, categoryCoreCcompetenciesValueChange);
            registerSelectOnchangeEvent(internalContributionsArr, categoryInternalIcontributionsValueChange);
            categoryCoreCcompetenciesValueChange();
            categoryInternalIcontributionsValueChange();
            convertTextToDate();
            jQuery("input[name='op'][value='Submit']").click(
                    function() {
                        jQuery(".messages.error").remove();
                        var result = checkComments(coreCompetenciesArr, 'comments')
                        result = checkComments(internalContributionsArr, 'comments') && result;
                        result = checkRequireField() && result;
                        result = checkProjectStartEndDate() && result;
                        if (!result)
                            goTopEx();
                        return result;
                    });
        }
);