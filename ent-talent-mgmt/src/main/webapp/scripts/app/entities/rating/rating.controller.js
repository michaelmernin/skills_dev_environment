'use strict';

angular.module('etmApp')
    .controller('RatingController', function ($scope, Rating, Question, Feedback) {
        $scope.ratings = [];
        $scope.questions = Question.query();
        $scope.feedbacks = Feedback.query();
        $scope.loadAll = function() {
            Rating.query(function(result) {
               $scope.ratings = result;
            });
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Rating.get({id: id}, function(result) {
                $scope.rating = result;
                $('#saveRatingModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.rating.id != null) {
                Rating.update($scope.rating,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Rating.save($scope.rating,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Rating.get({id: id}, function(result) {
                $scope.rating = result;
                $('#deleteRatingConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Rating.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteRatingConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveRatingModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.rating = {score: null, comment: null, visible: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
