'use strict';

angular.module('etmApp').factory('Authority', function ($resource) {
  return $resource('api/authorities/:id', {}, {
    'query': { method: 'GET', isArray: true, cache: true }
  });
});
