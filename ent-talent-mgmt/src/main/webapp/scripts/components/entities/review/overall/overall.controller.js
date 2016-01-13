'use strict';

angular.module('etmApp').controller('OverallController', function ($scope, Principal, Feedback, FeedbackType, Evaluation) {
  var review = {};
  var user = {};

  $scope.reviewerFeedback = {};
  $scope.revieweeFeedback = {};
  $scope.categories = {};
  $scope.questions = [];
  $scope.getRatings = Evaluation.getRatings;
  $scope.getAvgScore = Evaluation.avgScore;

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
      parentFeedback.forEach(function (feedbackItem) {
        if (feedbackItem.feedbackType.id === FeedbackType.SELF.id) {
          $scope.revieweeFeedback = feedbackItem;
          $scope.revieweeFeedback.editable = (user.login)? user.login == feedbackItem.author.login : false;
        } else if (feedbackItem.feedbackType.id === FeedbackType.REVIEWER.id) {
          $scope.reviewerFeedback = feedbackItem;
          $scope.reviewerFeedback.editable = (user.login)? user.login == feedbackItem.author.login : false;

        }
      });
    }
  });
  
   $scope.change = function () {
        console.log("changed");
      };


  $scope.updateFeedback = function (feedback) {
    if(delete feedback.ratings){
      Feedback.update({
        reviewId: review.id,
        feedbackId: feedback.id
      }, feedback);
    }
  };
});
