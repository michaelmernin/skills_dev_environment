'use strict';

angular.module('etmApp').controller('PeersController', function ($scope, $stateParams, $mdDialog, Review, User, Peer, FeedbackStatus) {
  $scope.peers = [];
  var review = {};
  $scope.$parent.$watch('review', function (parentReview) {
    if (parentReview.id) {
      review = parentReview;
      Peer.query({reviewId: review.id}, function (peers) {
        $scope.peers = peers;
      });
    }
  });

  $scope.getMatches = function (query) {
     return User.autocomplete({query: query, reviewId: review.id}).$promise;
  };

  $scope.peerSelected = function (user) {
    if (user != null) {
      if (user.login != null) {
        $scope.peers.push(user);
        Peer.save({reviewId: review.id}, {id: user.id});
        window.location.reload();
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
    $mdDialog.show(confirmDelete).then(angular.bind(null, deletePeer, user));
  };

  var allActions = [{
    action: 'open',
    validStatuses: [FeedbackStatus.NOT_SENT],
    translateKey: 'review.peers.open',
    fn: openPeers
    
  }, {
    action: 'reopen',
    validStatuses: [FeedbackStatus.READY],
    translateKey: 'review.peers.reopen',
    fn: reopenPeers
  }, {
    action: 'delete',
    translateKey: 'review.peers.removePeer',
    fn: deletePeers
  }];
  
  $scope.availableActions = function () {
    var selectedPeers = getSelectedPeers();
    
  };

  $scope.performAction = function (action) {
    action.fn(getSelectedPeers());
  };

  function getSelectedPeers() {
    var selectedPeers = [];
    angular.forEach($scope.peers, function (peer) {
      if (peer.selected) {
        selectedPeers.push(peer);
        peer.selected = false;
      }
    });
    return selectedPeers;
  }
  
  function openPeers(peers) {
    console.dir("Open Peers", peers);
  }

  function reopenPeers(peers) {
    console.dir("Reopen Peers", peers);
  }

  function deletePeers(peers) {
    var confirmDelete = $mdDialog.confirm()
      .title('Remove Peers from Review')
      .ariaLabel('Remove Peers')
      .content('Remove ' + peers.length + 'peers from this review?')
      .ok('Remove')
      .cancel('Cancel')
      .targetEvent(ev);
    $mdDialog.show(confirmDelete).then(angular.bind(null, angular.each, peers, deletePeer));
  }
  
  function deletePeer(peer) {
    $scope.peers.splice($scope.peers.indexOf(peer), 1);
    Peer.delete({reviewId: review.id, id: peer.id});
  }
});
