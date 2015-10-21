'use strict';

angular.module('etmApp').controller('MainController', function ($scope, Principal) {
  $scope.account = {};
  $scope.isAuthenticated = Principal.isAuthenticated;

  Principal.identity().then(function (account) {
    $scope.account = account || {};
  });
});
