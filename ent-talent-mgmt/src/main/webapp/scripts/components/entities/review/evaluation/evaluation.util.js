'use strict';

angular.module('etmApp').factory('EvaluationUtil', function (ReviewStatus) {

  function eq(e1, e2) {
    if ((e1 === undefined || e1 === null) || (e2 === undefined || e2 === null)) {
      return e1 === e2;
    }
    return e1.id === e2.id;
  }

  function isCounselor(review, user) {
    return eq(user, review.reviewee.counselor);
  }

  function isGM(review, user) {
    return eq(user, review.reviewee.generalManager);
  }

  function isDirector(review, user) {
    return eq(user, review.reviewee.director);
  }
  
  /**
   * Check if catigories has at least one empty question (no rating)
   * @param {*} categories 
   */
  function hasEmptyQuestions(categories) {
    if(!categories) return true;
    var hasOneEmptyQuestion = false;
    var keepLooking = true;
    angular.forEach(categories, function(questions){
      if(keepLooking){
        hasOneEmptyQuestion = hasEmptyQuestion(questions)
      }
    });
    return hasOneEmptyQuestion;
  }

  /**
   * check if question array has atleast one empty question
   * @param {*} questionsArr 
   */
  function hasEmptyQuestion(questionsArr){
    var hasOneEmptyQuestion = false;
    var keepLooking = true;
    angular.forEach(questionsArr, function(q){
      if(keepLooking && isEmptyQuestion(q)){
        hasOneEmptyQuestion = true;
        keepLooking = false;
      }
    });
    return hasOneEmptyQuestion;
  }

  /**
   * check if question is empty
   * @param {*} question 
   */
  function isEmptyQuestion(question){
    return question.editableRating.score == null;
  }

  return {
    showAlways: function (review, user) {
      return review && review.reviewee && (isCounselor(review, user) || isGM(review, user) || isDirector(review, user));
    },

    isCounselor: isCounselor,

    isReviewee: function (review, user) {
      return eq(user, review.reviewee);
    },

    isReviewer: function (review, user) {
      return eq(user, review.reviewer);
    },

    isGM: isGM,

    isDirector: isDirector,

    isPeer: function (review, user) {
      var result = false;
      review.peers.forEach(function(peer) {
        if(peer.id === user.id){
          result = true;
        }
      });
      return result;
    },

    eq: eq,

    hasEmptyQuestions: hasEmptyQuestions,
    hasEmptyQuestion: hasEmptyQuestion,
    isEmptyQuestion: isEmptyQuestion
  }
});
