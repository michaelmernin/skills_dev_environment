'use strict';

angular.module('etmApp')
    .controller('FeedbackTypeController', function ($scope, FeedbackType) {
        $scope.feedbackTypes = [];
        $scope.loadAll = function() {
            FeedbackType.query(function(result) {
               $scope.feedbackTypes = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            FeedbackType.save($scope.feedbackType,
                function () {
                    $scope.loadAll();
                    $('#saveFeedbackTypeModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            FeedbackType.get({id: id}, function(result) {
                $scope.feedbackType = result;
                $('#saveFeedbackTypeModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            FeedbackType.get({id: id}, function(result) {
                $scope.feedbackType = result;
                $('#deleteFeedbackTypeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            FeedbackType.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteFeedbackTypeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.feedbackType = {name: null, id: null};
        };
    });
