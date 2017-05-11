'use strict';

angular.module('etmApp').controller('ProjectDetailController', function ($scope, $mdDialog, Authority, project) {
  $scope.roles = [];
  Authority.query(function (result) {
    $scope.roles = result;
  });

  $scope.project = angular.copy(project);

  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.save = function () {
    $mdDialog.hide($scope.project);
  };
});
