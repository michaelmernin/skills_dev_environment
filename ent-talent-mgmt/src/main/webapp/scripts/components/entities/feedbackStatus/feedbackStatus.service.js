'use strict';

angular.module('etmApp').factory('FeedbackStatus', function ($resource) {
  return angular.extend($resource('api/feedbackStatuses/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        return angular.fromJson(data);
      }
    }
  }), {
    INITIATED: {id: 1, name: 'Initiated'},
    COMPLETED: {id: 2, name: 'Completed'},
    NOT_SENT: {id: 3, name: 'Not Sent'},
    SENT: {id: 4, name: 'Sent'},
    RECEIVED: {id: 5, name: 'Received'}
  });
});
