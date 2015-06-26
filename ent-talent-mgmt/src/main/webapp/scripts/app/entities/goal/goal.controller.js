'use strict';

angular.module('etmApp')
    .controller('GoalController', function ($scope, Goal, Review, User) {
        $scope.goals = [];
        $scope.reviews = Review.query();
        $scope.users = User.query();
        $scope.loadAll = function() {
            Goal.query(function(result) {
               $scope.goals = result;
            });
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Goal.get({id: id}, function(result) {
                $scope.goal = result;
                $('#saveGoalModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.goal.id != null) {
                Goal.update($scope.goal,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Goal.save($scope.goal,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Goal.get({id: id}, function(result) {
                $scope.goal = result;
                $('#deleteGoalConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Goal.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteGoalConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveGoalModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.goal = {name: null, note: null, targetDate: null, completionDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
