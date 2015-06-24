'use strict';

angular.module('etmApp')
    .controller('FeedbackTypeDetailController', function ($scope, $stateParams, FeedbackType) {
        $scope.feedbackType = {};
        $scope.load = function (id) {
            FeedbackType.get({id: id}, function(result) {
              $scope.feedbackType = result;
            });
        };
        $scope.load($stateParams.id);
    });
