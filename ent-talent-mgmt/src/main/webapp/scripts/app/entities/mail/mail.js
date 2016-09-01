'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('mail', {
    parent: 'entity',
    url: '/mail',
    data: {
      roles: ['ROLE_USER','ROLE_GENERAL_MANAGER'],
      pageTitle: 'mail.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/entities/mail/mail.html',
        controller: 'MailController'
      }
    },
    resolve: {
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('mail');
        return $translate.refresh();
      }]
    }
  });
});
