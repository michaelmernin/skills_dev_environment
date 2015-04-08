'use strict';

angular.module('etmApp')
    .controller('ReviewTypeDetailController', function ($scope, $stateParams, ReviewType) {
        $scope.reviewType = {};
        $scope.load = function (id) {
            ReviewType.get({id: id}, function(result) {
              $scope.reviewType = result;
            });
        };
        $scope.load($stateParams.id);
    });
