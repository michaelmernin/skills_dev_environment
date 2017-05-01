'use strict';

angular.module('etmApp').controller('ReviewNewController', function ($scope, $state, $mdDialog, $translate, Review, ReviewType, Principal, User, Project) {
  $scope.review = new Review();
  $scope.reviewTypes = [];
  $scope.reviewees = [];
  $scope.currentUser = User.profile();
  $scope.projects = [];

  $scope.load = function () {
    ReviewType.query(function (result) {
      $scope.reviewTypes = result;
    });
  };
  $scope.load();

  var translateKeys = ['title', 'label', 'content', 'ok', 'cancel'],
  key = "";
  //translateKeys = translateKeys.map(function (key) {return 'review.new.save.dialog.' + key;});

  $scope.save = function (ev) {
    if ($scope.reviewForm.$valid) {
      if ($scope.review.reviewType.id == 1) {
        Review.getReviewsByTypeAndReviewee({revieweeId: $scope.review.reviewee.id, reviewTypeId: 1}, function(annualReviewList) {
          var date = new Date(),
            startEndDate = new Date($scope.review.reviewee.startDate),
            latestReviewList = $scope.getLatestReviews(annualReviewList, date);
          if (latestReviewList.length === 0) {
            $scope.review.startDate = new Date(startEndDate.setFullYear(date.getFullYear()));
            $scope.review.endDate = new Date(startEndDate.setFullYear(date.getFullYear() + 1));
            translateKeys = translateKeys.map(function (key) {return 'review.new.save.thisYear.' + key;});
            key = "thisYear";
            $scope.displayConfirmDialog(translateKeys, key, ev);
          } else if (latestReviewList.length === 1) {
            $scope.review.startDate = new Date(startEndDate.setFullYear(date.getFullYear() + 1));
            $scope.review.endDate = new Date(startEndDate.setFullYear(date.getFullYear() + 2));
            translateKeys = translateKeys.map(function (key) {return 'review.new.save.nextYear.' + key;});
            key = "nextYear";
            $scope.displayConfirmDialog(translateKeys, key, ev);
          } else {
            var dialog = $mdDialog.confirm()
                .title('Cannot create annual review')
                .ariaLabel('aria label')
                .content('You already have an annual review created for this year and next year')
                .ok('Okay');
            $mdDialog.show(dialog);
          }
        });
      } else {
        translateKeys = translateKeys.map(function (key) {return 'review.new.save.engagement.' + key;});
        $scope.displayConfirmDialog(translateKeys, "engagement", ev);
      }
    }
  };

  $scope.minEndDate = function () {
    if ($scope.reviewForm.startDate.$valid) {
      var minDate = new Date($scope.review.startDate);
      minDate.setDate(minDate.getDate() + 1);
      return minDate;
    }
    return '';
  };
  
  $scope.toggleDates = function () {
    if (!this.review.reviewType || this.review.reviewType.processName === 'annualReview') {
      return true;
    } else {
      return false;
    }
  }
  
  $scope.displayConfirmDialog = function (translateKeys, key, ev) {
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
  }
  
  $scope.getLatestReviews = function (annualReviewList, currentDate) {
    var list = [];
    for (var i = 0; annualReviewList.length > i; i++) {
      if (new Date(annualReviewList[i].startDate).getFullYear() === currentDate.getFullYear() || new Date(annualReviewList[i].startDate).getFullYear() === currentDate.getFullYear() + 1) {
        list.push(annualReviewList[i]);
      }
    }
    
    return list;
  }
  
  $scope.populateShowReviewees = function () {
    if (this.review.reviewType.processName === 'annualReview') {
      Principal.identity().then(function (account) {
        $scope.reviewees.push(account);
        if (Principal.isInRole('ROLE_COUNSELOR')) {
          User.queryCounselees(function (result) {
            $scope.reviewees = [];
            Array.prototype.push.apply($scope.reviewees, result);
          });
        }
      });
    } else if ($scope.projects.length > 0 && this.review.reviewType.processName === 'engagementReview'){
      var project = $scope.review.project;
      $scope.reviewees = [];
      Array.prototype.push.apply($scope.reviewees, project.projectMembers);
    }
  }
  
  $scope.populateProjects= function () {
    if (this.review.reviewType.processName === 'engagementReview') {
      Project.queryProjects({id: $scope.currentUser.id}, function(projects) {
        $scope.projects = []
        Array.prototype.push.apply($scope.projects, projects);
      })
    }
  }
});
