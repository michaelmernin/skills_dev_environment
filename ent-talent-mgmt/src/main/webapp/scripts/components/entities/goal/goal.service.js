'use strict';

angular.module('etmApp').factory('Goal', function ($resource, DateUtils) {
  return $resource('api/reviews/:reviewId/goals/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data = DateUtils.covertDatePropertiesFromServer(data, ['targetDate', 'completionDate']);
        return data;
      }
    },
    'update': {
      method: 'PUT',
      transformRequest: function (data) {
        return angular.toJson(data);
      }
    },
    'save': {
      method: 'POST',
      transformRequest: function (data) {
        data.targetDate = DateUtils.convertLocaleDateToServer(data.targetDate);
        data.completionDate = DateUtils.convertLocaleDateToServer(data.completionDate);
        return angular.toJson(data);
      }
    }
  });
});
