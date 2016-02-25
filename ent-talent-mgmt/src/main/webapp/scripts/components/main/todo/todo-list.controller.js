'use strict';

angular.module('etmApp').controller('TodoListController', function ($scope, $state, $stateParams, $mdDialog, Todo, Review, ReviewStatus, Principal) {
  $scope.todoList = Todo.queryTodoList();
  
  if (Principal.isAuthenticated()) {
    Todo.queryTodoList(function(todos) {
      $scope.todoList = todos;
    });
  } else {
    $scope.todoList = getDummyReviews();
  }
  
  $scope.orderByItems = [
    {key: 'Start date', value: 'createDate'},
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
      $state.go('review.edit', {id: reviewId});
    } else {
      $state.go('login');
    }
  };
  
  $scope.reverseOrderFunction = function() {
    $scope.reverseOrder = !$scope.reverseOrder;
    $('.todoOrder').toggle();
  };
  
  function getDummyReviews() {
    return [{
      name: 'create self review',
      createDate: '2014-12-05',
      review: {
        startDate: '2014-06-10',
        endDate: '2015-04-10',
        client: 'BestBuy',
        project: 'Open Box',
        reviewType: {name: 'Annual Review'},
        reviewStatus: ReviewStatus.OPEN,
        reviewee: {firstName: 'Jack', lastName: 'Smith'},
        reviewer: {firstName: 'David', lastName: 'Smith'}
      }
    }, {
      name: 'create annual review',
      createDate: '2015-11-04',
      review: {
        startDate: '2015-03-8',
        endDate: '2015-06-21',
        client: 'Target',
        project: 'AEM',
        reviewType: {name: '3 Month Review'},
        reviewStatus: ReviewStatus.JOINT_APPROVAL,
        reviewee: {firstName: 'John', lastName: 'Doe'},
        reviewer: {firstName: 'Sam', lastName: 'Jackson'}
      }
    }, {
      createDate: '2016-01-01',
      name: 'create director review',
      review: {
        startDate: '2015-03-4',
        endDate: '2015-08-7',
        client: 'Midtronic',
        project: 'iPhone App',
        reviewType: {name: 'Engagement'},
        reviewStatus: ReviewStatus.GM_APPROVAL,
        reviewee: {firstName: 'Jason', lastName: 'White'},
        reviewer: {firstName: 'Joe', lastName: 'Rose'}
      }
    }];
  }
});
