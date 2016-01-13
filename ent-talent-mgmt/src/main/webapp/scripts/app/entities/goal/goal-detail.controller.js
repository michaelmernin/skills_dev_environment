'use strict';

angular.module('etmApp')
    .controller('GoalDetailController', function ($scope, $stateParams, Goal, Review, User) {
        $scope.goal = {};
        $scope.load = function (id) {
            Goal.get({id: id}, function (result) {
              $scope.goal = result;
            });
        };
        $scope.load($stateParams.id);
    });
