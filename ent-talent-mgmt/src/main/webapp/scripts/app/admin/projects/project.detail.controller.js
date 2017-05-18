'use strict';

angular.module('etmApp').controller('ProjectDetailController', function ($scope, $mdDialog, Authority, project, dialogType, User) {
  $scope.project = angular.copy(project);
  $scope.project.projectMembers = $scope.project.projectMembers || [];
  // date conversions
  $scope.project.startDate = new Date($scope.project.startDate);
  $scope.project.endDate = new Date($scope.project.endDate);

  // set scope vars for auto-completes
  $scope.selectedManager = $scope.project.manager;
  $scope.managerSearchText = "";
  $scope.selectedMember = "";
  $scope.memberSearchText = "";

  // set dialogType (add or edit)
  $scope.dialogType = dialogType;

  // set roles
  $scope.roles = [];
  Authority.query(function (result) { $scope.roles = result; });


  $scope.cancel = function () { $mdDialog.cancel(); };
  $scope.save = function () { $mdDialog.hide($scope.project);};

  $scope.managerSelected = function(m){ $scope.project.manager = m; }

  function isNotMember(user){
    var members = $scope.project.projectMembers;
    if(!members || !members.length) return true;

    var filteredMembers =  members.filter(function (m){ return  m.id === user.id})
    return filteredMembers.length == 0;
  }

  $scope.getMatches = function (query, filterMembers) {
    return User.autocomplete({query: query, reviewId:""}).$promise
      .then(function(response){
        return filterMembers
               ? response.filter(isNotMember)
               : response;
      });
  };

  $scope.isSelected = function(value, index, array){ return $scope.project.projectMembers.indexOf(value) < 0}

  $scope.transformChip = function(chip){ return chip; };
});
