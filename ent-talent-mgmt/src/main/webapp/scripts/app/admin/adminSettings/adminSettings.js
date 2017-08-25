'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('adminsettings', {
    parent: 'admin',
    url: '/adminsettings',
    data: {
      roles: ['ROLE_ADMIN'],
      pageTitle: 'Admin Settings'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/admin/adminSettings/adminSettings.html',
        controller: 'AdminSettingsController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('activiti');
        return $translate.refresh();
      }]
    }
  });
});