angular.module('etmApp').controller('SkillsGuidelinesController', function ($scope, $mdDialog) { 
  $scope.close = function() {
    $mdDialog.hide();
  };
  $scope.cancel = function() {
    $mdDialog.cancel();
  };
});
