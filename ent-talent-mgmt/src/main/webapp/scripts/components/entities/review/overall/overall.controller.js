'use strict';

angular.module('etmApp').controller('OverallController', function ($scope, $rootScope, Principal, Feedback, FeedbackType, Evaluation, Notification, FeedbackUtil, FeedbackStatus) {
  var review = {};
  var user = {};

  $scope.reviewerFeedback = {};
  $scope.revieweeFeedback = {};
  $scope.categories = {};
  $scope.questions = [];
  $scope.getRatings = Evaluation.getRatings;
  $scope.getAvgScore = Evaluation.avgScore;
  $scope.overallErrors = false;
  $scope.reviewerOverallDisabled = false;

  Principal.identity().then(function (account) {
    user = account;
    $scope.$parent.$watch('review', function (parentReview) {
      if (parentReview.id) {
        review = parentReview;
        $scope.categories = Evaluation.getCategories(review, user);
        $scope.questions = Evaluation.getQuestions(review);
      }
    });
  });

  $scope.showRevieweeRating = function () {
    return Evaluation.showRevieweeRating(review, user);
  };

  $scope.showReviewerRating = function () {
    return Evaluation.showReviewerRating(review, user);
  };

  $scope.showPeerRatings = function () {
    return Evaluation.showPeerRatings(review, user);
  };
  
  $scope.showAllRating = function () {
    return Evaluation.showRevieweeRating(review, user)
      && Evaluation.showReviewerRating(review, user)
      && Evaluation.showPeerRatings(review, user);
  };

  $scope.$parent.$watch('review.feedback', function (parentFeedback) {
    if (parentFeedback && parentFeedback.length) {

      $scope.revieweeFeedback = FeedbackUtil.getRevieweeFeedback(parentFeedback);
      if($scope.revieweeFeedback) {
        $scope.revieweeFeedback.editable = (user.login)? user.login == $scope.revieweeFeedback.author.login : false;
      }

      $scope.reviewerFeedback = FeedbackUtil.getReviewerFeedback(parentFeedback);
      if($scope.reviewerFeedback) {
        $scope.reviewerFeedback.editable = (user.login)? user.login == $scope.reviewerFeedback.author.login : false;
        $scope.reviewerOverallDisabled = $scope.reviewerFeedback.feedbackStatus.id > FeedbackStatus.OPEN.id;
      }
    }
  });

  $scope.updateFeedback = function (feedback) {
    validateOverall();
    var overalCommentError = $scope.reviewerOverallForm.comment.$error;
    if(overalCommentError.required || overalCommentError['md-maxlength']){
      console.log(overalCommentError);
      var errorMsg = 'Cannot save overall: ';
      errorMsg = overalCommentError.required? errorMsg+'comment required' : errorMsg;
      errorMsg = overalCommentError['md-maxlength']? errorMsg+'comment max length is 3000' : errorMsg;
      Notification.notify(errorMsg);
      return;
    }
    var feedbackCopy = {};
    angular.copy(feedback, feedbackCopy);
    delete feedbackCopy.ratings;
    Feedback.update({
      reviewId: review.id,
      feedbackId: feedback.id
    }, feedbackCopy)
    .$promise
    .then(function(res){
      Notification.notify('Overall feedback saved!');
    })
    .catch(function(err){
      Notification.notify('An error occured while saving Overall feedback!');
      console.log(err);
    });
  };
  
  $scope.getAllAvgScore = function(categories, userType) {
    if (categories !== null && categories !== undefined) {
      if (Object.keys(categories).length !== 0) {
        var questions = [];
        angular.forEach(categories, function(questionList){
          angular.forEach(questionList, function(question) {
            questions.push(question);
          });
        });
        return $scope.getAvgScore($scope.getRatings(questions, userType));
      } else {
        return 'N/A';
      }
    }
  };
  
  $scope.isAnnual = function () {
    if ($scope.review.reviewType !== undefined && $scope.review.reviewType.processName === 'annualReview') {
      return true;
    }
    return false;
  };

  $rootScope.$on('overall-has-errors', function(){
    $scope.overallErrors = true;
    validateOverall();
  });

  function validateOverall(){
    var isValid =  $scope.reviewerFeedback.overallScore && $scope.reviewerFeedback.overallComment;
    if(isValid) $rootScope.$emit('overall-valid');
    else $rootScope.$emit('overall-invalid');
  }

});
