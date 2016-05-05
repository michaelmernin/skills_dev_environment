'use strict';

angular.module('etmApp').controller('TodoController', function ($scope, $stateParams, $mdDialog, Principal, Todo, Review, ReviewStatus, FeedbackType, Feedback) {
  $scope.todo = Todo.query();
  var reviewerFeedback = {};
  var review = {};
  var user = {};
  
  Principal.identity().then(function (account) {
    user = account;
    $scope.$parent.$watch('review', function (parentReview) {
      if (parentReview.id) {
        review = parentReview;
        loadTodo();
      }
    });

    $scope.$parent.$watch('review.feedback', function (parentFeedback) {
      if (parentFeedback && parentFeedback.length) {
        parentFeedback.forEach(function (feedbackItem) {
          if (feedbackItem.feedbackType.id === FeedbackType.REVIEWER.id) {
            reviewerFeedback = feedbackItem;
          }
        });
      }
    });
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
    Feedback.query({reviewId: review.id}, function (feedback) {
      review.feedback = feedback;

      Review.todo({id: review.id}, function (result) {
        $scope.todo = result;
        $scope.actions = Todo.getActions($scope.todo, review, reviewerFeedback);
      });
    });
  }
});
