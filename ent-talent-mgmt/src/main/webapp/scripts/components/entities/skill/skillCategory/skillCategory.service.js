'use strict';

angular.module('etmApp')
.factory('SkillCategory', function ($resource) {
  return $resource('api/skillCategories/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'save': {
      method: 'POST'
    },
    'update': {
      method: 'PUT',
      params: {id: '@id'}
    },
    'findAll': {
      url:'api/skillCategories/all',
      method: 'GET',
      isArray: true},
    'skillsReview': {
        url:'api/skillCategories/skillsReview',
        method: 'GET',
        isArray: true},
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        return data;
      }
    }
  });
});
