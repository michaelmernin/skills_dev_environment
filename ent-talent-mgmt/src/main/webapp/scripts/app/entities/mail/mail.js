'use strict';

angular.module('etmApp').config(function ($stateProvider) {
  $stateProvider.state('mail', {
    parent: 'entity',
    url: '/email',
    data: {
      roles: ['ROLE_USER'],
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
