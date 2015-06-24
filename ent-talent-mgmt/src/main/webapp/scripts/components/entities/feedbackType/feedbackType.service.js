'use strict';

angular.module('etmApp')
    .factory('FeedbackType', function ($resource) {
        return $resource('api/feedbackTypes/:id', {}, {
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
