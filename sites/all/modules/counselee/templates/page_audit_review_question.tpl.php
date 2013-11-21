<div class="webform-submission-info clearfix">

  <div>  <?php print $node['description']; ?> 
  </div>
  <div class="wellwarp">
    <div style="width:200px">
      <b style="font-weight: 700"> Self Rating:</b>
      <font color="red"> <?php print $node['self_rating']; ?></font>  
    </div>
    <div style="width:200px">
      <b style="font-weight: 700"> Counselor Rating: </b>
      <?php print $node['counselor_rating']; ?>
    </div>
    <div style="width:200px">
      <b style="font-weight: 700">
        Peer Rating: 
      </b>   <?php print $node['peer_rating']; ?>
    </div>
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

  <div class="wellwarp">
    <b style="font-weight: 700">
      Peer Comment:
    </b> <?php print $node['peer_comment']; ?>
  </div>




