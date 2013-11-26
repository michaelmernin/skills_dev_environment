/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * To determine whether a string is a numeric string
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
    return 'dddddd';
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
            sum += parseInt(value);
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