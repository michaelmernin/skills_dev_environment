/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * To judge a string which can be convert a numeric.
 * @param {string} str A string
 * @return {bool} true,stand for is a numeric string,false stand for not
 * */
function IsNum(str)
{
    if (str != null && str != "" && str != ' ')
    {
        return !isNaN(str);
    }
    return false;
}


/**
 * Calculate the average score
 * @param {array} category The elements array
 * @param {string} elementType The element type 
 * @return {number} The average score
 * */
function calculate_average_score(category, elementType)
{
    var i = 0, count = 0, sum = 0, value, averageScore;
    for (i = 0; i < category.length; i++)
    {
        if (elementType == 'select')
            value = jQuery(category[i]).find('option:selected').val();
        else if (elementType == 'html')
            value = jQuery(category[i]).html();

        if (IsNum(value) && value != '0')
        {
            count++;
            sum += parseFloat(value);
        }
    }

    var averageScore = '';
    if (count != 0)
    {
        averageScore = (sum / count).toFixed(2);
    }
    return averageScore;
}


/**
 * When the socre is not equal three,check whether wrote comments.
 * If not worte comment,focus the element.
 * 
 * @param {array} category The elements id array
 *                  
 * @return {bool} 
 * */
function checkComment(category, commentSuffix)
{
    var value, commentLen, commentID, isRight = true;
    for (var i = 0; i < category.length; i++)
    {
        value = jQuery(category[i]).find('option:selected').val();
        if (value != '3')
        {
            commentID = category[i] + commentSuffix;
            commentLen = jQuery(commentID).val().length;
            if (commentLen < 1) {
                alert('The socre is not 3 points.Please enter comment!');
                jQuery(comments[i]).focus();
                jQuery(comments[i]).removeClass().addClass("form-textarea required error");
                isRight = false;
                return isRight
            }
            else
            {
                jQuery(comments[i]).removeClass().addClass("form-textarea");
            }
        }
    }
    return isRight;
}



/**
 * Modify the average points.
 * 
 * */
function modifyElementValue(category, value)
{
    var i;
    for (i = 0; i < category.length; i++)
    {
        jQuery(category[i]).html(value);
    }
}


/**
 * Register the value change event
 * 
 * */
function registerValueChangeEvent()
{

}



 function initialOverScore()
  {
    var category = new Array();
    category[0] = 'client_engagements';
    category[1] = 'technical_abilities';
    category[2] = 'consulting_skills';
    category[3] = 'professionalism';
    category[4] = 'leadership';
    category[5] = 'teamwork';

    for (var i = 0; i < category.length; i++)
    {
      jQuery('#rating_' + category[i]).html(jQuery('#src_self_' + category[i]).html());
      jQuery('#counselor_rating_' + category[i]).html(jQuery('#src_counselor_' + category[i]).html());
    }

    var selfPre = '#src_self_';
    var self_internal = new Array();
    self_internal[0] = selfPre + 'business_development';
    self_internal[1] = selfPre + 'career_counseling';
    self_internal[2] = selfPre + 'recruiting_assistance';
    self_internal[3] = selfPre + 'internal_contributions';
    self_internal[4] = selfPre + 'perficient_basics';

    var self_internal = calculate_average_score(self_internal, 'html');
    //rating_internal_contributions
    jQuery('#rating_internal_contributions').html(self_internal);

    var counselorPre = '#src_counselor_';
    var counselor_internal = new Array();
    counselor_internal[0] = counselorPre + 'business_development';
    counselor_internal[1] = counselorPre + 'career_counseling';
    counselor_internal[2] = counselorPre + 'recruiting_assistance';
    counselor_internal[3] = counselorPre + 'internal_contributions';
    counselor_internal[4] = counselorPre + 'perficient_basics';


    var counselor_internal = calculate_average_score(counselor_internal, 'html');
    jQuery('#counselor_rating_internal_contributions').html(counselor_internal);


    category[6] = 'internal_contributions';

    var self_all = new Array();
    var counselor_all = new Array()
    for (var i = 0; i < category.length; i++)
    {
      self_all[i] = '#rating_' + category[i];
      counselor_all[i] = '#counselor_rating_' + category[i];
    }

    var self_all_score = calculate_average_score(self_all, 'html');
    jQuery('#rating_all').html(self_all_score);

    var counselor_all_score = calculate_average_score(counselor_all, 'html');
    jQuery('#counselor_rating_all').html(counselor_all_score);

  }