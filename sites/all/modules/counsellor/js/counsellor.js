

function hideConfirmdialog() {
    jQuery('#submit_button').removeAttr("disabled");
    jQuery('#close_btn').click();
    jQuery('#status_loading_img').hide();
    jQuery('#submit_button').removeClass();
    jQuery('#submit_button').addClass("btn btn-danger");
}

function clickSubmitButton() {
    jQuery('#status_loading_img').show();
    jQuery('#submit_button').attr("disabled", "disabled");
    jQuery('#submit_button').removeClass();
    jQuery('#submit_button').addClass("btn btn-primarybtn-block disabled");
}


function switchProjectName(flag) {
    if (flag == 1) {
        jQuery('#Project_Name_Lable').show();
        jQuery('#Project_Name_Text').show();
    } else {

        jQuery('#Project_Name_Lable').hide();
        jQuery('#Project_Name_Text').hide();
    }
}

function watch_reveiw_status(url) {
    window.location.href = url;
}

function start_review(review_id, basepath) {
    jQuery('#start_button_' + review_id).hide();
    jQuery('#status_loading_img_' + review_id).show();
    jQuery.ajax({
        type: "POST",
        url: basepath + 'newreview/startreview',
        data:{'review_id':review_id},
        success: function(date) {
            if (date != '-1')
            {
                jQuery('#review_status_' + review_id).html('<a href="javascript:{void(0)}" title="This review is in draft, reviewer can edit it before submit.">Review in draft</a>');
            }
            else {
               window.location.href = basepath+ 'mydashboard';
            }
        }
    });
}