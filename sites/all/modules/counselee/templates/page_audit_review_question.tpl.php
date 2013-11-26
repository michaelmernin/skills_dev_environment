<div class="webform-submission-info clearfix">

  <div>
    <?php print $node['description']; ?> 
  </div>

  <div class="wellwarp">
    <b style="font-weight: 700"> Self Rating:</b>
    <font color="red">
    <span id="<?php print 'src_self_' . $node['key']; ?>" >
      <?php print $node['self_rating']; ?>
    </span>
    </font>  

    <b style="font-weight: 700">
      | ·Counselor Rating: </b>
    <font color="red">
    <span id="<?php print 'src_counselor_' . $node['key']; ?>" >
      <?php print $node['counselor_rating']; ?>
    </span>
    </font>



    <?php
    if ($node['exist_peer'] == true) {
      print '<b style="font-weight: 700"> | ·Peer Rating: </b>  <font color="red">';
      print $node['peer_rating'];
      print '</font>';
    }
    ?>

  </div>


  <div class="wellwarp">
    <b style="font-weight: 700">
      Self Comment:
    </b> <?php print $node['self_comment']; ?>
  </div>

  <div class="wellwarp">
    <b style="font-weight: 700">
      Counselor Comment:
    </b> <?php print $node['counselor_comment']; ?>
  </div>

  <?php
  if ($node['exist_peer'] == true) {
    print '<div class="wellwarp"><b style="font-weight: 700">Peer Comment:</b>';
    print $node['peer_comment'];
    print '</div>';
  }
  ?>
</div>
<hr>



