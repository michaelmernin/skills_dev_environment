'use strict';

angular.module('etmApp').controller('PeersController', function ($scope, $stateParams, $mdDialog, Review, User, Peer, FeedbackStatus, Feedback, Principal) {
  $scope.peers = [];
  var review = {};
  $scope.isReviewer = false;
  $scope.isReviewee = false;
  $scope.$parent.$watch('review', function (parentReview) {
    if (parentReview.id) {
      review = parentReview;
      Peer.query({reviewId: review.id}, function (peers) {
        $scope.peers = peers;
      });
    }
  });
  
  Principal.identity().then(function (account) {
    $scope.isReviewer = account.id === review.reviewer.id;
    $scope.isReviewer = account.id === review.reviewee.id;
  });

  $scope.getMatches = function (query) {
     return User.autocomplete({query: query, reviewId: review.id}).$promise;
  };

  $scope.peerSelected = function (user) {
    if (user !== null) {
      if (user.login !== null) {        
       Peer.addPeer({reviewId: review.id, id: user.id}, {}, function(peers) {
         $scope.peers = peers;
       });      
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
    if (selectedPeers.length) {
      var actions = [];
      angular.forEach(allActions, function (action) {
        if (isActionValid(action, selectedPeers)) {
          actions.push(action);
        }
      });
      return actions;
    } else {
      return allActions;
    }
  };

  $scope.performAction = function (action, ev) {
    action.fn(getSelectedPeers(), ev);
  };
  
  function isActionValid(action, peers) {
    var valid = true;
    if (action.validStatuses && action.validStatuses.length) {
      valid = false;
      angular.forEach(peers, function (peer) {
        if (!valid && has(action.validStatuses, peer.feedbackStatus)) {
          valid = true;
        }
      });
    }
    return valid;
  }

  function getSelectedPeers() {
    var selectedPeers = [];
    angular.forEach($scope.peers, function (peer) {
      if (peer.selected) {
        selectedPeers.push(peer);
      }
    });
    return selectedPeers;
  }
  
  function openPeers(peers, ev) {
    var confirmOpen = $mdDialog.confirm()
      .title('Open Peer Feedback')
      .ariaLabel('Open Peers')
      .content('Open ' + peers.length + ' peers for this review?')
      .ok('Open')
      .cancel('Cancel')
      .targetEvent(ev);
    $mdDialog.show(confirmOpen).then(angular.bind(null, angular.forEach, peers, openPeer));
  }

  function reopenPeers(peers, ev) {
    console.dir("Reopen Peers", peers);
  }

  function deletePeers(peers, ev) {
    var confirmDelete = $mdDialog.confirm()
      .title('Remove Peers from Review')
      .ariaLabel('Remove Peers')
      .content('Remove ' + peers.length + ' peers from this review?')
      .ok('Remove')
      .cancel('Cancel')
      .targetEvent(ev);
    $mdDialog.show(confirmDelete).then(angular.bind(null, angular.forEach, peers, deletePeer));
  }
  
  function deletePeer(peer) {
    $scope.peers.splice($scope.peers.indexOf(peer), 1);
    Peer.delete({reviewId: review.id, id: peer.id});
  }
  
  function openPeer(peer) {
    if (eq(peer.feedbackStatus, FeedbackStatus.NOT_SENT)) {
      peer.feedbackStatus = FeedbackStatus.OPEN;
      Feedback.open({reviewId: review.id, id: peer.feedbackId}, {reviewId: review.id, id: peer.feedbackId});
    }
  }
  
  function has(array, element) {
    var result = false;
    for (var i = 0; i < array.length; ++i) {
      if (eq(array[i], element)) {
        result = true;
        break;
      }
    }
    return result;
  }
  
  function eq(e1, e2) {
    if (e1 === undefined || e2 === undefined) {
      return e1 === e2;
    }
    return e1.id === e2.id;
  }
});
