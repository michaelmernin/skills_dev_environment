'use strict';

angular.module('etmApp').factory('Peer', function ($resource) {

  return $resource('api/reviews/:reviewId/peers/:id', {}, {});
});
 