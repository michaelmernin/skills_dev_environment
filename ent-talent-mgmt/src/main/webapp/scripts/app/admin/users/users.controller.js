'use strict';

angular.module('etmApp').controller('UsersController', function ($scope, $mdDialog, User) {
  $scope.users = [];
  $scope.loadAll = function() {
    User.query(function(result) {
      $scope.users = result;
    });
  };
  $scope.loadAll();
});
