'use strict';

angular.module('etmApp').controller('TodoController', function ($scope, $stateParams, $mdDialog, Principal, Todo, Review, ReviewStatus, FeedbackType, Feedback, Evaluation, EvaluationUtil, $rootScope) {
  $scope.todo = Todo.query();
  var reviewerFeedback = {};
  var review = {};
  var user = {};

  Principal.identity().then(function (account) {
    user = account;
    $scope.$parent.$watch('review', function (parentReview) {
      if (parentReview.id) {
        review = parentReview;
        $scope.categories = Evaluation.getCategories(review, user);
        loadTodo();
      }
    });

    $scope.$parent.$watch('review.feedback', function (parentFeedback) {
      if (parentFeedback && parentFeedback.length) {
        angular.forEach(parentFeedback, function (feedbackItem) {
          if (feedbackItem.feedbackType.id === FeedbackType.REVIEWER.id) {
            reviewerFeedback = feedbackItem;
          }
        });
      }
    });
  });

  $scope.confirm = function (action, categories, questions, ev) {
    var confirmAction = $mdDialog.confirm()
      .title(action.name)
      .ariaLabel('Confirm Action Dialog')
      .content(action.confirm)
      .ok(action.result)
      .cancel('Cancel')
      .targetEvent(ev);
    var missingQuestionDialog = $mdDialog.alert()
      .title('Feedback questions missing rating')
      .ariaLabel('Missing Feedbacks Dialog')
      .content('One or more feedback questions are missing a rating.')
      .ok('Okay')
      .targetEvent(ev);
    
    var categories = Evaluation.getCategories(review, user);
    var hasEmptyQuestion = false;
    // commented this as it does not matter who is submitting the feedback, no empty evaluations regadless
    //var isDirector = EvaluationUtil.isDirector(review, user);
    //var isGM = EvaluationUtil.isGM(review, user);
    //if(!isDirector && !isGM){
    hasEmptyQuestion = EvaluationUtil.hasEmptyQuestions(categories);
   // }
    if (!hasEmptyQuestion) {
      $mdDialog.show(confirmAction).then(function () {
          Todo.update({id: action.todoId}, action, loadTodo);
      });
    } else {
      $rootScope.$emit('evaluation-has-errors');
      $mdDialog.show(missingQuestionDialog);
    }
  };

  function loadTodo() {
    Feedback.query({reviewId: review.id}, function (feedback) {
      review.feedback = feedback;
      // update review status.
      if($scope.review){
        Review.get({id:$scope.review.id }, function (result) {
          $scope.review.reviewStatus = result.reviewStatus;
        });
      }
      Review.todo({id: review.id}, function (result) {
        $scope.todo = result;
        $scope.actions = Todo.getActions($scope.todo, review, reviewerFeedback);
      });
    });
  }
});
