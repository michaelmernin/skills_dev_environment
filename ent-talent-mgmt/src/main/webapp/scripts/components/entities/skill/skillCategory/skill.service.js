'use strict';

angular.module('etmApp')
.factory('Skill', function ($resource) {
  return $resource('api/skills/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'save': {
      method: 'POST'
    },
    'delete': {
      method: 'DELETE',
      params: {id: '@id'}
    },
    'update': {
      method: 'PUT',
      params: {id: '@id'}
    },
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        return data;
      }
    }
  });
});
