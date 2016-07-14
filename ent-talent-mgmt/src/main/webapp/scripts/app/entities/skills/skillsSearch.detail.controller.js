'use strict';

angular.module('etmApp').controller('SkillsSearchDetailController', function ($scope, $mdDialog, $mdToast, skill, Skill) {

  $scope.skill = angular.copy(skill);

  $scope.cancel = function () {
    //$mdDialog.hide(skillCategory);
    $mdDialog.cancel();
  };
  
});
