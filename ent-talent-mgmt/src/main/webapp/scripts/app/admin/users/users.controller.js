'use strict';

angular.module('etmApp').controller('UsersController', function ($scope, $mdDialog, User) {
  $scope.users = [];
  $scope.loadAll = function () {
    User.query(function (result) {
      $scope.users = result;
    });
  };
  $scope.loadAll();

  $scope.viewUserDetails = function (user, ev) {
    $mdDialog.show({
      controller: 'UserDetailController',
      templateUrl: 'scripts/app/admin/users/user.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {user: user}
    }).then(function (updatedUser) {
      angular.copy(updatedUser, user);
      User.update(user, function () {
        $scope.refresh();
      });
    });
  };
});
