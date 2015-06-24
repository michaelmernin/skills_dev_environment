'use strict';

angular.module('etmApp')
    .controller('ReviewStatusDetailController', function ($scope, $stateParams, ReviewStatus) {
        $scope.reviewStatus = {};
        $scope.load = function (id) {
            ReviewStatus.get({id: id}, function(result) {
              $scope.reviewStatus = result;
            });
        };
        $scope.load($stateParams.id);
    });
