/* globals moment */
'use strict';

angular.module('etmApp').controller('ReviewNewController', function ($scope, $state, $mdDialog, $translate, Review, ReviewType, Principal, User, Project) {
  $scope.review = new Review();
  $scope.reviewTypes = [];
  $scope.reviewees = [];
  $scope.currentUser = User.profile();
  $scope.projects = [];
  $scope.years = [];
  $scope.isAnnual = null;

  $scope.load = function () {
    ReviewType.query(function (result) {
      $scope.reviewTypes = result;
    });
  };
  $scope.load();

  var translateKeys = ['title', 'label', 'content', 'ok', 'cancel'],
  key = "";
  //translateKeys = translateKeys.map(function (key) {return 'review.new.save.dialog.' + key;});

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
        $scope.review.$save(function (review) {
          $state.go('review.edit', {review: review, id: review.id});
        });
      });
    });
  };

 /**
  *      PUBLIC SCOPE METHODS
  */
  // triggered by save button, creates a new review
  $scope.save = function (ev) {

    if(!$scope.reviewForm.$valid){ return;}

    // case, creating a review for a reviewee with no councelor
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
        var year = $scope.year;
        var quarter = $scope.quarter;
        var date = moment(year, 'YYYY');
        $scope.review.startDate = new Date(date.quarter(quarter).startOf('quarter').format());
        $scope.review.endDate = new Date(date.quarter(quarter).endOf('quarter').format());
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

});
