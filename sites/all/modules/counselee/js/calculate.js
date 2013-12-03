/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function goTopEx() {
    //window.scrollTo(0, 0)
    var obj = jQuery("#gotoTopBtn")[0];
    obj.click();
}
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
 * Get the category item name
 * 
 * */
function getCategoryChildName(idValue)
{
    var arr = idValue.split("-");
    var childName = '', len, i;
    for (len = arr.length, i = len - 1; i >= 0; i--)
    {
        if (arr[i] != 'category')
            childName = arr[i].substring(0, 1).toUpperCase()
                    + arr[i].substring(1, arr[i].length)
                    + ' ' + childName;
        else
            break;
    }
    return childName;
}

/**
 * Calculate the average score
 * @param {array} category The elements array
 * @param {string} elementType The element type 
 * @return {number} The average score
 * */
function calculateAverageScore(category, elementType)
{
    var i = 0, count = 0, sum = 0, value, averageScore = '';
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
    if (count != 0)
    {
        averageScore = (sum / count).toFixed(2);
    }
    return averageScore;
}


/**
 * Register the select onchange event!
 * 
 * */
function registerSelectOnchangeEvent(items, onchangeEvent)
{
    for (var i = 0; i < items.length; i++)
    {
        jQuery(items[i]).change(onchangeEvent);
    }
}


/**
 * When the socre is not equal three,check whether wrote comments.
 * If not worte comment,focus the element.
 * 
 * @param {array} category The elements id array
 *                  
 * @return {bool} 
 * */
function checkComments(category, commentSuffix)
{
    var score, commentLen, commentID, li, i, field, isRight = true;
    for (i = 0; i < category.length; i++)
    {
        score = jQuery(category[i]).find('option:selected').val();
        commentID = category[i] + "-" + commentSuffix;
        jQuery(commentID).removeClass().addClass("form-textarea");
        if (score != '3')
        {
            commentLen = jQuery(commentID).val().length;
            if (commentLen < 1) {
                addErrorMessageArea();
                field = getCategoryChildName(category[i]);
                li = '<li>' + field + "Comment field is required,because his score is not 3 point." + '</li>';
                jQuery("#error-message").append(li);
                jQuery(commentID).addClass('form-textarea required error');
                isRight = false;
            }
        }
    }
    return isRight;
}

/**
 * Modify the average points.
 * 
 * */
function modifyElementValue(arr, value)
{
    for (var i = 0; i < arr.length; i++)
    {
        jQuery(arr[i]).html(value);
    }
}


function getSamePrefixID(prefix)
{
    var id, samePrefixArr = new Array();
    var arr = jQuery('select');
    for (var i = 0; i < arr.length; i++)
    {
        id = arr[i].getAttribute("id");
        if (id.indexOf(prefix) != -1)
        {
            samePrefixArr[samePrefixArr.length] = "#" + id;
        }
    }
    return samePrefixArr;
}

/**
 * Check required field
 * 
 * */
function checkRequireField()
{
    var arr = jQuery("label > span"), obj, fieldId, description, isRight = true, li;
    for (var i = 0; i < arr.length; i++)
    {
        if (arr[i].innerText == "*")
        {
            obj = arr[i].parentNode;
            fieldId = "#" + obj.getAttribute("for");
            description = obj.innerText;
            addErrorMessageArea();
            jQuery(fieldId).removeClass().addClass("form-text required");


            if (getElementContentLength(jQuery(fieldId)) < 1)
            {
                li = '<li>' + description.substring(0, description.length - 1) + " field is required." + '</li>';
                jQuery("#error-message").append(li);
                jQuery(fieldId).addClass('form-textarea required error');
                isRight = false;
            }
        }
    }
    return isRight;
}


function getElementContentLength(ele)
{
    var type = ele[0].tagName;
    if (type == 'INPUT')
        return ele.val().length;
    else if (type == "TEXTAREA")
        return ele.val().length;

    return 0;
}


function addErrorMessageArea()
{
    //.page-title
    var errorArea = jQuery(".messages.error");
    if (errorArea.length == 0)
    {
        var position = jQuery('.page-title');
        var content = '<div class="messages error">'
                + '<h2 class="element-invisible">Error message</h2>'
                + '<ul id="error-message"></ul></div>';
        position.after(content);
    }
}

function focusOnErrorMessage()
{
    var errorArea = jQuery(".messages.error");
    //if (errorArea.length != 0)
//    window.scrollTo(0, 0);

    //jQuery("html,body").animate({"scrollTop": 0}, speed);

    // jQuery("html,body").animate({"scrollTop": 0}, 1000);
}

function modifySelectCategoryValue(category, destArr)
{
    var averageScore = calculateAverageScore(category, 'select');
    modifyElementValue(destArr, averageScore);
}

/**
 * Calculate the overall score average
 * */
function modifyOverallScoreAverage(category, destArr)
{
    var averageScore = calculateAverageScore(category, 'html');
    modifyElementValue(destArr, averageScore);
}