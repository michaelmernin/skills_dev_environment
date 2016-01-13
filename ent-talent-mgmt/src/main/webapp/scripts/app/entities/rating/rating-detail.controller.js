'use strict';

angular.module('etmApp')
    .controller('RatingDetailController', function ($scope, $stateParams, Rating, Question, Feedback) {
        $scope.rating = {};
        $scope.load = function (id) {
            Rating.get({id: id}, function (result) {
              $scope.rating = result;
            });
        };
        $scope.load($stateParams.id);
    });
