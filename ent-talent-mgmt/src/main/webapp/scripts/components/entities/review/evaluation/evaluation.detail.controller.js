'use strict';

angular.module('etmApp').controller('EvaluationDetailController', function ($scope, $mdDialog, Evaluation, question, review, user, EvaluationUtil) {
  $scope.question = question;
  $scope.review = review;
  if (question.ratings.reviewer) {
    var reviewerScore = [question.ratings.reviewer.score, question.ratings.reviewer.comment];
  }
  if (question.ratings.peer) {
    var peerScores = getPeerScores(question.ratings.peer);
  }
  if (question.ratings.reviewee) {
    var revieweeScore = [question.ratings.reviewee.score, question.ratings.reviewee.comment];
  }
  $scope.save = function () {
    if ($scope.evalForm.$valid) {
      $mdDialog.hide($scope.question);
    }
  };
  
  $scope.close = function (question) {
 /*   if (EvaluationUtil.isReviewer(review, user)) {
      question.ratings.reviewer.score = reviewerScore[0];
      question.ratings.reviewer.comment = reviewerScore[1];
    } else if (EvaluationUtil.isReviewee(review, user)) {
      question.ratings.reviewee.score = revieweeScore[0];
      question.ratings.reviewee.comment = revieweeScore[1];
    } else if (EvaluationUtil.isPeer(review, user)) {
      $.each(peerScores, function(key, peer) {
        if (peer[0] === user.id) {
          question.ratings.peer[key].score = peer[1];
          question.ratings.peer[key].comment = peer[2];
        }
      });
    }*/
    $mdDialog.hide();
  };

  $scope.showRevieweeRating = function () {
    return Evaluation.showRevieweeRating($scope.review, user);
  };

  $scope.showReviewerRating = function () {
    return Evaluation.showReviewerRating($scope.review, user);
  };

  $scope.showPeerRatings = function () {
    return Evaluation.showPeerRatings($scope.review, user);
  };

  $scope.showPeerRating = function (peerRating) {
    return Evaluation.showPeerRating($scope.review, user, peerRating);
  };
  
  $scope.isReviewer = function () {
    if (user.id == review.reviewer.id) {
      return true;
    } else {
      return false;
    }
  };
  
  function getPeerScores(peers) {
    var peerScores = [];
    $.each(peers, function(key, value) {
      var peer = [value.feedback.author.id, value.score, value.comment];
      peerScores.push(peer);
    });
    return peerScores;
  };
});
