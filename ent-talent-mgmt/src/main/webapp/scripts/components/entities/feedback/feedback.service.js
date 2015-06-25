'use strict';

angular.module('etmApp')
    .factory('Feedback', function ($resource, DateUtils) {
        return $resource('api/feedback/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
