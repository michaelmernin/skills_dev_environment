'use strict';

angular.module('etmApp').controller('ReviewAuditDialogController',
    ['$scope', '$stateParams', '$modalInstance', '$q', 'entity', 'ReviewAudit', 'Review', 'User',
        function($scope, $stateParams, $modalInstance, $q, entity, ReviewAudit, Review, User) {

        $scope.reviewAudit = entity;
        $scope.reviews = Review.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            ReviewAudit.get({id : id}, function(result) {
                $scope.reviewAudit = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('etmApp:reviewAuditUpdate', result);
            $modalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.reviewAudit.id != null) {
                ReviewAudit.update($scope.reviewAudit, onSaveSuccess, onSaveError);
            } else {
                ReviewAudit.save($scope.reviewAudit, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
