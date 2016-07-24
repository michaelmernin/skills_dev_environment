'use strict';

angular.module('etmApp').factory('Feedback', function ($resource) {
  return $resource('api/reviews/:reviewId/feedback/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        return data;
      }
    },
    'update': { method:'PUT' },
    'open': {
      url: 'api/reviews/:reviewId/feedback/:id/open',
      method: 'PUT'
    }
  });
});
