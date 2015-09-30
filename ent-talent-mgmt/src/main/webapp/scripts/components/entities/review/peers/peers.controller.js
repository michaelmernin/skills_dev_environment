'use strict';

angular.module('etmApp').controller('PeersController', function ($scope, $stateParams, $mdDialog, Review, User) {
  $scope.peers = [];
  $scope.load = function (id) {
    Review.peers({id: id}, function (result) {
      $scope.peers = result;
    });
  };
  $scope.load($stateParams.id);
  
  $scope.getMatches = function (query) {
     return User.autocomplete({query: query}).$promise;
  };
  
  $scope.peerSelected = function (user) {
    if (user.login != null) {
      $scope.peers.push(user);
    }
 };
 
 $scope.deletePeer = function (user, ev) {
   var confirmDelete = $mdDialog.confirm()
     .title('Remove Peer from Review')
     .ariaLabel('Remove Peer')
     .content('Remove ' + user.firstName + ' ' + user.lastName + ' from this review?')
     .ok('Remove')
     .cancel('Cancel')
     .targetEvent(ev);
   $mdDialog.show(confirmDelete).then(function () {
         $scope.peers.splice($scope.peers.indexOf(user), 1);
   });
 };
});
