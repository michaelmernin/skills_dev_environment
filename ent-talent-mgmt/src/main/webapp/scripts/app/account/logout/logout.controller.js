'use strict';

angular.module('etmApp').controller('LogoutController', function (Auth) {
  Auth.logout();
});
