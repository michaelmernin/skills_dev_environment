'use strict';

angular.module('etmApp').controller('ReviewEditController', function ($scope, $stateParams, Review, Principal) {
  $scope.review = {};
  $scope.load = function (id) {
    Review.get({id: id}, function (result) {
      $scope.review = result;
    });
  };

  Principal.identity().then(function (account) {
    $scope.allTabs = function () {
      return $scope.review.id &&
        (  $scope.review.reviewee.id === account.id
        || $scope.review.reviewer.id === account.id
        || $scope.review.reviewee.counselor.id === account.id
        || $scope.review.reviewee.generalManager.id === account.id);
    };
  });

  if ($stateParams.review) {
    $scope.review = $stateParams.review;
  } else {
    $scope.load($stateParams.id);
  }
});
