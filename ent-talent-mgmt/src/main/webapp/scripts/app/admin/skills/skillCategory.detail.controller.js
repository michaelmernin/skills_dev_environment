'use strict';

angular.module('etmApp').controller('SkillCategoryDetailController', function ($scope, $mdDialog, $mdToast, skillCategory, Skill) {

  $scope.skillCategory = angular.copy(skillCategory);

  $scope.cancel = function () {
    $mdDialog.hide(skillCategory);
    $mdDialog.cancel();
  };

  $scope.deleteSkill = function(sc,skill, ev){
    var confirmDelete = $mdDialog.confirm()
    .title('Confirm Skill Deletion')
    .ariaLabel('Delete Skill Confirm')
    .content('Delete ' + skill.name + ' from ' + sc.title + 'Category ?')
    .ok('Delete')
    .cancel('Cancel')
    .targetEvent(ev);
    $mdDialog.show(confirmDelete).then(function () {
      Skill.delete({id: skill.id}, function () {
        $scope.skillCategory.skills.splice($scope.skillCategory.skills.indexOf(skill), 1);
        skillCategory.skills.splice(skillCategory.skills.indexOf(skill), 1);
      });
    });
  };
  
  $scope.enableDisableSkill = function (skill,ev) {
    skill.enabled= !skill.enabled;
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
