'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('reviewStatus', {
                parent: 'entity',
                url: '/reviewStatus',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.reviewStatus.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reviewStatus/reviewStatuses.html',
                        controller: 'ReviewStatusController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reviewStatus');
                        return $translate.refresh();
                    }]
                }
            })
            .state('reviewStatusDetail', {
                parent: 'entity',
                url: '/reviewStatus/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.reviewStatus.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reviewStatus/reviewStatus-detail.html',
                        controller: 'ReviewStatusDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reviewStatus');
                        return $translate.refresh();
                    }]
                }
            });
    });
