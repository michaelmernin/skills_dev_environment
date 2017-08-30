'use strict';

angular.module('etmApp').controller('ReviewDetailController', function ($scope, $stateParams, Review, Principal) {
  $scope.review = {};
  $scope.load = function (id) {
    Review.get({id: id}, function (result) {
      $scope.review = result;
      $scope.reviewTitle = $scope.getReviewTitle();
    });
  };
  
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
});