<div class="well sidebar-nav">
  <table >
    <caption>
      <h3></h3>
    </caption>

    <tbody>
      <tr>
        <td colspan="3">
          <?php print $node['description']; ?>
        </td>
      </tr>
      <tr>
        <td>
          Self Rating: <?php print $node['self_rating']; ?>      
        </td>
        <td>
          Counselor Rating: <?php print $node['counselor_rating']; ?>
        </td>
        <td>
          Peer Rating:    <?php print $node['peer_rating']; ?>
        </td>
      </tr>
      <tr>
        <td colspan="3">
          Self Comment: <?php print $node['self_comment']; ?>
        </td>
      </tr>
      <tr>
        <td colspan="3">
          Counselor Comment: <?php print $node['counselor_comment']; ?>
        </td>
      </tr>
      <tr>
        <td colspan="3">
          Peer Comment: <?php print $node['peer_comment']; ?>
        </td>
      </tr>
    </tbody>
  </table>
</div>

