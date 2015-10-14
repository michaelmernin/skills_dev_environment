'use strict';

angular.module('etmApp').factory('Account', function Account($resource, DateUtils) {
  function convertFromServer(data) {
    data.startDate = DateUtils.convertLocaleDateFromServer(data.startDate);
  }
  
  return $resource('api/account', {}, {
    'get': {
      method: 'GET',
      params: {},
      isArray: false,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        convertFromServer(data);
        return data;
      }
    }
  });
});
