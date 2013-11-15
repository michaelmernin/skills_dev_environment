
<h3 colspan="4"><?php print $reviewee; ?>'s Self Assessment </h3>
<?php
$counselor = get_counselor($reviewee);
$cur_user = get_current_user_name();
$identity = ($cur_user != $counselor)?'counselee': 'counselor';
if ($identity == 'counselee') {
  for ($i = 0; $i < count($dataset); $i++) {
    switch ($dataset[$i]['identifier']) {
      case '0':
        display_header($i, $dataset[$i]);
        break;

      case '1':
        display_subtitle($i, $dataset[$i]['question']);
        break;

      case '2':
        print '<div>';
        display_questions($i, $dataset[$i]['question']);
        if (in_array($dataset[$i]['rating'], array(0, 1, 2, 3, 4, 5))) {
          display_rating($i, $dataset[$i]['rating']);
        };
        display_comment($i, $dataset[$i]['comment']);
        print '</div>';
        break;

      case '3':
        display_fillform($i, $dataset[$i]);
        break;

      case '4':
        display_questions($i, $dataset[$i]['question']);
      	display_overall($i, $dataset[$i]['comment']);
      	break;

      default:
        break;
    }
  }
}
else {
  // counselor
  for ($i = 0; $i < count($dataset); $i++) {
    switch ($dataset[$i]['identifier']) {
      case '0':
        display_header($i, $dataset[$i]);
        break;

      case '1':
        display_subtitle($i, $dataset[$i]['question']);
        break;

      case '2':
        print '<div>';
        display_questions($i, $dataset[$i]['question']);
        if (in_array($dataset[$i]['rating'], array(0, 1, 2, 3, 4, 5))) {
          display_rating($i, $dataset[$i]['rating']);
        };
        display_comment($i, $dataset[$i]['comment']);
        display_counselor_rating_area($i);
        display_counselor_comment_area($i);
        print '</div>';
        break;

      case '3':
        display_fillform($i, $dataset[$i]);
        display_counselor_comment_area($i);
        break;

	    case '4':
	    display_questions($i, $dataset[$i]['question']);
	  	display_overall($i, $dataset[$i]['comment']);
	  	display_counselor_rating_area($i);
	  	break;

      default:
        break;
    }
  }
}
?>
<br />
<div>
<div class="draftbutton" style="float: left; margin-right: 15px;">
<!-- <a id="modal-912872" href="#modal-container-912872" >Disapprove</a> -->
<a id="modal-912872" href="#modal-container-912872" role="button" data-toggle="modal"><input formnovalidate="formnovalidate" class="webform-draft form-submit btn btn-large" type="submit" name="op" value="Disapprove"></a>
<div class="row-fluid">
  <div class="span12">
    <div id="modal-container-912872" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
        <h3 id="myModalLabel">
          Confirm to Disapprove.
        </h3>
      </div>
      <div class="modal-body">
        <p>
          Do you want to disapprove this self assessment? <br>
          Please write down reason below.
        </p>
        <textarea id="counselor_reject_reason" cols="30" rows="5" style="width: 500px" placeholder="Please write reasons here..."></textarea>
      </div>
      <div class="modal-footer">
        <img class="loading_img" id="status_loading_img" title="loading..." style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-danger" id="counselor_disapprove_btn" onclick="disapproveCounseleeSelfAssessment()">Disapprove</button>

      </div>
    </div>
  </div>
</div>
</div>
<div class="draftbutton" style="float: left; margin-right: 15px;">
<!-- <a id="modal-912871" href="#modal-container-912871" role="button" class="btn btn-danger" data-toggle="modal">Save as draft</a> -->
<a id="modal-912871" href="#modal-container-912871" role="button" data-toggle="modal"><input class="webform-submit button-primary form-submit btn btn-large btn-primary" type="submit" name="op" value="Submit"></a>
  <div class="row-fluid">
  <div class="span12">
    <div id="modal-container-912871" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_btn" value="0">×</button>
        <h3 id="myModalLabel">
          Confirm to Submit sheet.
        </h3>
      </div>
      <div class="modal-body">
        <p>
          Do you want to submit this form? <br>
          When you submit this form, all the contents cannot be revised!
        </p>
      </div>
      <div class="modal-footer">
        <img class="loading_img" id="status_loading_img" title="loading..." style="width: 25px; height: 25px; display: none" src="<?php print base_path() . drupal_get_path('theme', 'flat_ui') . '/assets/images/loading.gif' ?>">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button> <button class="btn btn-danger" id="counselor_submit_btn" onclick="submitcounselorassessment()">SAVE AS DRAFT</button>

      </div>
    </div>
  </div>
</div>
</div>
</div>

<div>
  <table class="table">

    <thead id="peer-review-form-overall-thead">
      <tr><th>Reviewer Composite Scores</th>
        <th>Self</th>
        <th>Counselor</th>
      </tr></thead>

    <tbody id="peer-review-form-overall-tbody">
      <tr><td>Client Engagements</td>
        <td id="rating_client_engagements">3</td>
        <td id="counselor_rating_client_engagements">3</td>
      </tr>
      <tr><td>Technical Abilities</td>
        <td id="rating_technical_abilities">3</td>
        <td id="counselor_rating_technical_abilities">3</td>

      </tr>
      <tr><td>Consulting Skills</td>
        <td id="rating_consulting_skills">3</td>
        <td id="counselor_rating_consulting_skills">3</td>
      </tr>
      <tr><td>Professionalism</td>
        <td id="rating_professionalism">3</td>
        <td id="counselor_rating_professionalism">3</td>
      </tr>
      <tr><td>Leadership</td>
        <td id="rating_leadership">3</td>
        <td id="counselor_rating_leadership">3</td>
      </tr>
      <tr><td>Teamwork</td>
        <td id="rating_teamwork">3</td>
        <td id="counselor_rating_teamwork">3</td>
      </tr>
      <tr><td>Internal Contributions</td>
        <td id="rating_internal_contributions">3.00</td>
        <td id="counselor_rating_internal_contributions">3.00</td>
      </tr>
      <tr><td>All</td>
        <td id="rating_all">3.00</td>
        <td id="counselor_rating_all">3.00</td>
      </tr>
    </tbody>
  </table>
</div>




<script type="text/javascript">
          var basePath = '<?php print $base_path = 'http://' . $_SERVER['HTTP_HOST'] . base_path(); ?>';
          var js_path = basePath + "sites/all/modules/counselee/js/annual_review_approve_overall.js";
          var new_element = document.createElement("script");
          new_element.setAttribute("type", "text/javascript");
          new_element.setAttribute("src", js_path);
          document.body.appendChild(new_element);

</script>
<input type="hidden" id="nid" value="<?php print $nid ?>"/>
<input type="hidden" id="count" value="<?php print count($dataset); ?>"/>

<script type="text/javascript">
// function displaySwitch() {
  jQuery(document).ready(function() {
    jQuery(".assessment-title").bind('click', function() {
      // jQuery(".assessment-title").click(function(){
      if (jQuery(this).siblings(".assessmentbubble").css("display") == "none") {
        jQuery(this).siblings(".assessmentbubble").css("display", "");
      }
      else {
        jQuery(this).siblings(".assessmentbubble").css("display", "none");
      }
      jQuery(this).siblings(".commentbubble").css("display", "none");
    });
    jQuery(".comment-title").bind('click', function() {
      // jQuery(".comment-title").click(function(){
      if (jQuery(this).siblings(".commentbubble").css("display") == "none") {
        jQuery(this).siblings(".commentbubble").css("display", "");
      }
      else {
        jQuery(this).siblings(".commentbubble").css("display", "none");
      }
      jQuery(this).siblings(".assessmentbubble").css("display", "none");
    });
    var isCounselor = '<?php print $identity ?>';
    if (isCounselor == "counselee") {
		jQuery(".draftbutton").hide();
    }
  });

</script>




<?php

function display_header($num, $header) {
  $content = '<h5 id="header-' . $num . '" class="assessment-header" value="' . $header['comment'] . '">' . $header['question'] . ': ' . $header['comment'] . '</h5>';
  print $content;
}

function display_subtitle($num, $subtitle) {
  $content = '<h6 id="subtitle-' . $num . '" class="assessment-subtitle" value="' . $subtitle . '">' . $subtitle . '</h6>';
  print $content;
}

function display_fillform($num, $fillform) {
  $title = '<h6 a id="fillform-' . $num . '" class="assessment-fillform">' . $fillform['question'] . '</h6>';
  $content = '<div id="fillform-content-' . $num . '" class="assessment-fillform-content">';
  if ($fillform['comment'] != '') {
    print $title . $content . '<div>' . $fillform['comment'] . '</div></<div></div>';
 }
  else {
    print $title . $content . '<div></div></div> <br />';
  }
}

function display_rating($num, $rating) {
  $title = '<div>	<a id="assessment-' . $num . '" class="assessment-title" style="cursor: pointer;">Rating</a> | 
					<a id="comment-' . $num . '" class="comment-title" style="cursor: pointer;">Comments</a>';
  $tab = '<div id="assessment-content-' . $num . '" class="assessmentbubble">';
  print $title . $tab;
  switch ($rating) {
    case '1':
      $content = '<div id="assessment-content-value-' . $num . '"> 1 </div>';
      print $content;
      break;

    case '2':
      $content = '<div id="assessment-content-value-' . $num . '"> 2 </div>';
      print $content;
      break;

    case '3':
      $content = '<div id="assessment-content-value-' . $num . '"> 3 </div>';
      print $content;
      break;

    case '4':
      $content = '<div id="assessment-content-value-' . $num . '"> 4 </div>';
      print $content;
      break;

    case '5':
      $content = '<div id="assessment-content-value-' . $num . '"> 5 </div>';
      print $content;
      break;

    case '0':
      $content = '<div id="assessment-content-value-' . $num . '"> N/A </div>';
      print $content;
      break;

    default:
      print 'no rating';
      break;
  }
  print '</div>';
}

function display_overall($num, $rating) {
  dd($num, 'num');
  dd($rating, 'rating');  
  $content = '<div id="overall-ratings-' . $num . '" value="' . $rating . '">' . $rating . '</div>';
  print $content;
}
function display_questions($num, $question) {
  $content = '<h6 id="question-' . $num . '" class="survey-question">' . $question . '</h6>';
  print $content;
}

function display_comment($num, $comment) {
  $tab = '<div id="comment-content-' . $num . '" class="commentbubble" style="display: none;">';
  if ($comment != 'NIL') {
    print $tab . '<div>' . $comment . '</div></div></div>';
  }
  else {
    print $tab . '<div>' . 'No comments!' . '</div></div></div> <br />';
  }
}

function display_counselor_rating_area($num) {
  $valueMap = array('0' => 'N/A', '1' => '1', '2' => '2', '3' => '3', '4' => '4', '5' => '5',);
  print '<select id="counselor-rating-' . $num . '">';
  for ($i = 0; $i < count($valueMap); $i++) {
    $content = '<option  value="' . $i . '"';
    if ($i == 3) {
      $content .= ' selected="selected"';
    }
    $content .= '>' . $valueMap[$i] . '</option>';
    print $content;
  }
  print '</select><br />';
}

function display_counselor_comment_area($num) {
  $comment = '<textarea id="counselor-comment-' . $num . '" cols="20" rows="5" placeholder="Please write your comments here..."></textarea> ';
  print $comment;
}
?>