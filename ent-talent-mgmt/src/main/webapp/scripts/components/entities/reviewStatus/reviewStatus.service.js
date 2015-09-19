'use strict';

angular.module('etmApp').factory('ReviewStatus', function ($resource) {
  return angular.extend($resource('api/reviewStatuses/:id', {}, {
    'query': { method: 'GET', isArray: true},
    'get': {
      method: 'GET',
      transformResponse: function (data) {
        return angular.fromJson(data);
      }
    }
  }), {
    INITIATED: {id: 1, name: 'Initiated'},
    COUNSELOR: {id: 2, name: 'Counselor Review'},
    JOINT: {id: 3, name: 'Joint Review'},
    GM: {id :4, name: 'GM Review'},
    COMPLETED: {id: 5, name: 'Completed'},
    CLOSED: {id: 6, name: 'Closed'}
  });
});
