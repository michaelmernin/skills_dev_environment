'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('reviewType', {
                parent: 'entity',
                url: '/reviewType',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.reviewType.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reviewType/reviewTypes.html',
                        controller: 'ReviewTypeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reviewType');
                        return $translate.refresh();
                    }]
                }
            })
            .state('reviewTypeDetail', {
                parent: 'entity',
                url: '/reviewType/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.reviewType.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reviewType/reviewType-detail.html',
                        controller: 'ReviewTypeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reviewType');
                        return $translate.refresh();
                    }]
                }
            });
    });
