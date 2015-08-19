'use strict';

angular.module('etmApp').factory('User', function ($resource, DateUtils) {
  function convertFromServer(data) {
    data.startDate = DateUtils.convertLocaleDateFromServer(data.startDate);
  }

  return $resource('api/users/:id', {}, {
    'query': {
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data.forEach(convertFromServer);
        return data;
      }
    },
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        convertFromServer(data);
        return data;
      }
    },
    'update': {
      method:'PUT',
      transformRequest: function (data) {
        data.startDate = DateUtils.convertLocaleDateToServer(data.startDate);
        return angular.toJson(data);
      }
    }
  });
});
