'use strict';

angular.module('etmApp')
    .factory('ReviewType', function ($resource) {
        return $resource('api/reviewTypes/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'getAllExceptAR': {
                url: 'api/reviewTypes',
                method: 'GET', 
                isArray: true
            }
        });
    });
