/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function getRootPath() {
    var curWwwPath = window.document.location.href;
    var pos = curWwwPath.indexOf('EnterpriseReview');
    var localhostPath = curWwwPath.substring(0, pos);
    return localhostPath;
}

var bathpath = getRootPath();
var js_path = bathpath + "EnterpriseReview/sites/all/modules/counselee/js/peer_review_form.js";
var new_element = document.createElement("script");
new_element.setAttribute("type", "text/javascript");
new_element.setAttribute("src", js_path);
document.body.appendChild(new_element);

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

convertTextToDate();