'use strict';

angular.module('etmApp').directive('etmRating', function (Principal) {
  return {
    restrict: 'E',
    templateUrl: 'scripts/components/entities/rating/rating.directive.html',
    scope: {
      rating: '=ngModel',
      disabled: '=ngDisabled',
      readonly: '=ngReadonly',
      reviewer: '=ngReviewer'
    },
    
    controller: function ($scope) {
      var score = 0;
      $scope.form = {};
      var user = {};
      Principal.identity().then(function (account) {
        user = account;
      });
      
      $scope.setNA = function () {
        if ($scope.na) {
          score = $scope.rating.score;
          $scope.rating.score = 0;
        } else {
          $scope.rating.score = score;
        }
      };
      
      $scope.isEditable = function () {
        if ($scope.rating.feedback.author.id == user.id) {
         return true;
        } else {
          return false;
        }
      };
      
      $scope.getScore = function () {
        if ($scope.rating.score === 0) {
          return 'N/A';
        }
        return $scope.rating.score;
      };

      if ($scope.rating) {
        if ($scope.rating.score === null) {
          $scope.rating.score = undefined;
        }
        $scope.na = $scope.rating.score === 0;
      }

      $scope.$watch('form.ratingForm.$dirty', function (dirty) {
        if (dirty !== undefined) {
          $scope.rating.$dirty = dirty;
        }
      });
    }
  };
});
