'use strict';

angular.module('etmApp')
    .controller('CategoryController', function ($scope, Category) {
        $scope.categories = [];
        $scope.loadAll = function() {
            Category.query(function(result) {
               $scope.categories = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Category.save($scope.category,
                function () {
                    $scope.loadAll();
                    $('#saveCategoryModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Category.get({id: id}, function(result) {
                $scope.category = result;
                $('#saveCategoryModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Category.get({id: id}, function(result) {
                $scope.category = result;
                $('#deleteCategoryConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Category.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCategoryConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.category = {title: null, id: null};
        };
    });
