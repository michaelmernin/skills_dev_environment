'use strict';

angular.module('etmApp')
    .controller('FeedbackStatusController', function ($scope, FeedbackStatus) {
        $scope.feedbackStatuses = [];
        $scope.loadAll = function() {
            FeedbackStatus.query(function(result) {
               $scope.feedbackStatuses = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            FeedbackStatus.save($scope.feedbackStatus,
                function () {
                    $scope.loadAll();
                    $('#saveFeedbackStatusModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            FeedbackStatus.get({id: id}, function(result) {
                $scope.feedbackStatus = result;
                $('#saveFeedbackStatusModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            FeedbackStatus.get({id: id}, function(result) {
                $scope.feedbackStatus = result;
                $('#deleteFeedbackStatusConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            FeedbackStatus.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteFeedbackStatusConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.feedbackStatus = {name: null, description: null, id: null};
        };
    });
