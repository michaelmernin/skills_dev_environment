'use strict';

angular.module('etmApp').controller('SettingsController', function ($scope, $mdDialog, $mdToast, $q, Principal, User, SkillCategory, SkillRanking) { 
  $scope.user = {};
  $scope.profile = {};  

  Principal.identity().then(function (account) {
    $scope.user = account;
  });

  $scope.loadProfile = function () {
    User.profile(function (result) {
      $scope.profile = result;
    });
  };
  $scope.loadProfile();

  $scope.displaySkillCategories = function(){
    SkillCategory.query(function (result){
      $scope.skillCategories =result; 
      angular.forEach($scope.skillCategories, function (category) {
        angular.forEach(category.skills, function (skill) {
          skill.ranking = skill.rankings[0] || {};
          delete skill.rankings;
          skill.ranking.initialRank = skill.ranking.rank;
        });
      });
    });
  }
  $scope.displaySkillCategories();

  $scope.cancel = function(){
    $scope.displaySkillCategories();
  }

  $scope.saveRankings = function (skillCategories) {
    var promises = [];
    angular.forEach(skillCategories, function (category) {
      angular.forEach(category.skills, function (skill) {
        var ranking = skill.ranking;
        var promise = null;
        if (ranking && ranking.initialRank !== ranking.rank) {
          if (!ranking.id) {
            promise = SkillRanking.save({
              rank: ranking.rank,
              skill: {id: skill.id}
            }).$promise.then(function (saved) {
              ranking.id = saved.id;
              ranking.initialRank = saved.rank;
            }, function(){
                  return $q.reject('Error Occured while saving Skill Rankings.');
            });
          } else {
            promise = SkillRanking.update({
              id: ranking.id
            }, {
              id: ranking.id,
              rank:ranking.rank,
              skill: {id: skill.id}
            }, function (updated) {
              ranking.initialRank = updated.rank;
            }).$promise;
          }
          promises.push(promise);
        }
      });
    });
    $q.all(promises).then(function () {
      $mdToast.show(
          $mdToast.simple()
            .textContent('Save Successful')
            .hideDelay(3000)
        );
    }, function (msg) {
      //oops one of them failed
      $mdToast.show(
          $mdToast.simple()
            .textContent(msg)
            .hideDelay(5000)
      )
    });
  }

  $scope.viewSkillGuideline = function (ev) {
    $mdDialog.show({
      controller: SkillGuidelineController,
      templateUrl: 'scripts/components/entities/skill/skillCategory/skill.guideline.html',
      parent: angular.element(document.body),
      targetEvent: ev
    })
  };

});

function SkillGuidelineController($scope, $mdDialog) {
  $scope.close = function() {
    $mdDialog.hide();
  };
  $scope.cancel = function() {
    $mdDialog.cancel();
  };
}
