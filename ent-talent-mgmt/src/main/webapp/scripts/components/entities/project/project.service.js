'use strict';

angular.module('etmApp').factory('Project', function ($resource, DateUtils) {
  
  return $resource('api/projects/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
        method: 'GET',
        transformResponse: function (data) {
            if (data) {
                data = angular.fromJson(data);
            }
            return data;
        }
    },
    'update': { method:'PUT' }
  });
});
