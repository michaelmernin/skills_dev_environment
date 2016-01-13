'use strict';

angular.module('etmApp').controller('SettingsController', function ($scope, Principal, User) { 
  $scope.user = {};
  $scope.profile = {};  
  
  Principal.identity().then(function (account) {
    $scope.user = account;
  });
  
  $scope.loadProfile = function () {
    User.profile(function (result) {
      $scope.profile = result;
    });
  };
  $scope.loadProfile();
});
