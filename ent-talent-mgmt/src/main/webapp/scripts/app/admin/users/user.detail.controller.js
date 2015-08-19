'use strict';

angular.module('etmApp').controller('UserDetailController', function ($scope, $mdDialog, Authority, user) {
  $scope.roles = [];
  Authority.query(function (result) {
    $scope.roles = result;
  });

  $scope.user = angular.copy(user);

  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.save = function () {
    $mdDialog.hide($scope.user);
  };
});
