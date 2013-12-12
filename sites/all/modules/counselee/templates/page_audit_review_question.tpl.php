<div class="webform-submission-info clearfix">

  <div>
    <?php print $node['description']; ?> 
  </div>

  <div class="wellwarp">
    <b style="font-weight: 700"> Self Rating:</b>
    <font color="red">
    <span id="<?php print 'src_self_' . $node['key']; ?>" >
      <?php print display_rating($node['self_rating']); ?>
    </span>
    </font>  

    <b style="font-weight: 700">
      | ·Counselor Rating: </b>
    <font color="red">
    <span id="<?php print 'src_counselor_' . $node['key']; ?>" >
      <?php print display_rating($node['counselor_rating']); ?>
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
    </b> <?php print display_comment($node['self_comment']); ?>
  </div>

  <div class="wellwarp">
    <b style="font-weight: 700">
      Counselor Comment:
    </b> <?php print display_comment($node['counselor_comment']); ?>
  </div>


  <?php if ($node['exist_peer']) : ?>
    <div>
      <table id="peer-table-<?php print $node['key'] ?>"></table>
      <div id="peer-div-<?php print $node['key'] ?>"></div>
      <script>
        var lastSel;
        var peer_data_<?php print $node['key'] ?> =<?php print (trim($node['peer_comment']) == '') ? '[]' : $node['peer_comment']; ?>;
        jQuery("#peer-table-<?php print $node['key'] ?>").jqGrid({
          datatype: "local",
          height: 250,
          width: 800,
          data: peer_data_<?php print $node['key'] ?>,
          colNames: ['Title', 'Rating', 'Comment', 'Nid', 'Cid'],
          colModel: [
            //           {name: 'peer_name', index: 'peer_name', width: 60, align: 'center', sorttype: "text", cellattr: function(rowId, val, rawObject) {
            //              return 'title="' + rawObject.peer_name + '\nDouble click for more information."';
            //            }},
            {name: 'title', index: 'title', width: 100, cellattr: function(rowId, val, rawObject) {
                return 'title="' + rawObject.title + '\nDouble click for more information."';
              }},
            {name: 'rating', index: 'rating', align: 'center', width: 50, cellattr: function(rowId, val, rawObject) {
                return 'title="' + rawObject.rating + '\nDouble click for more information."';
              }},
            {name: 'comment', index: 'comment', width: 300, cellattr: function(rowId, val, rawObject) {
                var comment_text = rawObject.comment;
                var i = 200;
                if (comment_text.length > i) {
                  comment_text = comment_text.substr(0, i) + '...';
                }
                return 'title="' + comment_text + '\nDouble click for more information."';
              }},
            {name: 'nid', index: 'nid', hidden: true},
            {name: 'cid', index: 'cid', hidden: true}],
          multiselect: false,
          rownumbers:true,
          caption: "·Peer Comment",
          pager: '#peer-div-<?php print $node['key'] ?>',
          rowNum: 10,
          rowList: [10, 20, 30],
          pginput: false,
          viewrecords: true,
          editurl: 'clientArray',
          ondblClickRow: function(id) {
            if (id && id !== lastSel) {
              jQuery('#peer-table-<?php print $node['key'] ?>').restoreRow(lastSel);
              lastSel = id;
            }
            jQuery('#peer-table-<?php print $node['key'] ?>').viewGridRow(id, {width: 700, height: 320, dataheight: 200});
            //jQuery('#peer-table-<?php print $node['key'] ?>').editGridRow(id, {width: 700, height: 320, dataheight: 200});
          }
        }).navGrid('#peer-div-<?php print $node['key'] ?>',
                {view: false, add: false, edit: false, del: false, search: false},
        {}, // use default settings for edit  

                {}, // use default settings for add  

                {}, // delete instead that del:false we need this  

                {} // enable the advanced searching  


        );

        //      for (var i = 0; i <= peer_data_<?php // print $node['key']      ?>.length; i++)
        //        jQuery("#peer-table-<?php // print $node['key']      ?>").jqGrid('addRowData', i + 1, peer_data_<?php // print $node['key']     ?>[i]);

      </script>
    </div>
  <?php endif; ?>
</div>
<hr>



