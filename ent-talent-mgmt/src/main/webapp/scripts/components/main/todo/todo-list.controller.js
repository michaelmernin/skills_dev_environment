'use strict';

angular.module('etmApp').controller('TodoListController', function ($scope, $stateParams, $mdDialog, Todo, Review, ReviewStatus) {
  $scope.todoList = Todo.queryTodoList();
});
