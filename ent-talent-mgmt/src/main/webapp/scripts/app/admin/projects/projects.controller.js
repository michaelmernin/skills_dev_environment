'use strict';

angular.module('etmApp').controller('ProjectsController', function ($scope, $mdDialog, $mdToast, $filter, Project) {
  $scope.projects = [];
  $scope.loadAll = function () {
    Project.getAll(function (result) {
      $scope.projects = result;
    });
  };
  $scope.loadAll();

  $scope.viewProjectDetails = function (project, ev) {
    $mdDialog.show({
      controller: 'ProjectDetailController',
      templateUrl: 'scripts/app/admin/projects/project.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        project: project
      }
    }).then(function (updatedProject) {
      angular.copy(updatedProject, project);
      Project.update(project)
      .$promise.then(function () {showToast('Updated project'+ project.name)},showToast);
    });
  };
  
  $scope.deleteProject = function (project, ev) {
    var confirmDelete = $mdDialog.confirm()
      .title('Confirm Project Deletion')
      .ariaLabel('Delete Project Confirm')
      .content('Delete ' + project.name +'?')
      .ok('Delete')
      .cancel('Cancel')
      .targetEvent(ev);
    $mdDialog.show(confirmDelete).then(function () {
      Project.delete({id: project.id}, function () {
        $scope.projects.splice($scope.projects.indexOf(project), 1);
      }).$promise.then(function () {showToast('Deleted project'+ project.name)},showToast);
    });
  };

  function showToast(msg){
    $mdToast
    .show($mdToast.simple()
       .textContent(msg)
       .hideDelay(3000));
  };

});
