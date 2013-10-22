<script type = "text/javascript">
jQuery(function(){
    renderMultiselect();
});

function destoryMultiselect(){
    $('.multiselect').multiselect('destroy');
}

function renderMultiselect(){
    $(".multiselect").multiselect({
        sortable: true, 
        searchable: true, 
        dividerLocation: 0.6
    });
}
</script>
//function submitFeedbackProvider(){
//    var val = "";
//    var name = "";
//    var count = 0;
//    $("#users option:selected").each(function() {
//        val += $(this).val()+ ",";
//        count++;
//    });
//    if(count > 5){
//        alert("You should choose at most 5 people!");
//        return false;
//    }
//    if(count < 3){
//        alert("You should choose at least 3 people!");
//        return false;
//    }
//    $("#users option:selected").each(function() {
//        name += $(this).text()+ "";
//    });
//    
//    $.ajax({
//        type: "POST",
//        url: 'http://localhost/360review/myworkingstage/submitfeedbackprovider',
//        success: function(msg){
//            alert(msg);
//        }
//    });
//}

//function submitParticipant(){
//    var val = "";
//    var name = "";
//    var count = 0;
//    $("#users option:selected").each(function() {
//        val += $(this).val()+ ",";
//        count++;
//    });
//    $("#users option:selected").each(function() {
//        name += $(this).text()+ "";
//    });
//    
//    $.ajax({
//        type: "POST",
//        url: 'http://localhost/360review/myworkingstage/submitfeedbackprovider',
//        success: function(msg){
//            alert(msg);
//        }
//    });
//}