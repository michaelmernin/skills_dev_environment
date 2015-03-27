'use strict';

angular.module('etmApp').factory('Password', function ($resource) {
  return $resource('api/account/change_password', {}, {
  });
});
