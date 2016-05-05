'use strict';

angular.module('etmApp')
.factory('SkillCategory', function ($resource) {
  return $resource('api/skillCategories', {}, {
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
