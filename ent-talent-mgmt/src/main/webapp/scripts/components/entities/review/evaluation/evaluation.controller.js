'use strict';

angular.module('etmApp').controller('EvaluationController', function ($scope, $mdDialog, $mdMedia, $window, Principal, Feedback, Rating) {
  var questions = [];
  var questionsIndex = {};
  var review = {};

  $scope.categories = {};

  Principal.identity().then(function (account) {
    $scope.$parent.$watch('review', function (parentReview) {
      review = parentReview;
      questions = getQuestions(review);
      if (questions.length) {
        var feedback = getFeedback(review, account);
        angular.forEach(feedback, function (userFeedback) {
          setRatings(userFeedback, account);
        });
      } else {
        $scope.categories = {};
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
        review: review
      }
    });
  };

  $scope.getScore = function (rating) {
    if (rating.score === -1) {
      return 'N/A';
    }
    return rating.score;
  };

  $scope.getAvgScore = function (ratings) {
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

  function getFeedback(review, account) {
    if (!review.feedback) {
      review.feedback = [];
    }

    if (!accountHasFeedback(review.feedback, account)) {
      review.feedback.push(createNewFeedback(review, account));
    }

    return review.feedback;
  }

  function accountHasFeedback(feedback, account) {
    var found = false;
    for (var i = 0; i < feedback.length; ++i) {
      if (feedback[i].author.id === account.id) {
        found = true;
        break;
      }
    }
    return found;
  }

  function createNewFeedback(review, account) {
    var feedbackType = getFeedbackType(review, account);
    var userFeedback = new Feedback();
    userFeedback.author = {id: account.id, firstName: account.firstName, lastName: account.lastName};
    userFeedback.feedbackType = feedbackType;
    userFeedback.ratings = [];
    angular.forEach(questions, function (question) {
      indexQuestion(question);
      var userRating = new Rating();
      userRating.question = {id: question.id};
      userFeedback.ratings.push(userRating);
    });
    return userFeedback;
  }

  function getFeedbackType(review, account) {
    if (review.reviewee.id === account.id) {
      return {id: 1};
    }
    if (review.reviewer.id === account.id) {
      return {id: 2};
    }
    return {id: 3};
  }

  function setRatings(userFeedback, account) {
    var type = userFeedback.feedbackType.id;
    var canEdit = userFeedback.author.id === account.id;

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

});
