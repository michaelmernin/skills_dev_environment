'use strict';

angular.module('etmApp').controller('OverallController', function ($scope, $mdDialog, $mdMedia, $window, Principal, Review, Feedback,FeedbackType, Rating, Evaluation) {
  
  $scope.reviewerFeedback={};
  $scope.revieweeFeedback={};
  var reviewId=$scope.$parent && $scope.$parent.review && $scope.$parent.review.id ?  $scope.$parent.review.id : -1;
  console.log(reviewId);

  $scope.getCoreAvgScore = function (property) {
    var ratings = Evaluation.getCoreAverage(property);
    return Evaluation.avgScore(ratings);
  };

  $scope.getInternalAvgScore = function (property) {
    var ratings = Evaluation.getInternalAverage(property);
    return Evaluation.avgScore(ratings);
  };

  
  $scope.getOverall = function (property) {
    return (this.getCoreAvgScore(property) + this.getInternalAvgScore(property))/2;
};
 
  $scope.overallComment =  function (id) {
    Review.engagements({id: id}, function (result) {
      $scope.engagements = result;
    });
  };
  
  Principal.identity().then(function () {
    $scope.$parent.$watch('review.feedback', function (parentFeedback) {
      if (parentFeedback && parentFeedback.length){
        parentFeedback.forEach(function(feedbackItem) {
        if(feedbackItem.feedbackType.id === FeedbackType.SELF.id){
            $scope.revieweeFeedback= feedbackItem;
          }
          if(feedbackItem.feedbackType.id === FeedbackType.REVIEWER.id){
            $scope.reviewerFeedback = feedbackItem;
          }
        });
      }
    });
  });
  
  $scope.viewFeedback = function (feedback,userRole, ev) {
    $mdDialog.show({
	      controller: 'OverallDetailController',
	      templateUrl: 'scripts/components/entities/review/overall/overall.detail.html',
	      parent: angular.element(document.body),
	      targetEvent: ev,
	      locals: {
            feedback:feedback,
            userRole:userRole
	      }
	    }).then(function (feedback) {
	      updateDirtyFeedback(feedback);
	    });
	  };
  function updateDirtyFeedback(feedback) {
    if (feedback && feedback.$dirty) {
      Feedback.update({
        reviewId: reviewId,
        feedbackId: feedback.id
      }, feedback);
    }
  }

});
