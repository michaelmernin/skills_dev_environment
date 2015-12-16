'use strict';

angular.module('etmApp').factory('FeedbackStatus', function ($resource) {
  return {
    NOT_SENT: {id: 1, name: 'Not Sent'},
    OPEN: {id: 2, name: 'Open'},
    READY: {id: 3, name: 'Ready'},
    COMPLETE: {id: 4, name: 'Complete'},
    CLOSED: {id: 5, name: 'Closed'}
  };
});
