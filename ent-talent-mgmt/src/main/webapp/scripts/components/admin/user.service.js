'use strict';

angular.module('etmApp').factory('User', function ($resource) {
  return $resource('api/users/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
        method: 'GET',
        transformResponse: function (data) {
            data = angular.fromJson(data);
            return data;
        }
    }
  });
});
