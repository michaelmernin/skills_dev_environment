'use strict';

angular.module('etmApp').controller('OverallController', function ($scope, Principal, Feedback, FeedbackType, Evaluation, Notification) {
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
      angular.forEach(parentFeedback, function (feedbackItem) {
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

  $scope.updateFeedback = function (feedback) {
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
        return "N/A";
      }
    }
  };
  
  $scope.isAnnual = function () {
    if ($scope.review.reviewType !== undefined && $scope.review.reviewType.processName === "annualReview") {
      return true;
    }
  return false;
}
});
