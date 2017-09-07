'use strict';

angular.module('etmApp').factory('Account', function Account($resource, DateUtils) {
  function convertFromServer(data) {
    return DateUtils.covertDatePropertiesFromServer(data, ['startDate']);
  }
  
  return $resource('api/account', {}, {
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        convertFromServer(data);
        return data;
      }
    }
  });
});
