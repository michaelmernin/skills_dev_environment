/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 * Get the server root path
 * 
 * */
function getRootPath() {
    var curWwwPath = window.document.location.href;
    var pos = curWwwPath.indexOf('EnterpriseReview');
    var localhostPath = curWwwPath.substring(0, pos);
    return localhostPath;
}

/**
 *Add js to current page
 *@param {string} jsPath The location of the JavaScript Path
 *
 * */
function addJavaScript(jsPath)
{
    var new_element = document.createElement("script");
    new_element.setAttribute("type", "text/javascript");
    new_element.setAttribute("src", jsPath);
    document.body.appendChild(new_element);
}

/**
 * Convert Textfield to Date Input 
 * 
 * */
function convertTextfieldToDateInput()
{
    var startDate = getCommonNameId("start-date", 'input');
    var endDate = getCommonNameId("end-date", 'input');
    var project = getCommonNameId("edit-submitted-engagement-summary-category-project",'input');
    textFieldReadOnly(startDate);
    textFieldReadOnly(endDate);
    textFieldReadOnly(project);
}

/**
 * Make date input field read only
 * */
function textFieldReadOnly(idArr)
{
    for (var i = 0; i < idArr.length; i++)
    {
        jQuery(idArr[i]).attr("readonly", true);
        jQuery(idArr[i]).css('color', '#34495E');
        jQuery(idArr[i]).css('background', '#ffffff');
    }
}



var rootPath = getRootPath();
var jsPath = rootPath + "EnterpriseReview/sites/all/modules/counselee/js/peer_review_form.js";
addJavaScript(jsPath);
convertTextfieldToDateInput();



/**
 * initial project start Date and End date
 * 
 * */
function initialStartEndDate()
{
    var href = window.location.href;
    var params = href.split('/');
    var rreid = 0;
    for (var i = 0; i < params.length; i++)
    {
        if (params[i] == 'selfassessment')
        {
            rreid = params[i + 1]
        }
    }
    
    jQuery.ajax({
        type: "POST",
        url: rootPath + 'EnterpriseReview/watchstatus/get-projects-msg',
        data: {'rreid': rreid},
        success: function(date) {
            var obj = JSON.parse(date);  
            jQuery("#edit-submitted-engagement-summary-category-project").val(obj.project_name);
            jQuery('#edit-submitted-engagement-summary-category-start-date').val(obj.time_frame_from);
            jQuery('#edit-submitted-engagement-summary-category-end-date').val(obj.time_frame_to);
            
        }
    });
}

initialStartEndDate()


jQuery(document).ajaxComplete(
        function() {
            convertTextfieldToDateInput();
        }
);
