'use strict';

angular.module('etmApp').controller('ReviewListController', function ($scope, $state, $stateParams, Review, Principal, ReviewStatus) {
  var NUM_STEPS = 5;
  $scope.reviews = [];
  var user;
  Principal.identity().then(function (account) {
    user = account;
  });
  Review.query(function (result) {
    $scope.reviews = result;
  });

  $scope.getReviewProgress = function (reviewStatus) {
    var step = 0;
    if (reviewStatus && reviewStatus.id) {
      switch (reviewStatus.id) {
      case ReviewStatus.OPEN.id:
        step = 1;
        break;
      case ReviewStatus.DIRECTOR_APPROVAL.id:
        step = 2;
        break;
      case ReviewStatus.JOINT_APPROVAL.id:
        step = 3;
        break;
      case ReviewStatus.GM_APPROVAL.id:
        step = 4;
        break;
      case ReviewStatus.COMPLETE.id:
      case ReviewStatus.CLOSED.id:
        step = 5;
        break;
      default:
        break;
      }
    }
    return step * 100 / NUM_STEPS; //100% divided by steps to complete
  };

  $scope.goToReview = function (reviewId) {
    if (reviewId) {
      if (this.review.reviewStatus.id === 1) {
        // check if user is reviewee, reviewer, or peer - if yes, go to review edit. if no, go to review.detail
        if (user.id === this.review.reviewee.id) {
          $state.go('review.edit', {id: reviewId});
        } else if (user.id === this.review.reviewer.id) {
          $state.go('review.edit', {id: reviewId});
        } else {
          var matchedPeer = false;
          for (var i = 0; i < this.review.peers.length; i++) {
            if (user.id === this.review.peers[i].id) {
              matchedPeer = true;
              $state.go('review.edit', {id: reviewId});
            }
          }
          if (!matchedPeer) {
            $state.go('review.detail', {id: reviewId});
          }
        }
      } else {
        $state.go('review.detail', {id: reviewId});
      }
    } else {
      $state.go('login');
    }
  };

  $scope.reverseOrderFunction = function() {
    $scope.reverseOrder = !$scope.reverseOrder;
    // TODO - don't user jQuery
    $('.reviewOrder').toggle();
  };

  $scope.reviewSearch = function (review) {
    // if query is undefined or null, show all reviews
    if (!$scope.query) {
      return true;
    }

    var query = $scope.query ? $scope.query.toLowerCase() : '';
    return isSubstring(review.client, query) ||
      isSubstring(review.project, query) ||
      isSubstring(review.reviewType.name, query) ||
      isSubstring(review.reviewStatus.name, query) ||
      isSubstring(review.reviewee.firstName, query) ||
      isSubstring(review.reviewee.lastName, query) ||
      isSubstring(review.reviewer.firstName, query) ||
      isSubstring(review.reviewer.lastName, query);
  };

  $scope.orderByItems = [
    {key: 'Start date', value: 'startDate'},
    {key: 'End date',value: 'endDate'},
    {key: 'Client', value: 'client'},
    {key: 'Project', value: 'project'},
    {key: 'Review type', value: 'reviewType.name'},
    {key: 'Review status', value: 'reviewStatus.id'},
    {key: 'Reviewee last name', value: 'reviewee.lastName'},
    {key: 'Reviewee first name', value: 'reviewee.firstName'},
    {key: 'Reviewer last name', value: 'reviewer.lastName'},
    {key: 'Reviewer first name', value: 'reviewer.firstName'}
  ];

  function isSubstring(property, query) {
    return (property && query) ? property.toLowerCase().indexOf(query) !== -1 : false
  }

});