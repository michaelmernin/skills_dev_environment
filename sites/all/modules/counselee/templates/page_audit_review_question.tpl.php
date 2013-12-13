<div class="webform-submission-info clearfix">
  <!--Self comment-->
  <div class="webform-submission-info clearfix">
    <div id="header-0">
      <?php print $node['description']; ?> 
    </div>
    <br>
    <div class="view-self-comment">
      <div class="view-self-comment-title">
        <div style="font-weight: 600;float: left">·Self Comment | </div>
        <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">·Self Rating:</div>
        <div class="color-rating-box" id="<?php print 'src_self_' . $node['key']; ?>"> 
          <?php print display_rating($node['self_rating']); ?></div>
      </div>
      
      <div class="view-self-comment-bubble">
        <div class="additionalbubble">
          <div style="padding: 0 5px 5px;margin-bottom: 5px;">
            <div style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;">
              <?php print display_comment($node['self_comment']); ?>
            </div>
          </div>
        </div>
      </div>
    </div>

    <br>
    <br>
    <br>
    
    <!--Display the counselor comment message-->
    <div class="webform-submission-info clearfix">
      <div class="webform-submission-info-text">
        <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px;">
          ·Counselor Comment | ·Counselor Rating:</div>
        <div class="color-rating-box" id="<?php print 'src_counselor_' . $node['key']; ?>">

          <?php print display_rating($node['counselor_rating']); ?>
          
        </div>
        <div class="view-self-comment-bubble">
          <div class="additionalbubble">
            <div style="padding: 0 5px 5px;margin-bottom: 5px;">
              <div style="margin: 5px; height: 140px; width: 98%;overflow-y:auto;">
                <?php print display_comment($node['counselor_comment']); ?>
              </div>
            </div>
          </div>
        </div>      
      </div>
    </div>
    <br>
  </div>







  <?php if ($node['exist_peer']) : ?>

    <div class="webform-submission-info clearfix">

      <div class="webform-submission-info-text">
        <div style="font-weight: 600;float: left;padding-right: 5px;padding-left: 5px; margin-bottom: 10px">·Peer Rating: </div>
        <div class="color-rating-box" >
         <?php print $node['peer_rating'];?>
        </div>
      </div>
    </div>

    <div>
    <div class="jQgrid-center">
      <table id="peer-table-<?php print $node['key'] ?>"></table>
      <div id="peer-div-<?php print $node['key'] ?>"></div>
      <script>
        var lastSel;
        var peer_data_<?php print $node['key'] ?> =<?php print (trim($node['peer_comment']) == '') ? '[]' : $node['peer_comment']; ?>;
        jQuery("#peer-table-<?php print $node['key'] ?>").jqGrid({
          datatype: "local",
          height: 250,
  //          width: 800,
          autowidth: true,
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
                var i = 300;
                if (comment_text.length > i) {
                  comment_text = comment_text.substr(0, i) + '...';
                }
                return 'title="' + comment_text + '\nDouble click for more information."';
              }},
            {name: 'nid', index: 'nid', hidden: true},
            {name: 'cid', index: 'cid', hidden: true}],
          multiselect: false,
          rownumbers: true,
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


      </script>
    </div>
  <?php endif; ?>
</div>
<hr>



