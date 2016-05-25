'use strict';

angular.module('etmApp').controller('SkillCategoryController', function ($scope, $mdDialog, $mdToast, $timeout, $filter, SkillCategory) {

  var self = this;
  self.isOpen = false;

  $scope.skillCategories = [];
  $scope.skillCategory;

  var displaySkillCategories = function() {
    SkillCategory.findAll(function (result) {
      $scope.skillCategories = result;
    });
  }

  displaySkillCategories();

  $scope.viewSkillCategoryDetails = function (skillCategory, ev) {
    $mdDialog.show({
      controller: 'SkillCategoryDetailController',
      templateUrl: 'scripts/app/admin/skills/skillCategory.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        skillCategory:skillCategory
      }
    }).then(function(skillCategory){
      displaySkillCategories();
      });
  };

  $scope.addSkillCategory = function (ev) {
    $mdDialog.show({
      controller: 'AddSkillCategoryDetailController',
      templateUrl: 'scripts/app/admin/skills/addSkillCategory.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev
    })
  };

  $scope.addSkill = function (skillCategories, ev) {
    $mdDialog.show({
      controller: 'AddSkillDetailController',
      templateUrl: 'scripts/app/admin/skills/addSkill.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        skillCategories:skillCategories
      }
    })
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

  $scope.enableDisableSkillCategory = function (sc,ev) {
    sc.enabled= !sc.enabled;
    var promise = null;
    promise = SkillCategory.update({
      id: sc.id
    }, {
      id: sc.id,
      title: sc.title,
      enabled:sc.enabled,
    }).$promise.then(function (updatedSkillCategory) {
      var flag = updatedSkillCategory.enabled ? 'enabled': 'disabled';
      $mdToast.show(
          $mdToast.simple()
          .textContent(updatedSkillCategory.title + ' is ' + flag )
          .hideDelay(3000)
      );
    }); 
  };

});
