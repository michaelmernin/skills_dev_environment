/* globals moment */
'use strict';

angular.module('etmApp').controller('ReviewNewController', function ($scope, $state, $mdDialog, $translate, $timeout, Review, ReviewType, Principal, User, Project) {
  $scope.review = new Review();
  $scope.reviewType = "";
  $scope.reviewees = [];
  $scope.currentUser = User.profile();
  $scope.projects = [];
  $scope.years = [];
  $scope.isAnnual = null;
  $scope.review.reviewee = $scope.currentUser;
  $scope.quarters = [];

  $scope.load = function () {
    ReviewType.getAllExceptAR(function (result) {
      $scope.review.reviewType = result[0];
      $scope.reviewType = $scope.review.reviewType.name;
    });
  };
  $scope.load();

  var translateKeys = ['title', 'label', 'content', 'ok', 'cancel'],
  key = "";
  //translateKeys = translateKeys.map(function (key) {return 'review.new.save.dialog.' + key;});

  $timeout(function() {
    $scope.quaters = _getQuarters("next");
  },1000);
  
  /**
  *      PRIVATE HELPERS
  */

  var _populateShowReviewees = function () {
    if ($scope.review.reviewType.processName === 'annualReview') {
      Principal.identity().then(function (account) {
        $scope.reviewees.push(account);
        if (Principal.isInRole('ROLE_COUNSELOR')) {
          User.queryCounselees(function (result) {
            Array.prototype.push.apply($scope.reviewees, result);
          });
        }
      });
    } else if ($scope.review.reviewType.processName === 'engagementReview' && $scope.review.project !== undefined) {
      var project = $scope.review.project;
      if ($scope.currentUser.id === project.manager.id) {
        $scope.reviewees = project.projectMembers.concat(project.manager);
      } else {
        Array.prototype.push.apply($scope.reviewees, [$scope.currentUser]);
      }
    }
  };

  var _populateProjects = function () {
    if ($scope.review.reviewType.processName === 'engagementReview') {
      Project.getAllByUser({id: $scope.currentUser.id}, function(projects) {
        Array.prototype.push.apply($scope.projects, projects);
      });
    }
  };

  var _clearReviewees = function () {
    $scope.reviewees.length = 0;
  };

  var _clearDropdowns = function () {
    $scope.projects.length = 0;
    delete $scope.review.project;
    _clearReviewees();
  };

  var _populateYears = function () {
    $scope.years.length = 0;
    var startYear = moment($scope.review.project.startDate).year();
    var endYear = moment($scope.review.project.endDate).year();
    if (startYear === endYear) {
      Array.prototype.push.apply($scope.years, [startYear]);
    } else {
      Array.prototype.push.apply($scope.years, [startYear, endYear]);
    }
  };

  var _displayConfirmDialog = function (translateKeys, key, ev) {
    $translate(translateKeys).then(function (translations) {
      var confirmSave = $mdDialog.confirm()
        .title(translations['review.new.save.' + key + '.title'])
        .ariaLabel(translations['review.new.save.' + key + '.label'])
        .content(translations['review.new.save.' + key + '.content'])
        .ok(translations['review.new.save.' + key + '.ok'])
        .cancel(translations['review.new.save.' + key + '.cancel'])
        .targetEvent(ev);
      $mdDialog.show(confirmSave).then(function () {
        $scope.review.startDate = new Date($scope.review.startDate);
        $scope.review.endDate = new Date($scope.review.endDate);
        $scope.review.$save(function (review) {
          $state.go('review.edit', {review: review, id: review.id});
        });
      });
    });
  };
  
  var _getQuarters = function (direction) {
    $scope.getQuarters(direction);
  };
  
  var _populateQuarters = function (direction, startDate) {
    if (direction === 'next') {
      return [0, 1, 2, 3].map( function(q) {
        var newDate = moment(startDate).add(q, 'Q').format('MMM-YYYY');
        var quarter = {startDate:newDate, endDate:moment(newDate).add(2, 'M').format('MMM-YYYY')};
        return quarter;
      });
    } else {
      return [3, 2, 1, 0].map( function(q) {
        var newDate = moment(startDate).subtract(q, 'Q').format('MMM-YYYY');
        var quarter = {startDate:newDate, endDate:moment(newDate).add(2, 'M').format('MMM-YYYY')};
        return quarter;
      });
    }
  };
  
  var _filterQuarters = function (revieweeId, reviewTypeId, quarters) {
    var reviews = Review.getReviewsByTypeAndReviewee({revieweeId: $scope.review.reviewee.id, reviewTypeId: 2});
    $timeout(function() {
      var somethingStupid = quarters.map( function(quarter) {
        if (reviews.length > 0) {
          angular.forEach(reviews, function(review) {
            if (review.startDate == moment(quarter.startDate).format('YYYY-MM-DD')) {
              quarter['isReadOnly'] = true;
            }
          });
        } else {
          quarter['isReadOnly'] = false;
        }
        return quarter;
      });
      Array.prototype.push.apply($scope.quarters, somethingStupid);
    },1000);
  }

 /**
  *      PUBLIC SCOPE METHODS
  */
  // triggered by save button, creates a new review
  $scope.save = function (ev) {

    if(!$scope.reviewForm.$valid){ return;}

    // case, creating a review for a reviewee with no counselor
    if ($scope.review.reviewee.counselor === null) {
      var errorDialog = $mdDialog.confirm()
        .title('Cannot create review - Selected reviewee has no counselor')
        .ariaLabel('aria label')
        .content('Please contact IT to assign this reviewee a counselor in order to create this review.')
        .ok('Okay');
      $mdDialog.show(errorDialog);
     }
     // case, annual review
     else if ($scope.review.reviewType.id === 1) {
        Review.getReviewsByTypeAndReviewee({revieweeId: $scope.review.reviewee.id, reviewTypeId: 1}, function(annualReviewList) {
          var date = new Date(),
            startEndDate = new Date($scope.review.reviewee.startDate),
            latestReviewList = $scope.getLatestReviews(annualReviewList, date);
          // reviewer has no reviews for this year or th next
          if (latestReviewList.length === 0) {
            $scope.review.startDate = new Date(startEndDate.setFullYear(date.getFullYear()));
            $scope.review.endDate = new Date(startEndDate.setFullYear(date.getFullYear() + 1));
            translateKeys = translateKeys.map(function (key) {return 'review.new.save.thisYear.' + key;});
            key = 'thisYear';
            _displayConfirmDialog(translateKeys, key, ev);
          }
          // reviewer has review for this year only
          else if (latestReviewList.length === 1) {
            $scope.review.startDate = new Date(startEndDate.setFullYear(date.getFullYear() + 1));
            $scope.review.endDate = new Date(startEndDate.setFullYear(date.getFullYear() + 2));
            translateKeys = translateKeys.map(function (key) {return 'review.new.save.nextYear.' + key;});
            key = 'nextYear';
            _displayConfirmDialog(translateKeys, key, ev);
          }
          // case, reviewee has annual reviews for this year and the next
          else {
            var dialog = $mdDialog.alert()
                .title('Cannot create annual review')
                .ariaLabel('aria label')
                .content('You already have an annual review created for this year and next year')
                .ok('Okay');
            $mdDialog.show(dialog);
          }
        });

      // case, engagement review
      } else {
        translateKeys = translateKeys.map(function (key) {return 'review.new.save.engagement.' + key;});
        _displayConfirmDialog(translateKeys, 'engagement', ev);
      }
  };

  // Triggered on reviewType dropdown change
  $scope.reviewTypeChange = function(){
    _clearDropdowns();
    _populateShowReviewees();
    _populateProjects();
    $scope.isAnnual = $scope.review.reviewType.id === 1;
  };

  // Triggered on project dropdown change
  $scope.projectChange = function(){
    _clearReviewees();
    _populateShowReviewees();
    _populateYears();
  };

  /* TODO: remove unused
  $scope.minEndDate = function () {
    if ($scope.reviewForm.startDate.$valid) {
      var minDate = new Date($scope.review.startDate);
      minDate.setDate(minDate.getDate() + 1);
      return minDate;
    }
    return '';
  };
  */

  $scope.getLatestReviews = function (annualReviewList, currentDate) {
    var list = [];
    for (var i = 0; annualReviewList.length > i; i++) {
      if (new Date(annualReviewList[i].startDate).getFullYear() === currentDate.getFullYear() || new Date(annualReviewList[i].startDate).getFullYear() === currentDate.getFullYear() + 1) {
        list.push(annualReviewList[i]);
      }
    }

    return list;
  };
 
 $scope.getMatches = function (query) {
   return User.autocomplete({query: query, reviewId:""}).$promise
     .then(function(options){
       return options.filter(function (user) {
         return user.id !== $scope.currentUser.id;
       });
     });
 };
 
 $scope.getQuarters = function (direction) {
   if ($scope.currentUser.startDate !== undefined) {
     if ($scope.quarters.length == 0) {
       var startDate = moment($scope.currentUser.startDate).format('MMM-YYYY');
       var quarters = _populateQuarters(direction, startDate);
       _filterQuarters($scope.review.reviewee.id, 2, quarters);
     } else {
       var startDate = direction === "next" ? $scope.quarters[3].startDate : $scope.quarters[0].startDate;
       $scope.quarters.length = 0;
       var quarters = _populateQuarters(direction, startDate);
       _filterQuarters($scope.review.reviewee.id, 2, quarters);
     }
   }
 };
 
 $scope.applyQuarter = function (quarter) {
   $scope.review.startDate = moment(quarter.startDate).format('MM/DD/YYYY');
   $scope.review.endDate = moment(quarter.endDate).format('MM/DD/YYYY');
 };
 
 $scope.setReviewer = function (reviewer) {
   $scope.review.reviewer = reviewer;
 }
});
