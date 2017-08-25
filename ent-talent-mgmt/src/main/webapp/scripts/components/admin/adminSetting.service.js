'use strict';

angular.module('etmApp').factory('AdminSettingService', function ($resource) {
  return $resource('api/adminsetting', {}, {
    'get': {
      method: 'GET',
      isArray:true
    },
    'update': {
      method: 'PUT'
    },
    'create': {
      method: 'POST'
    },
    'delete': {
      url:'api/adminsetting/:key',
      method: 'DELETE'
    }
  });
});