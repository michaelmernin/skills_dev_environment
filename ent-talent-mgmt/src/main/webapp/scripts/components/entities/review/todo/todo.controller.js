'use strict';

angular.module('etmApp').controller('TodoController', function ($scope, $stateParams, $mdDialog, Principal, Todo, Review, ReviewStatus, FeedbackType, Feedback, Evaluation, EvaluationUtil, $rootScope, FeedbackUtil) {
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
    
    var categories = Evaluation.getCategories(review, user);
    //var hasEmptyQuestion = false;
    var errMessagses = [];
    // commented this as it does not matter who is submitting the feedback, no empty evaluations regadless
    //var isDirector = EvaluationUtil.isDirector(review, user);
    //var isGM = EvaluationUtil.isGM(review, user);
    //if(!isDirector && !isGM){
    var hasEmptyQuestion = EvaluationUtil.hasEmptyQuestions(categories);
    var hasReviewerOverall = _hasReviewerOverall();
   // }

    // error scinarios
    if(hasEmptyQuestion || !hasReviewerOverall){
      if(hasEmptyQuestion){
        $rootScope.$emit('evaluation-has-errors');
        errMessagses.push('One or more feedback questions are missing a rating.');
      }
      if(!hasReviewerOverall){
        $rootScope.$emit('overall-has-errors');
        errMessagses.push('Overall comment/score is missing/invalid.');
      }
      _showValidationDialog(errMessagses, ev);
    }
    // no errors
    else {
      $mdDialog.show(confirmAction).then(function () {
        Todo.update({id: action.todoId}, action, loadTodo);
      });
    }
  };

  function _hasReviewerOverall(){
    var reviewerFeedback = FeedbackUtil.getReviewerFeedback(review.feedback);
    // check both overall comment and score exist
    return reviewerFeedback.overallComment && 
           reviewerFeedback.overallScore && 
           // TODO:  move validation to a shared service.
           reviewerFeedback.overallComment.length <= 3000;
  }

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

  function _showValidationDialog(messagesArr, ev){
    var errorStr = '';
    angular.forEach(messagesArr, function(msg){
      errorStr += '<div>'+msg+'</div>';
    });
    var validationDialog =  $mdDialog.alert()
    .title('Looks like you missed something')
    .ariaLabel('Validation Dialog')
    .htmlContent(errorStr)
    .ok('Okay')
    .targetEvent(ev);

    $mdDialog.show(validationDialog);
    
  }
});
