'use strict';

angular.module('etmApp').controller('OverallDetailController', function ($scope, $mdDialog, feedback,userRole) {
  
  $scope.feedback = feedback;
  $scope.userRole = userRole;
  $scope.close = function () {
    $mdDialog.hide($scope.feedback);
  };
});
