'use strict';

angular.module('etmApp').controller('ReviewNewController', function ($scope, $state, $mdDialog, $translate, Review, ReviewType, Principal, User) {
  $scope.review = new Review();
  $scope.reviewTypes = [];
  $scope.reviewees = [];

  Principal.identity().then(function (account) {
    $scope.reviewees.push(account);
    if (Principal.isInRole('ROLE_COUNSELOR')) {
      User.queryCounselees(function (result) {
        Array.prototype.push.apply($scope.reviewees, result);
      });
    }
  });

  $scope.load = function () {
    ReviewType.query(function (result) {
      $scope.reviewTypes = result;
    });
  };
  $scope.load();

  var translateKeys = ['title', 'label', 'content', 'ok', 'cancel'];
  //translateKeys = translateKeys.map(function (key) {return 'review.new.save.dialog.' + key;});

  $scope.save = function (ev) {
    if ($scope.reviewForm.$valid) {
      $scope.review = Review.getLatestReview({id: $scope.review.reviewee.id}, function(review) {
        if (review.startDate) {
          var d = new Date();
          var key;
          if (review.startDate.getFullYear().toString() === d.getFullYear().toString()) {
            translateKeys = translateKeys.map(function (key) {return 'review.new.save.thisYear.' + key;});
            key = "thisYear";
          } else {
            translateKeys = translateKeys.map(function (key) {return 'review.new.save.nextYear.' + key;});
            key = "nextYear";
          }
          $translate(translateKeys).then(function (translations) {
            var confirmSave = $mdDialog.confirm()
              .title(translations['review.new.save.' + key + '.title'])
              .ariaLabel(translations['review.new.save.' + key + '.label'])
              .content(translations['review.new.save.' + key + '.content'])
              .ok(translations['review.new.save.' + key + '.ok'])
              .cancel(translations['review.new.save.' + key + '.cancel'])
              .targetEvent(ev);
            $mdDialog.show(confirmSave).then(function () {
              $scope.review.reviewee = $scope.reviewees[0];
              $scope.review.$save(function (review) {
                $state.go('review.edit', {review: review, id: review.id});
              });
            });
          });
        } else {
          var dialog = $mdDialog.confirm()
            .title('Cannot create annual review')
            .ariaLabel('aria label')
            .content('You already have an annual review created for this year and next year')
            .ok('Okay');
          $mdDialog.show(dialog);
        }
      });
    }
  };

  $scope.minEndDate = function () {
    if ($scope.reviewForm.startDate.$valid) {
      var minDate = new Date($scope.review.startDate);
      minDate.setMonth(minDate.getMonth() + 12);
      return minDate;
    }
    return '';
  };
  
  $scope.checkReviewType = function () {
    if (this.review.reviewType.processName === 'annualReview') {
      $('.date-div').hide();
    } else {
      $('.date-div').show();
    }
  }
});
