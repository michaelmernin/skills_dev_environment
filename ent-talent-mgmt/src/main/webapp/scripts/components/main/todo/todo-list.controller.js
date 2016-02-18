'use strict';

angular.module('etmApp').controller('TodoListController', function ($scope, $state, $stateParams, $mdDialog, Todo, Review, ReviewStatus) {
  $scope.todoList = Todo.queryTodoList();
  
  $scope.orderByItems = [
    {key: 'Start date', value: 'review.startDate'},
    {key: 'Review name',value: 'name'},
    {key: 'Review type', value: 'review.reviewType.name'}
  ];
  
  $scope.orderByExpression = "review.startDate";
  
  $scope.todoSearch = function (todo) {
    // if query is undefined or null, show all todos
    if (!$scope.query) {
      return true;
    }
    
    var query = $scope.query ? $scope.query.toLowerCase() : '';
    return isSubstring(todo.review.startDate, query)
      || isSubstring(todo.name, query)
      || isSubstring(todo.review.reviewType.name, query);
  };
  
  function isSubstring(property, query) {
    return (property && query) ? property.toLowerCase().indexOf(query) !== -1 : false
  }
  
  $scope.goToTodo = function (reviewId) {
    if (reviewId) {
      $state.go('review.edit', {id: reviewId});
    } else {
      $state.go('login');
    }
  };
});
