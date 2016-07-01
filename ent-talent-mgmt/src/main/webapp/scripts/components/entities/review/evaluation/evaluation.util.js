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
    return eq(user, review.reviewee.director) && !has([ReviewStatus.OPEN], review.reviewStatus);
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
    
    eq: eq
  }
});