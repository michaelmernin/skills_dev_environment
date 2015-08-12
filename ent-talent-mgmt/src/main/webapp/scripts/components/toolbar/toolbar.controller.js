'use strict';

angular.module('etmApp').controller('ToolbarController', function ($scope, $state, $mdMedia, Sidenav, Auth, Principal) {
  Principal.identity().then(function(account) {
    $scope.account = account;
  });
  $scope.isAuthenticated = Principal.isAuthenticated;
  $scope.isInRole = Principal.isInRole;
  $scope.$state = $state;
  $scope.$mdMedia = $mdMedia;

  $scope.logout = function () {
    Auth.logout();
    $state.go('home');
  };
  
  $scope.toggleSideNav = Sidenav.toggle;
});
