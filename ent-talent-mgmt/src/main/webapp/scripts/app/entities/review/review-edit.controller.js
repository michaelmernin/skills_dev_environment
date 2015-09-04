'use strict';

angular.module('etmApp').controller('ReviewEditController', function ($scope, $stateParams, Review, ReviewType, ReviewStatus, User) {
  $scope.review = {};
  $scope.load = function (id) {
    Review.get({id: id}, function(result) {
      $scope.review = result;
    });
  };
  
  if ($stateParams.review) {
    $scope.review = $stateParams.review;
  } else {
    $scope.load($stateParams.id);
  }
});
