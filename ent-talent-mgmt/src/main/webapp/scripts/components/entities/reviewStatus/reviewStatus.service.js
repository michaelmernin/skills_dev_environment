'use strict';

angular.module('etmApp')
    .factory('ReviewStatus', function ($resource) {
        return $resource('api/reviewStatuses/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
