
<!--webform-submission-info clearfix-->
<!--well sidebar-nav-->
<hr>
<div class="webform-submission-info clearfix">
  <table class="table">
    <caption>
      <h3>
        <?php print $node['description']; ?>
      </h3>
    </caption>

    <tbody>
      <tr >
        <td>
          <b style="font-weight: 700"> Self Rating:</b>
          <?php print $node['self_rating']; ?>      
        </td>
        <td>
          <b style="font-weight: 700"> Counselor Rating: </b>
          <?php print $node['counselor_rating']; ?>
        </td>
        <td>
          <b style="font-weight: 700">
            Peer Rating: 
          </b>   <?php print $node['peer_rating']; ?>
        </td>
      </tr>
      <tr class="success">
        <td colspan="3">
          <b style="font-weight: 700">
            Self Comment:
          </b> <?php print $node['self_comment']; ?>
        </td>
      </tr>
      <tr class="warning">
        <td colspan="3">
          <b style="font-weight: 700">
            Counselor Comment:
          </b> <?php print $node['counselor_comment']; ?>
        </td>
      </tr>
      <tr class="error">
        <td colspan="3">
          <b style="font-weight: 700">
            Peer Comment:
          </b> <?php print $node['peer_comment']; ?>
        </td>
      </tr>
    </tbody>
  </table>
</div>

