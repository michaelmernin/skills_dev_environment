'use strict';

angular.module('etmApp')
    .factory('Question', function ($resource) {
        return $resource('api/questions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
              url: 'api/questions/:reviewTypeId/:feedbackTypeId',
              method: 'GET',
              isArray: true
            }
        });
    });
