'use strict';

angular.module('etmApp').controller('TodoListController', function ($scope, $state, $stateParams, $mdDialog, Todo, Review, ReviewStatus, Principal) {
  $scope.todoList = [];
  var user;
  Principal.identity().then(function (account) {
    user = account;
  });
  Todo.queryTodoList(function(todos) {
    $scope.todoList = todos;
  });

  $scope.orderByItems = [
    {key: 'Created date', value: 'createDate'},
    {key: 'Review name',value: 'name'},
    {key: 'Review type', value: 'review.reviewType.name'}
  ];

  $scope.orderByExpression = "createDate";
  $scope.reverseOrder = false;

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
      if (this.todo.review.reviewStatus.id === 1) {
        // check if user is reviewee, reviewer, or peer - if yes, go to review edit. if no, go to review.detail
        if (user.id === this.todo.review.reviewee.id) {
          $state.go('review.edit', {id: reviewId});
        } else if (user.id === this.todo.review.reviewer.id) {
          $state.go('review.edit', {id: reviewId});
        } else {
          var matchedPeer = false;
          for (var i = 0; i < this.todo.review.peers.length; i++) {
            if (user.id === this.todo.review.peers[i].id) {
              matchedPeer = true;
              $state.go('review.edit', {id: reviewId});
            }
          }
          if (!matchedPeer) {
            $state.go('review.detail', {id: reviewId});
          }
        }
      } else {
        $state.go('review.detail', {id: reviewId});
      }
    } else {
      $state.go('login');
    }
  };

  $scope.reverseOrderFunction = function() {
    $scope.reverseOrder = !$scope.reverseOrder;
    $('.todoOrder').toggle();
  };

});
