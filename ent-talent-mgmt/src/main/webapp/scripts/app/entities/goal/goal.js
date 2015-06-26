'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('goal', {
                parent: 'entity',
                url: '/goal',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Goals'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/goal/goals.html',
                        controller: 'GoalController'
                    }
                },
                resolve: {
                }
            })
            .state('goalDetail', {
                parent: 'entity',
                url: '/goal/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Goal'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/goal/goal-detail.html',
                        controller: 'GoalDetailController'
                    }
                },
                resolve: {
                }
            });
    });
