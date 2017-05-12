'use strict';

angular.module('etmApp').controller('ReviewEditController', function ($scope, $stateParams, Review, Principal, Evaluation) {
  $scope.review = {};
  $scope.load = function (id) {
    Evaluation.reset();
    Review.get({id: id}, function (result) {
      $scope.review = result;
      $scope.reviewTitle = $scope.getReviewTitle();
    });
  };
  
  $scope.getReviewTitle = function () {
    var bulkTitle = ' ' + $scope.review.reviewType.name + ' for ' + $scope.review.reviewee.firstName + ' ' + $scope.review.reviewee.lastName;
    if ($scope.review.processName === 'annualReview') {
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
});
