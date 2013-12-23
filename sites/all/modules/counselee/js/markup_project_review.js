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


function convertTextfieldToDateInput()
{
    var startDate = getCommonNameId("start-date", 'input');
    var endDate = getCommonNameId("end-date", 'input');
    textfieldToDateInput(startDate);
    textfieldToDateInput(endDate);
}

var bathpath = getRootPath();
var jsPath = bathpath + "EnterpriseReview/sites/all/modules/counselee/js/peer_review_form.js";
addJavaScript(jsPath);
convertTextfieldToDateInput();


jQuery(document).ajaxComplete(
        function() {
            convertTextfieldToDateInput();
        }
);
