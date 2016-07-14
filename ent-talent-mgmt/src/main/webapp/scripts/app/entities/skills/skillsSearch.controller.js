'use strict';

angular.module('etmApp').controller('SkillsSearchController', function ($scope, SkillCategory, $mdDialog, $timeout,$mdToast, $filter) {

  $scope.skillCategories = [];
  $scope.allSkills = [];


  var displaySkillsCategories = function() {
    SkillCategory.search(function (result) {
      $scope.skillCategories = result;
     
      angular.forEach($scope.skillCategories, function (category) {
        angular.forEach(category.skills, function (skill) {
          skill._lowername = skill.name.toLowerCase();
          skill.check = false;
          $scope.allSkills.push(skill);
        });
      });
    });
  }

  displaySkillsCategories();
  
  $scope.removeCheck = function (chip) {
    chip.check = false;
  };
  
  $scope.addRemoveChip = function (skill) {
    if(skill.check){
      $scope.selectedSkills.push(skill);
    }else{
      var i = $scope.selectedSkills.indexOf(skill);
      if(i != -1) {
        $scope.selectedSkills.splice(i, 1);
      }
    }
  };
  
  $scope.viewUsers = function (skill, ev) {
    $mdDialog.show({
      controller: 'SkillsSearchDetailController',
      templateUrl: 'scripts/app/entities/skills/skillsSearch.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        skill:skill
      }
    }).then(function(skillCategory){
      displaySkillCategories();
      });
  };
  

  var self = $scope;
  self.readonly = false;
  self.selectedItem = null;
  self.searchText = null;
  self.querySearch = querySearch;
  self.selectedSkills = [];
  self.numberBuffer = '';
  self.autocompleteDemoRequireMatch = true;
  self.transformChip = transformChip;
 
  /**
   * Return the proper object when the append is called.
   */
  function transformChip(chip) {
    chip.check = true;
    // If it is an object, it's already a known chip
    if (angular.isObject(chip)) {
      return chip;
    }
    // Otherwise, create a new one
    return { name: chip, type: 'new' }
  }
  
  /**
   * Search for skills.
   */
  function querySearch (query) {
    var results = query ? self.allSkills.filter(createFilterFor(query)) : [];
    return results;
  }
  
  /**
   * Create filter function for a query string
   */
  function createFilterFor(query) {
    var lowercaseQuery = angular.lowercase(query);
    return function filterFn(skill) {
      return (skill._lowername.indexOf(lowercaseQuery) === 0);
    };
  }
  
  $scope.showChart = function (ev) {
    $mdDialog.show({
      controller: 'SkillsSearchChartsDetailController',
      templateUrl: 'scripts/app/entities/skills/skillsSearchCharts.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        selectedSkills:$scope.selectedSkills
      }
    }).then(function(skillCategory){
      displaySkillCategories();
      });
  };

});
