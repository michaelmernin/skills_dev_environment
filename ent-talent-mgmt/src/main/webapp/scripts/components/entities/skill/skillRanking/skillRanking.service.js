'use strict';

angular.module('etmApp').factory('SkillRanking', function ($resource,DateUtils) {
  return $resource('api/skillRanking/:id', {}, {
    'save': {
      method: 'POST'
    },
    'update': {
      method: 'PUT',
      params: {id: '@id'}
    }
  });
});
