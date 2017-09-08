'use strict';

/**
 * A feedback util functions
 */
angular.module('etmApp').service('FeedbackUtil', function (FeedbackType) {
  var FeedbackUtil = this;

  /**
   * true if revieer fedback, false otherwise
   */
  this.isReviewerFeedback = function isReviewerFeedback(feedback){
    if(!feedback.feedbackType) return false;
    return feedback.feedbackType.id === FeedbackType.REVIEWER.id;
  };

  /**
   * true if revieee fedback, false otherwise
   */
  this.isRevieweeFeedback = function isRevieweeFeedback(feedback){
    if(!feedback.feedbackType) return false;
    return feedback.feedbackType.id === FeedbackType.SELF.id;
  };

  /**
   * get the reviewer feedback from array of feedbacks
   * if feedbacks is object, check if is reviewer feedback, if true return it, if false, return null;
   */
  this.getReviewerFeedback = function getReviewerFeedback(feedbacks){
    // if array, find the reviewer feedback
    if (feedbacks && feedbacks.length) {
      var filtered = feedbacks.filter(FeedbackUtil.isReviewerFeedback);
      return filtered ? filtered[0] : null;
    }
    // if not array, check if reviewer feedback
    else {
      return FeedbackUtil.isReviewerFeedback(feedbacks) ? feedbacks : null;
    }
  };

  /**
   * get the reviewee feedback from array of feedbacks
   * if feedbacks is object, check if is reviewee feedback, if true return it, if false, return null;
   */
  this.getRevieweeFeedback = function getRevieweeFeedback(feedbacks){
    // if array, find the reviewee feedback
    if (feedbacks && feedbacks.length) {
      var filtered = feedbacks.filter(FeedbackUtil.isRevieweeFeedback);
      return filtered ? filtered[0] : null;
    }
    // if not array, check if reviewee feedback
    else {
      return FeedbackUtil.isRevieweeFeedback(feedbacks) ? feedbacks : null;
    }
  };

});
