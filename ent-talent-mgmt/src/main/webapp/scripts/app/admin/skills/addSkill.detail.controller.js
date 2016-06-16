'use strict';

angular.module('etmApp').controller('AddSkillDetailController', function ($scope, $mdDialog,$mdToast,$q, skillCategories, Skill) {

  $scope.skillCategories = angular.copy(skillCategories);
  
  $scope.cancel = function () {
    $mdDialog.cancel();
  };
  
  $scope.saveSkill = function(){
    var skillCategoryId = $scope.skillCategory;
    var promise = null;
    $mdDialog.hide($scope.user);
    promise = Skill.save({
    name: $scope.title,
    skillcategory: {id: skillCategoryId}
    }).$promise.then(function (saved) {
      $mdToast.show(
          $mdToast.simple()
            .textContent('"'+ saved.name +'"' + ' added Successfully')
            .hideDelay(3000)
        );
    }, function() {
          return $q.reject('Error Occured while saving Skill.');
    });
  };
 
});
