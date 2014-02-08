/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 * Click the anchor to back to top
 * */
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
 * @param {string} idValue The catagory id
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
 * Get field name from form key
 * @param {stirng} value The value of the form key
 * @return {string} The form key stand for field name
 * */
function getFormKeyName(value)
{
    var arr = value.split("_");
    var childName = '', len, i;
    for (len = arr.length, i = len - 1; i >= 0; i--)
    {
        if (arr[i] != 'text')
            childName = arr[i].substring(0, 1).toUpperCase()
                    + arr[i].substring(1, arr[i].length)
                    + ' ' + childName;
    }
    return childName;
}


/**
 * According to part of the form key id.Get the list of the form-key ID.
 * 
 * */
function getSameFormKeyId(same, type)
{
    var id, idArr = new Array();
    var arr = jQuery(type);
    for (var i = 0; i < arr.length; i++)
    {
        id = arr[i].getAttribute("id");
        formKey = arr[i].getAttribute("form-key");
        if (formKey != null && formKey.indexOf(same) != -1)
        {
            idArr[idArr.length] = "#" + id;
        }
    }
    return idArr;
}


/**
 * Calculate the average score
 * @param {array} category The elements array
 * @return {number} The average score
 * */
function calculateAverageScore(category)
{
    var i = 0, count = 0, sum = 0, value, averageScore = 'N/A';
    for (i = 0; i < category.length; i++)
    {
        value = getElementValue(jQuery(category[i]));
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
 * Register the select onchange event
 * @param {array} items The select element id
 * @param {sting} onchangeEvent The register onchage event
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
        if (score != '3' || score != 3)
        {
            commentLen = jQuery(commentID).val().length;
            if (commentLen < 1) {
                addErrorMessageArea();
                field = getCategoryChildName(category[i]);
                li = '<li>' + field + "Comment field is required,because his score is not 3 point." + '</li>';
                jQuery("#error-message").append(li);
                jQuery(commentID).removeClass().addClass('form-textarea required error');
                isRight = false;
            }
        }
    }
    return isRight;
}

/**
 * Modify the destination array element value
 * 
 * */
function modifyElementValue(destArr, value)
{
    for (var i = 0; i < destArr.length; i++)
    {
        jQuery(destArr[i]).html(value);
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
 * Get the id have comment part id.
 * @param {string} same The same part of the id
 * @param {string} type The element type
 * 
 * */
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
            jQuery(fieldId).removeClass().addClass("form-text required");
            if (getElementContentLength(jQuery(fieldId)) < 1)
            {
                addErrorMessageArea();
                li = '<li>' + description.substring(0, description.length - 1) + " field is required." + '</li>';
                jQuery("#error-message").append(li);
                jQuery(fieldId).addClass('form-textarea required error');
                isRight = false;
            }
        }
    }
    return isRight;
}

/**
 * check the project start date and end date
 * 
 * */
function checkProjectStartEndDate()
{
    var startDate = getCommonNameId("start-date", 'input');
    var endDate = getCommonNameId("end-date", 'input');
    var len = startDate.length;
    var start, end, startStr, endStr, li, isLegal = true;
    for (var i = 0; i < len; i++)
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



/**
 * Get the length of the element value
 * @param {object} ele description
 * @return {int} The length of the element content
 * 
 * */
function getElementContentLength(obj)
{
    var value = getElementValue(obj);
    return value.length;
}

/**
 * Get page element value
 * 
 * @param {object} obj Element Object
 * */
function getElementValue(obj) {
    var type = obj[0].tagName;
//    console.log(type);

    if (type == "INPUT")
        return obj.val();
    else if (type == "TEXTAREA")
        return obj.val();
    else if (type == "SELECT")
        return obj.find('option:selected').val();
    else if (type == "DIV")
        return obj.html();
    else if (type == "LABLE")
        return obj.html();
    else if (type == "TD")
        return obj.html();
    else if (type == "SPAN")
        return obj.html();

    return obj.val();
}



/**
 * Change textfield to Date Input
 * @param {array} idArr Textfield elment id array
 * */
function textfieldToDateInput(idArr)
{
    for (var i = 0; i < idArr.length; i++)
    {
        jQuery(idArr[i]).attr("readonly", true);
        jQuery(idArr[i]).css('color', '#34495E');
        jQuery(idArr[i]).css('background', '#ffffff');
        jQuery(idArr[i]).datepicker({
            firstDay: 1,
            changeMonth: true,
            changeYear: true
        });
    }
}



/**
 * Add Error Message display area on webform page
 * 
 *      Add Error Message Area after the class of the element equal "page-title"
 *      Add Error Message Area that his class equals with "message error"
 * 
 * */
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


/**
 *Calculate category average,and set the average to the destArr
 *@param {array} category The category id array 
 *@param {array} destArr  The modified element id array 
 * */
function modifyCategoryValue(category, destArr)
{
    var averageScore = calculateAverageScore(category);
    modifyElementValue(destArr, averageScore);
}


/**
 *Set counselee and counselor overall rating
 * */
function setCounselorOverallRating() {

    var counselorSrc = jQuery("#counselor-overall_rating-0");
    var counselorDest = jQuery("#counselor-overall_rating-content");
    var overallRating;

    if (counselorSrc.length <= 0)
        overallRating = 3;
    else { 
        overallRating = counselorSrc.val();
				if (overallRating < 1 || overallRating > 5) {
					overallRating = 3;
				}
		}
    if (counselorDest.length > 0)
    {
        if (counselorDest[0].tagName == 'SELECT')
            counselorDest.val(overallRating);
        else if (counselorDest.text() == '') {
						counselorDest.append(overallRating);
				}
				else {
						// Nothing to do, preventing duplicated appending score.
				}
    }
}

function setCounseleeOverallRating() {
    var overallRating = jQuery("#counselee-overall_rating-0").val();
		var counseleeDest = jQuery("#counselee-overall_rating-content");
    if (overallRating == "undefined")
        counseleeDest.append("3");

    if (counseleeDest[0].tagName == 'SELECT')
        counseleeDest.val(3);
    else if (counseleeDest.text() == '') {
        counseleeDest.append(overallRating);
		}
		else {
			// Nothing to do, preventing duplicate appending score.
		}
}
         
