'use strict';

angular.module('etmApp').directive('etmRating', function () {
  return {
    restrict: 'E',
    templateUrl: 'scripts/components/entities/rating/rating.directive.html',
    scope: {
      rating: '=ngModel',
      disabled: '=ngDisabled'
    },
    controller: function ($scope) {
      var score = null;

      $scope.setNA = function () {
        if ($scope.na) {
          score = $scope.rating.score;
          $scope.rating.score = -1;
        } else {
          $scope.rating.score = score;
        }
      }

      if ($scope.rating) {
        $scope.na = $scope.rating.score === -1;
      }
    }
  };
});
