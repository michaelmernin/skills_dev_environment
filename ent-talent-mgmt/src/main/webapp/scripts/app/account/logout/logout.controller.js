'use strict';

angular.module('etmApp').controller('LogoutController', function ($state, Auth) {
  Auth.logout().then(function () {
    $state.go('home');
  });
});
