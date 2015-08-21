'use strict';

angular.module('etmApp').controller('NewReviewController', function ($scope, $state, Review, ReviewType, Principal, User) {
  $scope.review = new Review();
  $scope.reviewTypes = [];
  $scope.reviewees = [];

  Principal.identity().then(function(account) {
    $scope.reviewees.push(account);
    if (Principal.isInRole('ROLE_COUNSELOR')) {
      //get counselees
    }
  });

  $scope.load = function () {
    ReviewType.query(function (result) {
      $scope.reviewTypes = result;
    });
  };
  $scope.load();

  $scope.save = function () {
    if ($scope.reviewForm.$valid) {
      $scope.review.$save(function (review) {
        $state.go('review.detail', {review: review, id: review.id});
      });
    }
  };

  $scope.minEndDate = function () {
    if ($scope.reviewForm.startDate.$valid) {
      var minDate = new Date($scope.review.startDate);
      console.dir(minDate);
      minDate.setMonth(minDate.getMonth() + 12);
      console.dir(minDate);
      return minDate;
    }
    return '';
  };
});
