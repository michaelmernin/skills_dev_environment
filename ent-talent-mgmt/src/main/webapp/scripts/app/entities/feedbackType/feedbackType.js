'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('feedbackType', {
                parent: 'entity',
                url: '/feedbackType',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.feedbackType.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/feedbackType/feedbackTypes.html',
                        controller: 'FeedbackTypeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('feedbackType');
                        return $translate.refresh();
                    }]
                }
            })
            .state('feedbackTypeDetail', {
                parent: 'entity',
                url: '/feedbackType/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'etmApp.feedbackType.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/feedbackType/feedbackType-detail.html',
                        controller: 'FeedbackTypeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('feedbackType');
                        return $translate.refresh();
                    }]
                }
            });
    });
