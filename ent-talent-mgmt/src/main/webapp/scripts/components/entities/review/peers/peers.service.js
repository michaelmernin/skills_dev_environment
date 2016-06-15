'use strict';

angular.module('etmApp').factory('Peer', function ($resource) {

  return $resource('api/reviews/:reviewId/peers/:id', {}, {
    'query': {
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        angular.forEach(data, function (feedback) {
          feedback.feedbackId = feedback.id;
          angular.extend(feedback, feedback.author);
          delete feedback.author;
        });
        return data;
      }
    },
    'addPeer': {
      url: 'api/reviews/:reviewId/peers/addPeer/:id',
      method: 'POST',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        angular.forEach(data, function (feedback) {
          feedback.feedbackId = feedback.id;
          angular.extend(feedback, feedback.author);
          delete feedback.author;
        });
        return data;
      }
    },
    'remindPeer':{
      url: 'api/reviews/:reviewId/peers/remindPeer/:id',
      method: 'POST',
      isArray: false
    }
  });
});
 