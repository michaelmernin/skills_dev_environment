

function hideConfirmdialog() {
    jQuery('#submit_button').removeAttr("disabled");
    jQuery('#close_btn').click();
    jQuery('#status_loading_img').hide();
    jQuery('#submit_button').removeClass();
    jQuery('#submit_button').addClass("btn btn-primary");
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
        url: basepath + 'newreview/startreview/' + review_id,
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

//function return_status_string(status, startid, base_path) {
//    var content="";
//    switch (status) {
//        case '0':
//            //0 for New review start
////            content = "<button id=\"start_button_" + startid + "\" class=\"btn btn-danger btn-sm\" onclick=\"start_review(" + startid + "," + base_path")\" title=\"Click to start this review.\" style=\"color:#ffffff;font-size:15px;\">Start riview</button>";
////            content = '<button id="start_button_' + startid +  '" class="btn btn-danger btn-sm" onclick="start_review(' + startid + ',' + '"'+base_path+'")" title="Click to start this review." style="color:#ffffff;font-size:15px;">Start riview</button>';
//           content='';
//            break;
//        case 1:
//            //1 for review in draft;
//            content = "<a href=\"javascript:{void(0)}\" title=\"This review is in draft, reviewer can edit it before submit.\">Review in draft</a>";
//            break;
//        case 2:
//            // 2 for review by counsellor;
//            content = "<a href=\"javascript:{void(0)}\" title=\"This review now is review by counselor.\">Review by counselor</a>";
//            break;
//        case 3:
//            // 3 for approved by counsellor;
//            content = "<a href=\"javascript:{void(0)}\" title=\"This review is approved by counselor.\">Counselor approved</a>";
//            break;
//        case 4:
//            // 4 for joint review;
//            content = "<a href=\"javascript:{void(0)}\" title=\"Joint review.\">Joint review</a>";
//            break;
//        case 5:
//            // 5 for GM review;
//            content = "<a href=\"javascript:{void(0)}\" title=\"This review is review by GM.\">GM review</a>";
//            break;
//        case 6:
//            // 6 for GM approved;
//            content = "<a href=\"javascript:{void(0)}\" title=\"This review is approved by GM.\">GM approved</a>";
//            break;
//  
//    }
//    return content;
//}