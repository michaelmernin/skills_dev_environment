'use strict';

angular.module('etmApp').factory('Mail', function ($resource) {
  return $resource('api/mail', {}, {
    'findAll': { url:'api/mail/messages',method: 'GET', isArray: true},
    'clear': {url:'api/mail/clear', method: 'DELETE'}
  });
});
