'use strict';

angular.module('etmApp')
    .controller('FeedbackStatusDetailController', function ($scope, $stateParams, FeedbackStatus) {
        $scope.feedbackStatus = {};
        $scope.load = function (id) {
            FeedbackStatus.get({id: id}, function(result) {
              $scope.feedbackStatus = result;
            });
        };
        $scope.load($stateParams.id);
    });
