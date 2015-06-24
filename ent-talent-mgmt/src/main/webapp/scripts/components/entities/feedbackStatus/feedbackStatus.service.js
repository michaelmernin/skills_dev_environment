'use strict';

angular.module('etmApp')
    .factory('FeedbackStatus', function ($resource) {
        return $resource('api/feedbackStatuses/:id', {}, {
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
