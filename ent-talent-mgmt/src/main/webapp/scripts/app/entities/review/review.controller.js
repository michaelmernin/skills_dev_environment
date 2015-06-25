'use strict';

angular.module('etmApp')
    .controller('ReviewController', function ($scope, Review, ReviewType, ReviewStatus, User) {
        $scope.reviews = [];
        $scope.reviewtypes = ReviewType.query();
        $scope.reviewstatuss = ReviewStatus.query();
        $scope.users = User.query();
        $scope.loadAll = function() {
            Review.query(function(result) {
               $scope.reviews = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Review.save($scope.review,
                function () {
                    $scope.loadAll();
                    $('#saveReviewModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Review.get({id: id}, function(result) {
                $scope.review = result;
                $('#saveReviewModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Review.get({id: id}, function(result) {
                $scope.review = result;
                $('#deleteReviewConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Review.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteReviewConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.review = {title: null, startDate: null, endDate: null, client: null, project: null, role: null, responsibilities: null, rating: null, id: null};
        };
    });
