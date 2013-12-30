var technicalAbilitiesPre = 'technical-abilities-category';
var technicalAbilitiesArr = getSamePrefixID(technicalAbilitiesPre);

var consultingSkillsPre = 'consulting-skills-category';
var consultingSkillsArr = getSamePrefixID(consultingSkillsPre);

var professionalismPre = 'professionalism-category';
var professionalismArr = getSamePrefixID(professionalismPre);

var leadershipPre = 'leadership-category';
var leadershipArr = getSamePrefixID(leadershipPre);

var teamworkPre = 'category-teamwork-category';
var teamworkArr = getSamePrefixID(teamworkPre);

var compositeScoreArr = new Array();
compositeScoreArr[0] = '#composite_technical_abilities';
compositeScoreArr[1] = '#composite_consulting_skills';
compositeScoreArr[2] = '#composite_professionalism';
compositeScoreArr[3] = '#composite_leadership';
compositeScoreArr[4] = '#composite_teamwork';

var overallCompositeScoresArr = new Array();
overallCompositeScoresArr[0] = '#rating_technical_abilities';
overallCompositeScoresArr[1] = '#rating_consulting_skills';
overallCompositeScoresArr[2] = '#rating_professionalism';
overallCompositeScoresArr[3] = '#rating_leadership';
overallCompositeScoresArr[4] = '#rating_teamwork';
var overallScoresAverageId = '#rating_all';


/**Calculate the average of the score
 * 
 * @type array catetory  The category that need to calculate;
 * @type string reviewer_composite_scores_id  The html element that need to change;
 * 
 * */
function calculateSelectCategoryValueAndModify(category, index)
{
    var destArr = new Array();
    destArr[0] = compositeScoreArr[index];
    destArr[1] = overallCompositeScoresArr[index];
    modifyCategoryValue(category, destArr);

    modifyCategoryValue(overallCompositeScoresArr, new Array(overallScoresAverageId));
}

function categoryTechnicalValueChange()
{
    calculateSelectCategoryValueAndModify(technicalAbilitiesArr, 0);
}
function categoryConsultingValueChange()
{
    calculateSelectCategoryValueAndModify(consultingSkillsArr, 1);
}
function categoryProfessionalismValueChange()
{
    calculateSelectCategoryValueAndModify(professionalismArr, 2);
}
function categoryLeadershipValueChange()
{
    calculateSelectCategoryValueAndModify(leadershipArr, 3);
}
function categoryTeamworkValueChange()
{
    calculateSelectCategoryValueAndModify(teamworkArr, 4);
}
registerSelectOnchangeEvent(technicalAbilitiesArr, categoryTechnicalValueChange);
registerSelectOnchangeEvent(consultingSkillsArr, categoryConsultingValueChange);
registerSelectOnchangeEvent(professionalismArr, categoryProfessionalismValueChange);
registerSelectOnchangeEvent(leadershipArr, categoryLeadershipValueChange);
registerSelectOnchangeEvent(teamworkArr, categoryTeamworkValueChange);

categoryTechnicalValueChange();
categoryConsultingValueChange();
categoryProfessionalismValueChange();
categoryLeadershipValueChange();
categoryTeamworkValueChange();


function addNoteMessageArea()
{
    var position = jQuery('.form-actions');
    var content = '<div><font color="red">'
            + 'Please note, upon selecting the Submit button, the form is locked and un-editable.'
            + '</font></div>';
    position.after(content);

}

function modifyProjectStyle()
{
//    webform-component-fieldset webform-component--project-roles-and-responsibilities-category form-wrapper
    var position = jQuery(".webform-component-fieldset.webform-component--project-roles-and-responsibilities-category.form-wrapper");
    position.removeClass().addClass('wfm-item');
}
/**
 * initial project name
 * 
 * */
function initialProjectName()
{
    var title = jQuery("#hidWebFormTitle").val();
    if (title.indexOf('_PROJECT_REVIEW') > 0)
    {
        var projectName = title.split('_PROJECT_REVIEW');
        jQuery('#edit-submitted-project').val(projectName[0]);
    } else {
        return;
    }
}


jQuery(document).ready(
        function() {
            addNoteMessageArea();
            modifyProjectStyle();
            jQuery("input[name='op'][value='Submit']").click(
                    function() {
                        jQuery(".messages.error").remove();
                        var result = checkComments(technicalAbilitiesArr, 'comment');
                        result = checkComments(consultingSkillsArr, 'comment') && result;
                        result = checkComments(professionalismArr, 'comment') && result;
                        result = checkComments(leadershipArr, 'comment') && result;
                        result = checkComments(teamworkArr, 'comment') && result;
                        result = checkRequireField() && result;
                        result = checkProjectStartEndDate() && result;
                        if (!result)
                            goTopEx();
                        return result;
                    });
            initialProjectName();
        }
);


