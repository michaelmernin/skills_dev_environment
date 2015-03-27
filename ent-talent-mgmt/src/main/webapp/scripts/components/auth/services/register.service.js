'use strict';

angular.module('etmApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


