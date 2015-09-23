'use strict';

angular.module('etmApp').factory('Engagement', function ($resource, DateUtils) {
  function convertFromServer(data) {
    data.startDate = DateUtils.convertLocaleDateFromServer(data.startDate);
    data.endDate = DateUtils.convertLocaleDateFromServer(data.endDate);
  }

  function convertToServer(data) {
    data.startDate = DateUtils.convertLocaleDateToServer(data.startDate);
    data.endDate = DateUtils.convertLocaleDateToServer(data.endDate);
  }

  return $resource('api/engagements?annulaReviewId=:id', {}, {
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
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        convertFromServer(data);
        return data;
      }
    }
  });
});