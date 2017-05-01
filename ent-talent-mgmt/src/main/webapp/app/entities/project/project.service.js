'use strict';

angular.module('etmApp').factory('Project', function ($resource, DateUtils) {
  function convertFromServer(data) {
    data.startDate = DateUtils.convertLocaleDateFromServer(data.startDate);
  }
  
  return $resource('api/projects/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
        method: 'GET',
        transformResponse: function (data) {
            if (data) {
                data = angular.fromJson(data);
            }
            return data;
        }
    },
    'update': { method:'PUT' },
    'queryProjects': {
      url: 'api/projects/getAllByUserId',
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