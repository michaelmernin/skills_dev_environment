'use strict';

angular.module('etmApp')
    .controller('QuestionController', function ($scope, Question, ReviewType, Category) {
        $scope.questions = [];
        $scope.reviewtypes = ReviewType.query();
        $scope.categorys = Category.query();
        $scope.loadAll = function () {
            Question.query(function (result) {
               $scope.questions = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Question.save($scope.question,
                function () {
                    $scope.loadAll();
                    $('#saveQuestionModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Question.get({id: id}, function (result) {
                $scope.question = result;
                $('#saveQuestionModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Question.get({id: id}, function (result) {
                $scope.question = result;
                $('#deleteQuestionConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Question.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteQuestionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.question = {text: null, position: null, id: null};
        };
    });
