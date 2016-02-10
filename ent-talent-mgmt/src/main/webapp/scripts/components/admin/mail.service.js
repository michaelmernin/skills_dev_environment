'use strict';

angular.module('etmApp').factory('Mail', function ($resource) {
  return $resource('api/mail', {}, {
    'findAll': { url:'api/mail/messages',method: 'GET', isArray: true},
    'testMessage': {url:'api/mail/test', method: 'POST'},
    'send': {url:'api/mail/send', method: 'POST'},
    'clear': {url:'api/mail/clear', method: 'DELETE'}
  });
});
