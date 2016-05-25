'use strict';

angular.module('etmApp').controller('MessageDetailController', function ($scope, $mdDialog, message, $sce) {
  $scope.message = message;
  $scope.htmlSrc = '/api/mail/messageHtml/'+message.hashcode;

  $scope.cancel = function () {
    $mdDialog.cancel();
  };
}).filter('unsafe', function ($sce) {
  return function (val) {
    return $sce.trustAsHtml(val.slice(0,100));
  };
});