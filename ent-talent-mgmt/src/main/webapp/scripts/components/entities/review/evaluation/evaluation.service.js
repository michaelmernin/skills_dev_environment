'use strict';

angular.module('etmApp').factory('Evaluation', function (ReviewStatus, FeedbackType, FeedbackStatus, Feedback, EvaluationUtil, Question) {
  var categories;
  var questions = [];
  var questionsIndex = {};

  function eq(e1, e2) {
    if ((e1 === undefined || e1 === null) || (e2 === undefined || e2 === null)) {
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

  function showByReviewStatus(review) {
    return has([ReviewStatus.JOINT_APPROVAL, ReviewStatus.GM_APPROVAL, ReviewStatus.COMPLETE], review.reviewStatus);
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

  function getQuestions(review, user) {
    var feedbackType = userFeedbackType(review, user);
    return Question.get({reviewTypeId:review.reviewType.id, feedbackTypeId:feedbackType.id});
  }

  function setRatings(userFeedback, user) {
    var type = userFeedback.feedbackType.id;
    var canEdit = userFeedback.author.id === user.id && userFeedback.feedbackStatus.id === 2;

    angular.forEach(userFeedback.ratings, function (rating) {
      rating.feedback = userFeedback;
      var question = getQuestionById(rating.question.id);

      if (canEdit) {
        rating.canEdit = canEdit;
        question.editableRating = rating;
      }

      switch (type) {
      case 1:
        question.ratings.reviewee = rating;
        break;
      case 2:
        question.ratings.reviewer = rating;
        break;
      case 3:
        question.ratings.peer.push(rating);
        break;
      }
    });
  }

  function getQuestionById(id) {
    var question = questionsIndex[id];
    if (!question) {
      for (var i = 0; i < questions.length; ++i) {
        question = questions[i];
        if (question.id === id) {
          indexQuestion(question, id);
          break;
        }
      }
    }
    question.ratings = question.ratings || {peer: []};
    return question;
  }

  function indexQuestion(question, id) {
    if (!questionsIndex[id]) {
      questionsIndex[id] = question;
      var category = question.category.title;
      if (!categories[category]) {
        categories[category] = [question];
      } else {
        categories[category].push(question);
      }
    }
  }
  
  function getRatings(questions, property) {
    var ratings = [];
    var types = property ? [property] : ['reviewee', 'reviewer', 'peer'];
    angular.forEach(questions, function (question) {
      if (question.ratings) {
        angular.forEach(types, function (type) {
          var rating = question.ratings[type];
          if (angular.isArray(rating)) {
            [].push.apply(ratings, rating);
          } else if (rating) {
            ratings.push(rating);
          }
        });
      }
    });
    return ratings;
  }

  return {
    userFeedbackType: userFeedbackType,
    getRatings: getRatings,
    getQuestions: getQuestions,
    getCategories: function (review, user) {
      if (categories !== undefined) {
        return categories;
      }

      categories = {};
      questions = getQuestions(review, user);

      Feedback.query({reviewId: review.id}, function (feedback) {
        review.feedback = feedback;
        if (questions.length) {
          angular.forEach(review.feedback, function (userFeedback) {
            setRatings(userFeedback, user);
          });
        }
      });

      return categories;
    },
    score: function (rating) {
      if (rating.score === 0) {
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
          if (rating.score !== 0 && rating.visible) {
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
      return Math.round(10 * sum / count) / 10;
    },
    showRevieweeRating: function (review, user) {
      return false;
      if (EvaluationUtil.showAlways(review, user)) {
        return true;
      }
      if (eq(user, review.reviewee)) {
        return true;
      }
      if (showByReviewStatus(review)) {
          return true;
      }
      return false;
    },
    showReviewerRating: function (review, user) {
      return true;
      if (EvaluationUtil.showAlways(review, user)) {
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
      return false;
      if (EvaluationUtil.showAlways(review, user)) {
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
      return false;
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
    },
    reset: function () {
      categories = undefined;
      questions = [];
      questionsIndex = {};
    }
  };
});
