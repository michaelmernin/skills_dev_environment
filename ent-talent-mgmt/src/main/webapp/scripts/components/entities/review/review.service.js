'use strict';

angular.module('etmApp')
    .factory('Review', function ($resource) {
        return $resource('api/reviews/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    var startDateFrom = data.startDate.split("-");
                    data.startDate = new Date(new Date(startDateFrom[0], startDateFrom[1] - 1, startDateFrom[2]));
                    var endDateFrom = data.endDate.split("-");
                    data.endDate = new Date(new Date(endDateFrom[0], endDateFrom[1] - 1, endDateFrom[2]));
                    return data;
                }
            }
        });
    });
