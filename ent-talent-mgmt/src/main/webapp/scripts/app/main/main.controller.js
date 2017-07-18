'use strict';

angular.module('etmApp').controller('MainController', function ($scope, Principal, $state) {
  $scope.account = {};
  $scope.isAuthenticated = Principal.isAuthenticated;

  Principal.identity().then(function (account) {
    $scope.account = account || {};

    if (!$scope.isAuthenticated()) {
      $state.go('login');
    }
  });
});
