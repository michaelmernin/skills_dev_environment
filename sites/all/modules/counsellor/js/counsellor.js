

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
function counselor_approve_clickSubmitButton() {
    jQuery('#approve_status_loading_img').show();
    jQuery('#counselor_submit_btn').attr("disabled", "disabled");
    jQuery('#counselor_submit_btn').removeClass();
    jQuery('#counselor_submit_btn').addClass("btn btn-primarybtn-block disabled");
}
function counselor_disapprove_clickSubmitButton() {
    jQuery('#disapprove_status_loading_img').show();
    jQuery('#counselor_disapprove_btn').attr("disabled", "disabled");
    jQuery('#counselor_disapprove_btn').removeClass();
    jQuery('#counselor_disapprove_btn').addClass("btn btn-primarybtn-block disabled");
}


function switchProjectName(flag) {
    if (flag == 1) {
        jQuery('#Project_Name_Lable').show();
        jQuery('#Project_Name_Text').show();
				jQuery("#Project_Client_Lable").show();
				jQuery("#Project_Client_Text").show();
				jQuery(".project_review").show();
    } else {

        jQuery('#Project_Name_Lable').hide();
        jQuery('#Project_Name_Text').hide();
				jQuery("#Project_Client_Lable").hide();
				jQuery("#Project_Client_Text").hide();
				jQuery(".project_review").hide();
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
        data: {'review_id': review_id},
        success: function(date) {
            if (date != '-1')
            {
                jQuery('#review_status_' + review_id).html('<a href="javascript:{void(0)}" title="This review is in draft, reviewer can edit it before submit.">Review in draft</a>');
            }
            else {
                window.location.href = basepath + 'mydashboard';
            }
        }
    });
}



function get_review_status_tiplink(status) {
    var content = '';
    switch (status) {
        case 1:
            //1 for review in draft;
            content = "<a href=\"javascript:{void(0)}\" title=\"This review is in draft, reviewer can edit it before submit.\" style=\"cursor: default\">Review in Draft</a>";
            break;
        case 2:
            // 2 for review by counsellor;
            content = "<a href=\"javascript:{void(0)}\" title=\"This review now is review by counselor.\" style=\"cursor: default\">Review by Counselor</a>";
            break;
        case 3:
            // 3 for approved by counsellor;
            content = "<a href=\"javascript:{void(0)}\" title=\"This review is approved by counselor.\" style=\"cursor: default\">Approved by Counselor</a>";
            break;
        case 4:
            // 4 for joint review;
            content = "<a href=\"javascript:{void(0)}\" title=\"Joint review.\" style=\"cursor: default\">Joint review</a>";
            break;
        case 5:
            // 5 for GM review;
            content = "<a href=\"javascript:{void(0)}\" title=\"This review is review by GM.\" style=\"cursor: default\">GM Review</a>";
            break;
        case 6:
            // 6 for GM approved;
            content = "<a href=\"javascript:{void(0)}\" title=\"This review is approved by GM.\" style=\"cursor: default\">GM Approve</a>";
            break;
    }
    return content;

}

function get_review_type_tiplink(status) {
    var review_type = '';
    switch (status) {
        case 0:
            //  0 for annual review;
            review_type = 'Annual review';
            break;
        case 1:
            // 1 for project review;
            review_type = 'Project review';
            break;
        case 2:
            // 2 for 3-month review
            review_type = 'Three-month review';
            break;
    }
    return review_type;
}
function rowhint(rowId, val, rawObject) {

    return 'title="Click to see more information about this review."';
}
function statusrowhint(rowId, val, rawObject) {
    var status = rawObject[8];
    var content = '';
    switch (status) {
        case '0':
            content = 'title="Click start review to start."';
            break;
        case '1':
            //1 for review in draft;
            content = 'title="This review is in draft, reviewer can edit it before submit."';
            break;
        case '2':
            // 2 for review by counsellor;
            content = 'title="This review now is review by counselor."';
            break;
        case '3':
            // 3 for approved by counsellor;
            content = 'title="This review is approved by counselor."';
            break;
        case '4':
            // 4 for joint review;
            content = 'title="Joint review."';
            break;
        case '5':
            // 5 for GM review;
            content = 'title="This review is review by GM."';
            break;
        case '6':
            // 6 for GM approved;
            content = 'title="This review is approved by GM."';
            break;
    }
    return content;
}

function startReview(rreid,baseurl) {
    var url=baseurl+'newreview/startreview';
    jQuery('#btnStartReview' + rreid).hide();
    jQuery('#status_loading_img' + rreid).show();
//    alert(url);
    jQuery.ajax({
        type: "POST",
        data: {'review_id': rreid},
        url: url,
        success: function(text) {
            if (text !== '-1') {
                jQuery("#userStatusTable").trigger("reloadGrid");
            } else {
                window.location.href = baseurl + "mydashboard";
                return;
            }
        },
        error: function(text) {
            window.location.href = baseurl + "mydashboard";
            return;
        }
    });
//    jQuery("#userStatusTable").trigger("reloadGrid");

}