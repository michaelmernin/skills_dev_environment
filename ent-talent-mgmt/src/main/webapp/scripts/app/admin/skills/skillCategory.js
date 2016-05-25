'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('skillCategory', {
    parent: 'entity',
    url: '/skillCategory',
    data: {
      roles: ['ROLE_USER'],
      pageTitle: 'global.menu.skills'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/admin/skills/skillCategory.html',
        controller: 'SkillCategoryController'
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
