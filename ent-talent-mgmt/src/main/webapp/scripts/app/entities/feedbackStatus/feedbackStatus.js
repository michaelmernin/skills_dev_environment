'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('feedbackStatus', {
                parent: 'entity',
                url: '/feedbackStatus',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.feedbackStatus.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/feedbackStatus/feedbackStatuses.html',
                        controller: 'FeedbackStatusController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('feedbackStatus');
                        return $translate.refresh();
                    }]
                }
            })
            .state('feedbackStatusDetail', {
                parent: 'entity',
                url: '/feedbackStatus/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.feedbackStatus.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/feedbackStatus/feedbackStatus-detail.html',
                        controller: 'FeedbackStatusDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('feedbackStatus');
                        return $translate.refresh();
                    }]
                }
            });
    });
