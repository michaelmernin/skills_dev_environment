'use strict';

angular.module('etmApp')
    .controller('ReviewDetailController', function ($scope, $stateParams, Review, ReviewType, ReviewStatus, User) {
        $scope.review = {};
        $scope.load = function (id) {
            Review.get({id: id}, function(result) {
              $scope.review = result;
            });
        };
        $scope.load($stateParams.id);
    });
