'use strict';

angular.module('etmApp').controller('PeersController', function ($scope, $stateParams, $mdDialog, Review, User, Peer) {
  $scope.peers = [];
  var review = {};
  $scope.$parent.$watch('review', function (parentReview) {
    if (parentReview.id) {
      review = parentReview;
      $scope.peers = review.peers;
    }
  });
  
  $scope.getMatches = function (query) {
     return User.autocomplete({query: query}).$promise;
  };
  
  $scope.peerSelected = function (user) {
    if (user != null) {
      if (user.login != null) {
        $scope.peers.push(user);
        Peer.save({reviewId: review.id}, {id: user.id});
      }
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
         Peer.delete({reviewId: review.id, id: user.id});
   });
 };
});
