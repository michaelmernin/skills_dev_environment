'use strict';

angular.module('etmApp').directive('etmRating', function () {
  return {
    restrict: 'E',
    templateUrl: 'scripts/components/entities/rating/rating.directive.html',
    scope: {
      rating: '=ngModel',
      disabled: '=ngDisabled',
      readonly: '=ngReadonly'
    },
    controller: function ($scope) {
      var score = null;
      $scope.form = {};
      
      $scope.setNA = function () {
        if ($scope.na) {
          score = $scope.rating.score;
          $scope.rating.score = -1;
        } else {
          $scope.rating.score = score;
        }
      }

      $scope.getScore = function () {
        if ($scope.na) {
          return 'N/A';
        }
        return $scope.rating.score;
      }

      if ($scope.rating) {
        if ($scope.rating.score === null) {
          $scope.rating.score = undefined;
        }
        $scope.na = $scope.rating.score === -1;
      }

      $scope.$watch('form.ratingForm.$dirty', function (dirty) {
        if (dirty !== undefined) {
          $scope.rating.$dirty = dirty;
        }
      });
    }
  };
});
