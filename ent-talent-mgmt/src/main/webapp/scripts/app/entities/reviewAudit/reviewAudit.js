'use strict';

angular.module('etmApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('reviewAudit', {
                parent: 'entity',
                url: '/reviewAudits',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'ReviewAudits'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reviewAudit/reviewAudits.html',
                        controller: 'ReviewAuditController'
                    }
                },
                resolve: {
                }
            })
            .state('reviewAudit.detail', {
                parent: 'entity',
                url: '/reviewAudit/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'ReviewAudit'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reviewAudit/reviewAudit-detail.html',
                        controller: 'ReviewAuditDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'ReviewAudit', function($stateParams, ReviewAudit) {
                        return ReviewAudit.get({id : $stateParams.id});
                    }]
                }
            })
            .state('reviewAudit.new', {
                parent: 'reviewAudit',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/reviewAudit/reviewAudit-dialog.html',
                        controller: 'ReviewAuditDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    reviewAuditId: null,
                                    date: null,
                                    comment: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('reviewAudit', null, { reload: true });
                    }, function() {
                        $state.go('reviewAudit');
                    })
                }]
            })
            .state('reviewAudit.edit', {
                parent: 'reviewAudit',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/reviewAudit/reviewAudit-dialog.html',
                        controller: 'ReviewAuditDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ReviewAudit', function(ReviewAudit) {
                                return ReviewAudit.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('reviewAudit', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('reviewAudit.delete', {
                parent: 'reviewAudit',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/reviewAudit/reviewAudit-delete-dialog.html',
                        controller: 'ReviewAuditDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['ReviewAudit', function(ReviewAudit) {
                                return ReviewAudit.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('reviewAudit', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
