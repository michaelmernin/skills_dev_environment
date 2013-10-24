


<?php

$nid = db_insert('node'); // Table name no longer needs {}

$data = array(
  'title' => 'Example',
  'uid' => 1,
  'created' => REQUEST_TIME,
);
$nid->fields($data);
$nid->execute();
?>