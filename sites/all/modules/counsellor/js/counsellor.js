

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
