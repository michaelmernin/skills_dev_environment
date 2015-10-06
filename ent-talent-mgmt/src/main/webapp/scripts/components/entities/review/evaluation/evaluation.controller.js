'use strict';

angular.module('etmApp').controller('EvaluationController', function ($scope, $mdDialog, $mdMedia, $window, Principal, Review, Feedback, Rating, Evaluation) {
  var questions = [];
  var questionsIndex = {};
  var review = {};
  var user = {};

  $scope.categories = {};

  Principal.identity().then(function (account) {
    user = account;
    $scope.$parent.$watch('review', function (parentReview) {
      if (parentReview.id) {
        review = parentReview;
        Feedback.query({reviewId: review.id}, function (feedback) {
          review.feedback = feedback;
          if (questions.length) {
            feedback = getFeedback(review);
            angular.forEach(feedback, function (userFeedback) {
              setRatings(userFeedback);
            });
          } else {
            $scope.categories = {};
          }
        });
        questions = getQuestions(review);
      }
    });
  });

  $scope.viewEvaluation = function (question, ev) {
    $mdDialog.show({
      controller: 'EvaluationDetailController',
      templateUrl: 'scripts/components/entities/review/evaluation/evaluation.detail.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      locals: {
        question: question,
        review: review,
        user: user
      }
    }).then(function (question) {
      updateDirtyRating(question.ratings.reviewee);
      updateDirtyRating(question.ratings.reviewer);
      angular.forEach(question.ratings.peer, updateDirtyRating);
    });
  };

  $scope.getScore = function (rating) {
    return Evaluation.score(rating);
  };

  $scope.getAvgScore = function (ratings) {
    return Evaluation.avgScore(ratings);
  };

  $scope.titleLimit = function () {
    var width = Math.max(320, $window.innerWidth);
    var padding = 100;
    if ($mdMedia('gt-lg')) {
      width = 0.8 * width;
      padding += 240;
    }
    if ($mdMedia('lg')) {
      width = 0.9 * width;
      padding += 300;
    }
    return Math.floor((width - padding) * 0.136);
  };

  function getQuestions(review) {
    return review && review.reviewType && review.reviewType.questions && review.reviewType.questions.length ? review.reviewType.questions : [];
  }

  function getFeedback(review) {
    if (!review.feedback) {
      review.feedback = [];
    }

    if (!userHasFeedback(review.feedback)) {
      review.feedback.push(createNewFeedback(review));
    }

    return review.feedback;
  }

  function userHasFeedback(feedback) {
    var found = false;
    for (var i = 0; i < feedback.length; ++i) {
      if (feedback[i].author.id === user.id) {
        found = true;
        break;
      }
    }
    return found;
  }

  function createNewFeedback(review) {
    var feedbackType = Evaluation.userFeedbackType(review, user);
    var userFeedback = {};
    userFeedback.author = {id: user.id, firstName: user.firstName, lastName: user.lastName};
    userFeedback.feedbackType = feedbackType;
    userFeedback.ratings = [];
    angular.forEach(questions, function (question) {
      indexQuestion(question);
      var userRating = {};
      userRating.question = {id: question.id};
      userFeedback.ratings.push(userRating);
    });
    var feedbackClone = {};
    angular.copy(userFeedback, feedbackClone);
    feedbackClone.review = {id: review.id};
    Feedback.save({reviewId: review.id}, feedbackClone, function (feedbackSaved) {
      userFeedback.id = feedbackSaved.id;
      angular.forEach(userFeedback.ratings, function (rating) {
        angular.forEach(feedbackSaved.ratings, function (ratingSaved) {
          if (rating.question.id === ratingSaved.question.id) {
            rating.id = ratingSaved.id;
          }
        });
      });
    });
    return userFeedback;
  }

  function setRatings(userFeedback) {
    var type = userFeedback.feedbackType.id;
    var canEdit = userFeedback.author.id === user.id;

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
    var id = id || question.id;
    if (!questionsIndex[id]) {
      questionsIndex[id] = question;
      var category = question.category.title;
      if (!$scope.categories[category]) {
        $scope.categories[category] = [question];
      } else {
        $scope.categories[category].push(question);
      }
    }
  }

  function updateDirtyRating(rating) {
    if (rating && rating.$dirty) {
      Rating.update({
        id: rating.id,
        score: rating.score,
        comment: rating.comment,
        visible: rating.visible,
        question: {id: rating.question.id}
      });
    }
  }

});
