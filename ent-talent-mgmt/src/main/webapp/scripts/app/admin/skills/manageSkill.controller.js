'use strict';

angular.module('etmApp').controller('ManageSkillController', function ($scope, $mdDialog, $timeout,$mdToast, $filter, Skill) {

  $scope.skills = [];

  var displaySkills = function() {
    Skill.query(function (result) {
      $scope.skills = result;
    });
  }

  displaySkills();

  $scope.deleteSkill = function(skill, ev){
    var confirmDelete = $mdDialog.confirm()
    .title('Confirm Skill Deletion')
    .ariaLabel('Delete Skill Confirm')
    .content('Delete ' + skill.name + '?')
    .ok('Delete')
    .cancel('Cancel')
    .targetEvent(ev);
    $mdDialog.show(confirmDelete).then(function () {
      Skill.delete({id: skill.id}, function () {
        $scope.skills.splice($scope.skills.indexOf(skill), 1);
      });
    });
  };

  $scope.$watch('demo.isOpen', function(isOpen) {
    if (isOpen) {
      $timeout(function() {
        $scope.tooltipVisible = true;
      }, 400);
    } else {
      $scope.tooltipVisible = false;
    }
  });

  $scope.enableDisableSkill = function (skill) {
    /*skill.enabled= !skill.enabled;*/
    var promise = null;
    promise = Skill.update({
      id: skill.id
    }, {
      id: skill.id,
      name: skill.name,
      enabled:skill.enabled,
      skillcategory: { id: skill.skillCategoryId}
    }).$promise.then(function (updatedSkill) {
      var flag = updatedSkill.enabled ? 'enabled': 'disabled';
      $mdToast.show(
          $mdToast.simple()
          .textContent(updatedSkill.name + ' is ' + flag )
          .hideDelay(3000)
      );
    }); 
  };

});
