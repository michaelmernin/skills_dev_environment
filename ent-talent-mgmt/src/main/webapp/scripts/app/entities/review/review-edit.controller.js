'use strict';

angular.module('etmApp').controller('ReviewEditController', function ($scope, $rootScope, $stateParams, Review, Principal, Evaluation, User, Feedback, EvaluationUtil) {
  $scope.review = {};
  $scope.currentUser = User.profile();
  $scope.load = function (id) {
    Evaluation.reset();
    Review.get({id: id}, function (result) {
      $scope.review = result;
      $scope.reviewTitle = $scope.getReviewTitle();
      Feedback.query({reviewId: $scope.review.id}, function (feedback) {
        $scope.isRevieweeAndFeedbackOpen = $scope.review.reviewee.id === $scope.currentUser.id && feedback[0].feedbackStatus.id === 2;
      });
    });
  };

  // Validation listeners for showing icon on evaluation and overall tabs
  $scope.evaluationInvalid = false;
  $scope.overallInvlaid = false;
  $rootScope.$on('evaluation-valid', function(){ $scope.evaluationInvalid = false; });
  $rootScope.$on('evaluation-invalid', function(){ $scope.evaluationInvalid = true; });
  $rootScope.$on('overall-valid', function(){ $scope.overallInvlaid = false; });
  $rootScope.$on('overall-invalid', function(){ $scope.overallInvlaid = true; });


  
  $scope.getReviewTitle = function () {
    var bulkTitle = ' ' + $scope.review.reviewType.name + ' for ' + $scope.review.reviewee.firstName + ' ' + $scope.review.reviewee.lastName;
    if ($scope.review.reviewType.processName === 'annualReview') {
      return $scope.review.startDate.getFullYear() + '-' + $scope.review.endDate.getFullYear() + bulkTitle;
    } else {
      var q = moment($scope.review.startDate).format('Q');
      return $scope.review.startDate.getFullYear() + ' Quarter ' + q + bulkTitle;
    }
  }

  if ($stateParams.review) {
    $scope.review = $stateParams.review;
  } else {
    $scope.load($stateParams.id);
  }
  
  $scope.isAnnual = function () {
      if ($scope.review.reviewType !== undefined && $scope.review.reviewType.processName === "annualReview") {
        return true;
      }
    return false;
  }
  
//  $scope.isRevieweeAndFeedbackOpen = function () {
//    console.log($scope.review.reviewee !== undefined && $scope.review.reviewee.id === $scope.currentUser.id && $scope.feedback !== undefined && $scope.feedback.feedbackStatus !== undefined && $scope.feedback.feedbackStatus.id === 3);
//    return $scope.review.reviewee !== undefined && $scope.review.reviewee.id === $scope.currentUser.id && $scope.feedback !== undefined && $scope.feedback.feedbackStatus !== undefined && $scope.feedback.feedbackStatus.id === 3;
//  }
});
