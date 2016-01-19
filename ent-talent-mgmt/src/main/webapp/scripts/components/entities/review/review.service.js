'use strict';

angular.module('etmApp').factory('Review', function ($resource, DateUtils) {
  function convertFromServer(data) {
    data.startDate = DateUtils.convertLocaleDateFromServer(data.startDate);
    data.endDate = DateUtils.convertLocaleDateFromServer(data.endDate);
    return data;
  }

  function convertToServer(data) {
    data.startDate = DateUtils.convertLocaleDateToServer(data.startDate);
    data.endDate = DateUtils.convertLocaleDateToServer(data.endDate);
    return data;
  }
  
  function transformArray(data) {
    data = angular.fromJson(data);
    data.forEach(convertFromServer);
    return data;
  }
  
  function transformSingle(data) {
    return convertFromServer(angular.fromJson(data));
  }

  return $resource('api/reviews/:id', {}, {
    'query': {
      method: 'GET',
      isArray: true,
      transformResponse: transformArray
    },
    'get': {
      method: 'GET',
      transformResponse: transformSingle
    },
    'save': {
      method: 'POST',
      transformResponse: transformSingle
    },
    'update': {
      method:'PUT',
      transformRequest: function (data) {
        var newData = {};
        newData.add("id", data.id);
        newData.add("peers", data.peers);
        return angular.toJson(newData);
      }
    },
    'engagements': {
      url: 'api/reviews/:id/engagements',
      method: 'GET',
      isArray: true,
      transformResponse: transformArray
    },
    'peers': {
      url: 'api/reviews/:id/peers',
      method: 'GET',
      isArray: true
    },
    'addPeer': {
      url: 'api/reviews/:id/peers',
      method: 'POST',
      isArray: true
    },
    'todo': {
      url: 'api/reviews/:id/todo',
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data.dueDate = DateUtils.convertLocaleDateFromServer(data.dueDate);
        return data;
      }
    }
  });
});
