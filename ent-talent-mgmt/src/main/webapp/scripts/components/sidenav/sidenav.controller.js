'use strict';

angular.module('etmApp').controller('SidenavController', function ($scope, $state, $mdUtil, $mdSidenav, $mdMedia, Auth, Principal) {
  $scope.isAuthenticated = Principal.isAuthenticated;
  $scope.isInRole = Principal.isInRole;
  $scope.$state = $state;
  $scope.$mdMedia = $mdMedia;

  $scope.logout = function () {
    Auth.logout();
    $state.go('home');
  };
  
  $scope.toggleRightNav = $mdUtil.debounce(function () {
    $mdSidenav('right-nav').toggle();
  }, 300);
});