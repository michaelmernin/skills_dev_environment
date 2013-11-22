<?php 
/*
	This template is for each survey item. All arguments this template needs are: 
	$item_num -- the item number
	$item_question -- the item question
	$item_rating -- the item self rating
	$item_comment -- the item self comment
	$item_peer_comments -- the synthesized peer comments
*/
?>
<div class="well sidebar-nav">
<h6 id="header-<?php print $item_num; ?>" class="survey-question">
	<?php print $item_question; ?>  1. Client Engagements - provided high levels of service to client satisfaction; built client confidence'
</h6>
<br />
<div id="rating-area-<?php print $item_num; ?>" class="rating area">
	<div><a id="rating-title-<?php print $item_num; ?>" class="rating-title">Rating: </a>
		<div id="assessment-content-value-<?php print $item_num; ?>" value="<?php print $item_rating ?>"> <?php print $item_rating; ?> 
		</div>
	</div>
	<div><a id="counselor-rating-<?php print $item_num; ?>" class="counselor-rating">Counselor Rating: </a>
		<select id="counselor-rating-<?php print $item_num; ?>">
			<option value="0"> N/A </option>
			<option value="1"> 1 </option>
			<option value="2"> 2 </option>
			<option value="3" selected="selected"> 3 </option>
			<option value="4"> 4 </option>
			<option value="5"> 5 </option>
		</select>
	</div>
</div>
<div id="comment-area-<?php print $item_num; ?>" class="comment-area">
	<div><a id="comment-title-<?php print $item_num; ?>" class="comment-title">Comment: </a>
		<div id="comment-content-value-<?php print $item_num; ?>" value="<?php print $item_comment ?>"> <?php print $item_comment ?>
		</div>
	</div>
	<div><a id="peer-comments-<?php print $item_num ?>" class="peer-comments">Peer Comments: </a>
		<div id="peer-comment-<?php print $item_num ?>" class="peer-comments-content"> <?php print $item_peer_comments; ?>
		</div>
	</div>
	<div><a id="counselor-comment-<?php print $item_num; ?>" class="counselor-comment">Counselor Comment: </a>
		<textarea id="counselor-comment-<?php print $item_num; ?>" cols="20" rows="5">
		</textarea>
	</div>
</div>
<div id="pie-chart-area-<?php print $item_num ?>" class="pie-chart-area">
	<!-- Remained -->
</div>
</div>

