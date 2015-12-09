'use strict';

angular.module('etmApp').controller('ToolbarController', function ($scope, $state, $mdMedia, Sidenav, Auth, Principal) {
  $scope.account = {};
  $scope.isAuthenticated = Principal.isAuthenticated;
  $scope.isInRole = Principal.isInRole;
  $scope.$state = $state;
  $scope.$mdMedia = $mdMedia;
  $scope.toggleSideNav = Sidenav.toggle;

  $scope.$watch('isAuthenticated()', function (newValue) {
    Principal.identity().then(function (account) {
      $scope.account = account || {};
    });
  });
});
