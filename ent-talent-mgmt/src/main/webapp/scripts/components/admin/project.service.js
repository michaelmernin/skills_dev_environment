'use strict';

angular.module('etmApp').factory('Project', function ($resource, DateUtils) {
  function convertFromServer(data) {
    return DateUtils.covertDatePropertiesFromServer(data, ['startDate']);
  }

  function responseTransformer(data){
    if(!data) return data;
    var jsondata = angular.fromJson(data);
    if(!jsondata.length) convertFromServer(jsondata);

    else angular.forEach(jsondata, convertFromServer);
    return jsondata;
  }

  return $resource('api/projects/:id', {}, {
    'getAll': {
      url:'api/projects',
      method: 'GET',
      isArray: true,
      transformResponse: responseTransformer
    },
    'get': {
      method: 'GET',
      transformResponse: responseTransformer
    },
    'update': {
      method:'PUT',
      transformResponse: responseTransformer
    },
    'delete':{
      method:'DELETE',
      transformResponse: responseTransformer
    },
    'getAllByUser': {
      url: 'api/projects/byUser/:id',
      method: 'GET',
      isArray: true,
      transformResponse: responseTransformer
    }
  });
});