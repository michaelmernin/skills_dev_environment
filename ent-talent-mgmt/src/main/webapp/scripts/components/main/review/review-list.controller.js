'use strict';

angular.module('etmApp').controller('ReviewListController', function ($scope, $state, $stateParams, Review, Principal, ReviewStatus) {
  var NUM_STEPS = 5;
  $scope.reviews = [];

  if (Principal.isAuthenticated()) {
    Review.query(function (result) {
      $scope.reviews = result;
    });
  } else {
    $scope.reviews = getDummyReviews();
  }

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
      $state.go('review.edit', {id: reviewId});
    } else {
      $state.go('login');
    }
  };
  
  $scope.reverseOrderFunction = function() {
    $scope.reverseOrder = !$scope.reverseOrder;
    $('.reviewOrder').toggle();
  };

  $scope.reviewSearch = function (review) {
    // if query is undefined or null, show all reviews
    if (!$scope.query) {
      return true;
    }
    
    var query = $scope.query ? $scope.query.toLowerCase() : '';
    return isSubstring(review.client, query)
      || isSubstring(review.project, query)
      || isSubstring(review.reviewType.name, query)
      || isSubstring(review.reviewStatus.name, query)
      || isSubstring(review.reviewee.firstName, query)
      || isSubstring(review.reviewee.lastName, query)
      || isSubstring(review.reviewer.firstName, query)
      || isSubstring(review.reviewer.lastName, query);
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
  
  function getDummyReviews() {
    return [{
      startDate: '2014-06-10',
      endDate: '2015-04-10',
      client: 'BestBuy',
      project: 'Open Box',
      reviewType: {name: 'Annual Review'},
      reviewStatus: ReviewStatus.OPEN,
      reviewee: {firstName: 'Jack', lastName: 'Smith'},
      reviewer: {firstName: 'David', lastName: 'Smith'}
    }, {
      startDate: '2015-03-8',
      endDate: '2015-06-21',
      client: 'Target',
      project: 'AEM',
      reviewType: {name: '3 Month Review'},
      reviewStatus: ReviewStatus.JOINT_APPROVAL,
      reviewee: {firstName: 'John', lastName: 'Doe'},
      reviewer: {firstName: 'Sam', lastName: 'Jackson'}
    }, {
      startDate: '2015-03-4',
      endDate: '2015-08-7',
      client: 'Midtronic',
      project: 'iPhone App',
      reviewType: {name: 'Engagement'},
      reviewStatus: ReviewStatus.GM_APPROVAL,
      reviewee: {firstName: 'Jason', lastName: 'White'},
      reviewer: {firstName: 'Joe', lastName: 'Rose'}
    }];
  }
});