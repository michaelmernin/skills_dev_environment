'use strict';

angular.module('etmApp').factory('Rating', function ($resource) {
  return $resource('api/reviews/:reviewId/feedback/:feedbackId/ratings/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        return data;
      }
    },
    'update': { method:'PUT' }
  });
});
