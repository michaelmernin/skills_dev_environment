'use strict';

angular.module('etmApp').controller('DashboardReviewsController', function ($scope, $location, $stateParams, Review, Principal) {
  $scope.reviews = [];
  $scope.isAuthenticated = Principal.isAuthenticated;

  $scope.loadReviews = function () {
    if ($scope.isAuthenticated()) {
      Review.query(function (result) {
        $scope.reviews = result;
      });
    } else {
      $scope.reviews = $scope.dummyReviews;
    }
  };
  $scope.loadReviews();
  $scope.getReviewProgress = function (reviewStatus) {
    var factor = 16.66; //6 steps to complete
    if (reviewStatus != null) {
      switch (reviewStatus.toLowerCase()) {
      case "initiated":
        return 1 * factor;
        break;
      case "counselor review":
        return 2 * factor;
        break;
      case "joint review":
        return 3 * factor;
        break;
      case "gm review":
        return 4 * factor;
        break;
      case "completed":
        return 5 * factor;
        break;
      case "closed":
        return 6 * factor;
        break;
      default:
        return 0;
      };
    } else {
      return 0;
    }

  };
  $scope.hasProjectAndClient = function (reviewName) {
    return (
      reviewName.toUpperCase().trim() !== "Annual Review".toUpperCase().trim() && reviewName.toUpperCase().trim() !== "3 Month Review".toUpperCase().trim()
    )
  };
  $scope.goToReview = function (reviewId) {
    $location.path('review/' + reviewId + '/edit');
  };

  $scope.isSubstring = function (property, query) {
    return (property && query) ? property.toLowerCase().indexOf(query) !== -1 : false
  }
  $scope.reviewSearch = function (review) {
    // if query is undefined or null, show all reviews
    if(!$scope.query){
      return true;
    }
    var query = ($scope.query) ? $scope.query.toLowerCase() : "";
    
    var isSubstring = $scope.isSubstring;
    return (
      isSubstring(review.client, query) || isSubstring(review.project, query) || isSubstring(review.reviewType.name, query) || isSubstring(review.reviewStatus.name, query) || isSubstring(review.reviewee.firstName, query) || isSubstring(review.reviewee.lastName, query) || isSubstring(review.reviewer.firstName, query) || isSubstring(review.reviewer.lastName, query)
    );
  };

  $scope.dummyReviews = [
    {
      "startDate": "2014-06-10",
      "endDate": "2015-04-10",
      "client": "BestBuy",
      "project": "Open Box",
      "reviewType": {
        "name": "Annual Review"
      },
      "reviewStatus": {
        "name": "Initiated"
      },
      "reviewee": {
        "firstName": "Jack",
        "lastName": "Smith"
      },
      "reviewer": {
        "firstName": "David",
        "lastName": "Smith"
      }
  }, {
      "startDate": "2015-03-8",
      "endDate": "2015-06-21",
      "client": "Target",
      "project": "AEM",
      "reviewType": {
        "name": "3 Month Review"
      },
      "reviewStatus": {
        "name": "Joint Review"
      },
      "reviewee": {
        "firstName": "John",
        "lastName": "Doe"
      },
      "reviewer": {
        "firstName": "Sam",
        "lastName": "Jackson"
      }
  }, {
      "startDate": "2015-03-4",
      "endDate": "2015-08-7",
      "client": "Midtronic",
      "project": "iPhone App",
      "reviewType": {
        "name": "Engagement"
      },
      "reviewStatus": {
        "name": "GM Review"
      },
      "reviewee": {
        "firstName": "Jason",
        "lastName": "White"
      },
      "reviewer": {
        "firstName": "Joe",
        "lastName": "Rose"
      }
  }
];
  $scope.orderByItems = [
    {
      key: 'Start date',
      value: 'startDate'
    }, {
      key: 'End date',
      value: 'endDate'
    }, {
      key: 'Client',
      value: 'client'
    }, {
      key: 'Project',
      value: 'project'
    }, {
      key: 'Review type',
      value: 'reviewType.name'
    }, {
      key: 'Review status',
      value: 'reviewStatus.id'
    }, {
      key: 'Reviewee last name',
      value: 'reviewee.lastName'
    }, {
      key: 'Reviewee first name',
      value: 'reviewee.firstName'
    }, {
      key: 'Reviewer last name',
      value: 'reviewer.lastName'
    }, {
      key: 'Reviewer first name',
      value: 'reviewer.firstName'
    }
  ];

});