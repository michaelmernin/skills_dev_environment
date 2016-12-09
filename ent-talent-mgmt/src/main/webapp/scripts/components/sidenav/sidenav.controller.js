'use strict';

angular.module('etmApp').controller('SidenavController', function ($scope, $state, $mdMedia, Sidenav, Auth, Principal, Env) {
  $scope.isAuthenticated = Principal.isAuthenticated;
  $scope.isInRole = Principal.isInRole;
  $scope.$state = $state;
  $scope.$mdMedia = $mdMedia;

  $scope.logout = function () {
    Auth.logout();
    $state.go('home');
  };

  $scope.toggleSideNav = Sidenav.toggle;
  
  $scope.isDev = false;
  $scope.isTest = false;
  
  Env.getEnvs().then(function(data){
    if(data && data.length){
      $scope.isDev =  data.indexOf('dev') !== -1;
      $scope.isTest =  data.indexOf('test') !== -1;
    }
  });
});
