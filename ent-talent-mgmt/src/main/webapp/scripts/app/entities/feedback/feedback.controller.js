'use strict';

angular.module('etmApp')
    .controller('FeedbackController', function ($scope, Feedback, Review, User, FeedbackType, FeedbackStatus, Rating) {
        $scope.feedback = [];
        $scope.reviews = Review.query();
        $scope.users = User.query();
        $scope.feedbacktypes = FeedbackType.query();
        $scope.feedbackstatuss = FeedbackStatus.query();
        $scope.ratings = Rating.query();
        $scope.loadAll = function () {
            Feedback.query(function (result) {
               $scope.feedback = result;
            });
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Feedback.get({id: id}, function (result) {
                $scope.feedback = result;
                $('#saveFeedbackModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.feedback.id != null) {
                Feedback.update($scope.feedback,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Feedback.save($scope.feedback,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Feedback.get({id: id}, function (result) {
                $scope.feedback = result;
                $('#deleteFeedbackConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Feedback.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteFeedbackConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveFeedbackModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.feedback = {id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
