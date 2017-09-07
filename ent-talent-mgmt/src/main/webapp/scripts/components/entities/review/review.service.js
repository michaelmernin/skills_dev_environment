'use strict';

angular.module('etmApp').factory('Review', function ($resource, DateUtils) {

  function convertFromServer(data) {
    return DateUtils.covertDatePropertiesFromServer(data, ['startDate', 'endDate']);
  }
  
  function transformArray(data) {
    data = angular.fromJson(data);
    data = convertFromServer(data);
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
    'todo': {
      url: 'api/reviews/:id/todo',
      method: 'GET',
      transformResponse: function (data) {
        data = angular.fromJson(data || "{}");
        if (data.dueDate) {
          data = DateUtils.covertDatePropertiesFromServer(data, ['dueDate']);
        }
        return data;
      }
    },
    'getReviewsByTypeAndReviewee': {
      url: 'api/reviews/get/reviews',
      method: 'GET',
      isArray: true,
      transformResponse: transformSingle
    }
  });
});
