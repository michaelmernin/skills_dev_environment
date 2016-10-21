'use strict';

angular.module('etmApp').controller('AddSkillCategoryDetailController', function ($scope, $mdDialog,$mdToast,$q, SkillCategory, skillCategories) {
   
  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.saveSkillCategory = function (title) {
    var promise = null;
    $mdDialog.hide($scope.user);
    promise = SkillCategory.save({
    title: title
    }).$promise.then(function (saved) {
      skillCategories.push(saved);
      $mdToast.show(
          $mdToast.simple()
            .textContent(saved.title + ' saved Successfully')
            .hideDelay(3000)
        );
    }, function() {
          return $q.reject('Error Occured while saving Skill Categories.');
    });
  };
  
});
