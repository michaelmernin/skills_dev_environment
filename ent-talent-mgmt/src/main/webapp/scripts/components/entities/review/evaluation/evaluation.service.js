'use strict';

angular.module('etmApp').factory('Evaluation', function (ReviewStatus, FeedbackType, FeedbackStatus) {
  function eq(e1, e2) {
    if (e1 === undefined || e2 === undefined) {
      return e1 === e2;
    }
    return e1.id === e2.id;
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

  function showAlways(review, user) {
    return eq(user, review.reviewee.counselor)
        || eq(user, review.reviewee.generalManager);
  }

  function showByReviewStatus(review) {
    return has([ReviewStatus.JOINT, ReviewStatus.GM, ReviewStatus.COMPLETED], review.reviewStatus);
  }

  function userFeedbackType(review, user) {
    if (eq(user, review.reviewee)) {
      return FeedbackType.SELF;
    }
    if (eq(user, review.reviewer)) {
      return FeedbackType.REVIEWER;
    }
    return FeedbackType.PEER;
  }

  return {
    userFeedbackType: userFeedbackType,
    score: function (rating) {
      if (rating.score === -1) {
        return 'N/A';
      }
      return rating.score;
    },
    avgScore: function (ratings) {
      var allNull = true;
      var sum = 0;
      var count = 0;

      angular.forEach(ratings, function (rating) {
        if (rating.score) {
          allNull = false;
          if (rating.score !== -1) {
            ++count;
            sum += rating.score;
          }
        }
      });

      if (allNull) {
        return null;
      }
      if (!count) {
        return 'N/A';
      }
      return sum / count;
    },
    showRevieweeRating: function (review, user) {
      if (showAlways(review, user)) {
        return true;
      }
      if (eq(user, review.reviewee)) {
        return true;
      }
      return false;
    },
    showReviewerRating: function (review, user) {
      if (showAlways(review, user)) {
        return true;
      }
      if (eq(user, review.reviewer)) {
        return true;
      }
      if (showByReviewStatus(review)) {
        return true;
      }
      return false;
    },
    showPeerRatings: function (review, user) {
      if (showAlways(review, user)) {
        return true;
      }
      if (eq(user, review.reviewer)) {
        return true;
      }
      if (eq(FeedbackType.PEER, userFeedbackType(review, user))) {
        return true;
      }
      if (showByReviewStatus(review)) {
        return true;
      }
      return false;
    },
    showPeerRating: function (review, user, peerRating) {
      if (showAlways(review, user)) {
        return true;
      }
      if (eq(user, review.reviewer)) {
        return true;
      }
      if (eq(user, peerRating.feedback.author)) {
        return true;
      }
      if (peerRating.visible) {
        return true;
      }
      return false;
    }
  };
});
