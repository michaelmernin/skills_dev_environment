'use strict';

angular.module('etmApp').controller('UserDetailController', function ($scope, $mdDialog, Authority, user, counselors) {
  $scope.roles = [];
  Authority.query(function (result) {
    $scope.roles = result;
  });

  $scope.user = angular.copy(user);
  $scope.counselors = counselors;

  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.save = function () {
    $mdDialog.hide($scope.user);
  };
});
