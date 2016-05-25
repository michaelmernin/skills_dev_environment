'use strict';

angular.module('etmApp').controller('MailDetailController', function ($scope, $mdDialog, mail) {
  $scope.mail = mail;
  
  $scope.cancel = function () {
    $mdDialog.cancel();
  };
  
  $scope.hide = function () {
    if ($scope.mailForm.$valid) {
      $mdDialog.hide($scope.mail);
    }
  };
});
