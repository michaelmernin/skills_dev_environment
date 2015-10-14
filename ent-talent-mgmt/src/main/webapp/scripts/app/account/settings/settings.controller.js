'use strict';

angular.module('etmApp').controller('SettingsController', function ($scope, Principal) { 
  $scope.user = {};
  
  Principal.identity().then(function (account) {
    $scope.user = account;
  });
});
