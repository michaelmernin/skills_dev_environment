'use strict';

angular.module('etmApp').factory('Peer', function ($resource) {

  return $resource('api/reviews/:reviewId/peers/:id', {}, {
    'query': {
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        angular.forEach(data, function (feedback) {
          angular.extend(feedback, feedback.author);
          delete feedback.author;
        });
        return data;
      }
    }
  });
});
 