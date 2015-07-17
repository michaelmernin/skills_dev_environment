'use strict';

angular.module('etmApp').controller('ToolbarController', function ($scope, $location, $state, Auth, Principal) {
  $scope.isAuthenticated = Principal.isAuthenticated;
  $scope.isInRole = Principal.isInRole;
  $scope.$state = $state;

  $scope.logout = function () {
    Auth.logout();
    $state.go('home');
  };
});
