'use strict';

angular.module('etmApp').factory('FeedbackType', function ($resource) {
  return angular.extend($resource('api/feedbackTypes/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        return angular.fromJson(data);
      }
    }
  }), {
    SELF: {id: 1},
    REVIEWER: {id: 2},
    PEER: {id: 3}
  });
});
