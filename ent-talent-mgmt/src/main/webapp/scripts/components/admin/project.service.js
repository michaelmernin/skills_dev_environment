'use strict';

angular.module('etmApp').factory('Project', function ($resource, DateUtils) {
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
      params: {id: '@id'},
      transformRequest: function (data) {
        data.startDate = DateUtils.convertLocaleDateToServer(data.startDate);
        return angular.toJson(data);
      }
    },
    'queryCounselees': {
      url: 'api/counselees',
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data.forEach(convertFromServer);
        return data;
      }
    },
    'autocomplete': {
      url: 'api/users/autocomplete',
      method: 'GET',
      isArray: true
    },
    'profile': {
      url: 'api/profile',
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        convertFromServer(data);
        return data;
      }
    }
  });
});