'use strict';

angular.module('etmApp')
    .controller('ReviewTypeController', function ($scope, ReviewType) {
        $scope.reviewTypes = [];
        $scope.loadAll = function () {
            ReviewType.query(function (result) {
               $scope.reviewTypes = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            ReviewType.save($scope.reviewType,
                function () {
                    $scope.loadAll();
                    $('#saveReviewTypeModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            ReviewType.get({id: id}, function (result) {
                $scope.reviewType = result;
                $('#saveReviewTypeModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            ReviewType.get({id: id}, function (result) {
                $scope.reviewType = result;
                $('#deleteReviewTypeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ReviewType.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteReviewTypeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.reviewType = {name: null, description: null, id: null};
        };
    });
