'use strict';

angular.module('etmApp').controller('UsersController', function ($scope, $mdDialog, $mdToast, User) {
  $scope.foundUsers = null;
  $scope.userText = '';

  function toast(str){
    $mdToast.show(
      $mdToast.simple()
      .textContent(str)
      .hideDelay(3000)
    );
  }

  $scope.findUsers = function() {
    var query = $scope.userText;
    query = query.length ? query.trim() : '';
    var queryLength = query.length;
    if (queryLength >= 3) {
      return User.autocomplete({query: query, reviewId:""}).$promise
        .then(function(response){
          var nbrUsers = response.length || 0;
          $scope.foundUsers = nbrUsers > 10 ? response.slice(0,11) : response;
        });
    } else {
      $scope.foundUsers = [];
    }
  };

  $scope.viewUserDetails = function (user, ev) {
    if (user.id == null){
      toast('userWithoutId error');
      console.error("cannot edit user without id", user);
      return;
    }
    User.get({id:user.login})
    .$promise
    .then(function(u){
      $mdDialog.show({
        controller: 'UserDetailController',
        templateUrl: 'scripts/app/admin/users/user.detail.html',
        parent: angular.element(document.body),
        targetEvent: ev,
        locals: {
          user: u
        }
      }).then(function (updatedUser) {
        angular.copy(updatedUser, user);
        User.update(user)
        .$promise
        .then(function(user){
          toast("updated user"+ user.login)
        });
      });
    })
  };

  $scope.deleteUser = function (user, ev) {
    var confirmDelete = $mdDialog.confirm()
      .title('Confirm User Deletion')
      .ariaLabel('Delete User Confirm')
      .content('Delete ' + user.firstName + ' ' + user.lastName + '?')
      .ok('Delete')
      .cancel('Cancel')
      .targetEvent(ev);
    $mdDialog.show(confirmDelete).then(function () {
      User.delete({id: user.id});
    });
  };
});
