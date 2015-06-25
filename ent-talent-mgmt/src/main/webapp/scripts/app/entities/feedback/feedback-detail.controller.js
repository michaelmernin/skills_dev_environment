'use strict';

angular.module('etmApp')
    .controller('FeedbackDetailController', function ($scope, $stateParams, Feedback, Review, User, FeedbackType, FeedbackStatus, Rating) {
        $scope.feedback = {};
        $scope.load = function (id) {
            Feedback.get({id: id}, function(result) {
              $scope.feedback = result;
            });
        };
        $scope.load($stateParams.id);
    });
