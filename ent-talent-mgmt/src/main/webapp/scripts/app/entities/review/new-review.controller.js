'use strict';

angular.module('etmApp').controller('NewReviewController', function ($scope, Review, ReviewType, Principal, User) {
  $scope.review = {};
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
      console.dir($scope.review);
    } else {
      $scope.reviewForm.$setDirty(true);
    }
  };
});
