
function checkCounselorComment()
{
    var ratings = getCommonNameId("counselor-rating", "select");
    var comments = getCommonNameId("counselor-comment", "textarea");

    var i, len = ratings.length, score, comment, field, isRight = true, formKey;
    for (i = 0; i < len; i++)
    {
        score = getElementValue(jQuery(ratings[i]));
        comment = getElementValue(jQuery(comments[i]));
        jQuery(comments[i]).attr("style", "margin: 4px 0px 0px; height: 112px; width: 98%;");
        if ((score != '3' || score != 3) && comment.trim().length < 1)
        {
            addErrorMessageArea();
            formKey = jQuery(comments[i]).attr('form-key');
            field = getFormKeyName(formKey);
            li = '<li>' + field + "Comment field is required,because his score is not 3 point." + '</li>';
            jQuery("#error-message").append(li);
            jQuery(comments[i]).attr("style", "margin: 4px 0px 0px; height: 112px; width: 98%;border: 2px solid red;");
            isRight = false;
        }
    }
    return isRight;
}


/**
 * Check Counselor Required Field
 * 
 * */
function checkCounselorRequireField()
{
    var comments = getSameFormKeyId("text", "textarea");
    var i, len = comments.length, comment, field, isRight = true, formKey;
    for (i = 0; i < len; i++)
    {
        comment = getElementValue(jQuery(comments[i]));
        jQuery(comments[i]).attr("style", "margin: 4px 0px 0px; height: 112px; width: 98%;");
        if (comment.trim().length < 1)
        {
            addErrorMessageArea();
            formKey = jQuery(comments[i]).attr('form-key');
            field = getFormKeyName(formKey);
            li = '<li>' + field + " field is required." + '</li>';
            jQuery("#error-message").append(li);
            jQuery(comments[i]).attr("style", "margin: 4px 0px 0px; height: 112px; width: 98%;border: 2px solid red;");
            isRight = false;
        }
    }
    return isRight;
}

jQuery(document).ready(
        function() {
            jQuery("input[name='op'][value='Approve']").click(
                    function() {
                        jQuery(".messages.error").remove();
                        var result = checkCounselorComment();
                        result = checkCounselorRequireField() && result;
                        if (!result)
                            goTopEx();
                        return result;

                    });
            jQuery("#counselor_disapprove_btn").click(function() {
                return check_reject_reasons();
            })
        }
);

function check_reject_reasons() {
    var comment = jQuery.trim(jQuery("#counselor_reject_reason").val());
    var len = comment.length;
    if (len < 1) {
        alert('Please write down reason. If you are going to reject, this area cannot be blank!');
        jQuery("#counselor_reject_reason").focus();
        disapprovehidalert();
        return false;
    }
    return true;
}

function disapprovehidalert() {
    jQuery('#counselor_disapprove_btn').removeAttr("disabled");
    jQuery('#disapprove_status_loading_img').hide();
    jQuery('#counselor_disapprove_btn').removeClass();
    jQuery('#counselor_disapprove_btn').addClass("btn btn-danger");
}