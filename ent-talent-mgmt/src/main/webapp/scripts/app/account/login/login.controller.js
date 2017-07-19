'use strict';

angular.module('etmApp').controller('LoginController', function ($rootScope, $scope, $state, $timeout, Auth, Principal) {
  $scope.user = {};
  $scope.errors = {};

  function focusUserName() {
    angular.element('[ng-model="username"]').focus();
  }

  $scope.rememberMe = true;
  $timeout(focusUserName);
  $scope.login = function () {
    Auth.login({
      username: $scope.username,
      password: $scope.password,
      rememberMe: $scope.rememberMe
    }).then(function () {
      $scope.authenticationError = false;
      if ($rootScope.previousStateName === 'register') {
        $state.go('home');
      } else {
        $rootScope.back();
      }
    }).catch(function () {
      focusUserName();
      $scope.authenticationError = true;
    });
  };

  if (Principal.isAuthenticated()) {
    $state.go('home');
  }
});
