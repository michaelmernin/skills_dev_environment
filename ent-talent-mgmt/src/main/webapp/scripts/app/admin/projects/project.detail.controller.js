'use strict';

angular.module('etmApp').controller('ProjectDetailController', function ($scope, $mdDialog, Authority, project, dialogType, User) {
  $scope.roles = [];
  Authority.query(function (result) {
    $scope.roles = result;
  });

  $scope.project = angular.copy(project);
  $scope.dialogType = dialogType;

  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.save = function () {
    $mdDialog.hide($scope.project);
  };

  $scope.selectedManager = $scope.project.manager;
  $scope.managerSearchText = "";
  $scope.managerSelected = function(m){
    $scope.project.manager = m;
  }
  $scope.getMatches = function (query) {
     return User.autocomplete({query: query, reviewId:""}).$promise;
  };
});
