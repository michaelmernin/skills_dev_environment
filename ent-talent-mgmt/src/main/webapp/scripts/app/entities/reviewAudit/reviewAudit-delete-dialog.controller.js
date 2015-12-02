'use strict';

angular.module('etmApp')
	.controller('ReviewAuditDeleteController', function($scope, $modalInstance, entity, ReviewAudit) {

        $scope.reviewAudit = entity;
        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            ReviewAudit.delete({id: id},
                function () {
                    $modalInstance.close(true);
                });
        };

    });