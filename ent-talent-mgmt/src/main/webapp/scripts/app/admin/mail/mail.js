'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('mail', {
    parent: 'admin',
    url: '/mail',
    data: {
      roles: ['ROLE_ADMIN'],
      pageTitle: 'mail.title'
    },
    views: {
      'content@': {
        templateUrl: 'scripts/app/admin/mail/mail.html',
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
