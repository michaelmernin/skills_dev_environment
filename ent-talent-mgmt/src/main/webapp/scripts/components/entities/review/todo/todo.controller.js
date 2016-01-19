'use strict';

angular.module('etmApp').controller('TodoController', function ($scope, $stateParams, Todo, Review, ReviewStatus) {
  $scope.todo = {};
  var review = {};
  $scope.$parent.$watch('review', function (parentReview) {
    if (parentReview.id) {
      review = parentReview;
      Review.todo({id: review.id}, function (result) {
        $scope.todo = result;
        $scope.actions = Todo.getActions($scope.todo, review);
      });
    }
  });

  $scope.confirm = function (action) {
    console.log('Confirm Action?');
    console.dir(action);
  };
});
