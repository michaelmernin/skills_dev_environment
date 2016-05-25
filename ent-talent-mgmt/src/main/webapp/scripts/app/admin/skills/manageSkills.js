'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('manageSkills', {
    parent: 'entity',
    url: '/manageSkill',
    data: {
      roles: ['ROLE_USER'],
      pageTitle: 'global.menu.skills'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/admin/skills/manageSkills.html',
        controller: 'ManageSkillController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('skills');
        return $translate.refresh();
      }]
    }
  });
});
