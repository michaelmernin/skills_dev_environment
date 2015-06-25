'use strict';

angular.module('etmApp')
    .controller('QuestionDetailController', function ($scope, $stateParams, Question, ReviewType, Category) {
        $scope.question = {};
        $scope.load = function (id) {
            Question.get({id: id}, function(result) {
              $scope.question = result;
            });
        };
        $scope.load($stateParams.id);
    });
