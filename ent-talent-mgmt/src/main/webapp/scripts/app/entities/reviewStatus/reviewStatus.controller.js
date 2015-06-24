'use strict';

angular.module('etmApp')
    .controller('ReviewStatusController', function ($scope, ReviewStatus) {
        $scope.reviewStatuses = [];
        $scope.loadAll = function() {
            ReviewStatus.query(function(result) {
               $scope.reviewStatuses = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            ReviewStatus.save($scope.reviewStatus,
                function () {
                    $scope.loadAll();
                    $('#saveReviewStatusModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            ReviewStatus.get({id: id}, function(result) {
                $scope.reviewStatus = result;
                $('#saveReviewStatusModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            ReviewStatus.get({id: id}, function(result) {
                $scope.reviewStatus = result;
                $('#deleteReviewStatusConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ReviewStatus.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteReviewStatusConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.reviewStatus = {name: null, description: null, id: null};
        };
    });
