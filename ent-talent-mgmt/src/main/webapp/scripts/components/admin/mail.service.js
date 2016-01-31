'use strict';

angular.module('etmApp').factory('MailService', function ($resource) {
  return $resource('api/mail', {}, {
    'findAll': { url:'api/mail/messages',method: 'GET', isArray: true},
    'testMessage': {url:'api/mail/test', method: 'POST'}
  });
});
