'use strict';

angular.module('etmApp').factory('FeedbackType', function ($resource) {
  return {
    SELF: {id: 1, name: 'Self'},
    REVIEWER: {id: 2, name: 'Reviewer'},
    PEER: {id: 3, name: 'Peer'}
  };
});
