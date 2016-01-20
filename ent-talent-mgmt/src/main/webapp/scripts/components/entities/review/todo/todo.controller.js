'use strict';

angular.module('etmApp').controller('TodoController', function ($scope, $stateParams, $mdDialog, Todo, Review, ReviewStatus) {
  $scope.todo = {};
  var review = {};
  $scope.$parent.$watch('review', function (parentReview) {
    if (parentReview.id) {
      review = parentReview;
      loadTodo();
    }
  });

  $scope.confirm = function (action, ev) {
    var confirmAction = $mdDialog.confirm()
      .title(action.name)
      .ariaLabel('Confirm Action Dialog')
      .content(action.confirm)
      .ok(action.result)
      .cancel('Cancel')
      .targetEvent(ev);
    $mdDialog.show(confirmAction).then(function () {
      Todo.update({id: action.todoId}, action, loadTodo);
    });
  };

  function loadTodo() {
    Review.todo({id: review.id}, function (result) {
      $scope.todo = result;
      $scope.actions = Todo.getActions($scope.todo, review);
    });
  }
});
