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
  translateKeys = translateKeys.map(function (key) {return 'review.new.save.dialog.' + key;});

  $scope.save = function (ev) {
    if ($scope.reviewForm.$valid) {
      $translate(translateKeys).then(function (translations) {
        var confirmSave = $mdDialog.confirm()
          .title(translations['review.new.save.dialog.title'])
          .ariaLabel(translations['review.new.save.dialog.label'])
          .content(translations['review.new.save.dialog.content'])
          .ok(translations['review.new.save.dialog.ok'])
          .cancel(translations['review.new.save.dialog.cancel'])
          .targetEvent(ev);
        $mdDialog.show(confirmSave).then(function () {
          $scope.review.$save(function (review) {
            $state.go('review.edit', {review: review, id: review.id});
          });
        });
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
});
