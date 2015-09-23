'use strict';

angular.module('etmApp').factory('Review', function ($resource, DateUtils) {
  function convertFromServer(data) {
    data.startDate = DateUtils.convertLocaleDateFromServer(data.startDate);
    data.endDate = DateUtils.convertLocaleDateFromServer(data.endDate);
  }

  function convertToServer(data) {
    data.startDate = DateUtils.convertLocaleDateToServer(data.startDate);
    data.endDate = DateUtils.convertLocaleDateToServer(data.endDate);
  }

  return $resource('api/reviews/:id', {}, {
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
    'save': {
      method: 'POST',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        convertFromServer(data);
        return data;
      }
    },
    'engagements': {
      url: 'api/reviews/:id/engagements',
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data.forEach(convertFromServer);
        return data;
      }
    }
  });
});
