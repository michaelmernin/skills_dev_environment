'use strict';

angular.module('etmApp').factory('Todo', function ($resource, DateUtils, ReviewStatus, FeedbackStatus) {
  function convertFromServer(data) {
    data.dueDate = DateUtils.convertLocaleDateFromServer(data.dueDate);
    return data;
  }

  function convertToServer(data) {
    if (data.dueDate) {
      data.dueDate = DateUtils.convertLocaleDateToServer(data.dueDate);
    }
    return data;
  }
  
  function isOpenToSubmit(review, reviewerFeedback) {
    return review.reviewStatus.id === ReviewStatus.OPEN.id;
  }

  return angular.extend($resource('api/todos/:id', {}, {
    'query': {
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data.forEach(convertFromServer);
        return data;
      }
    },
    'update': {
      method:'PUT',
      transformRequest: function (data) {
        return angular.toJson(convertToServer({
          todoId: data.todoId,
          result: data.result
        }));
      }
    },
    'queryTodoList': {
      url: 'api/todoList',
      method: 'GET',
      isArray: true,
      transformResponse: function (data) {
        data = angular.fromJson(data);
        data.forEach(convertFromServer);
        return data;
      }
    }
  }), {
    getActions: function (todo, review, reviewerFeedback) {
      var actions = [];
      if (todo.id && review.reviewStatus && review.reviewStatus.id) {
        if (isOpenToSubmit(review, reviewerFeedback)) {
          actions.push({
            confirm: 'Confirm Feedback Submission?',
            todoId: todo.id,
            result: 'SUBMIT',
            theme: '',
            icon: 'fa-send'
          });
          if (todo.name === "Reviewee Approval") {
            actions[0].name = "Submit";
          } else {
            actions[0].name = "Submit Feedback";
          }
        } else {
          actions.push({
            name: 'Approve',
            confirm: 'Confirm Review Approval?',
            todoId: todo.id,
            result: 'APPROVE',
            theme: '',
            icon: 'fa-check'
          }, {
            name: 'Reject',
            confirm: 'Confirm Review Rejection?',
            todoId: todo.id,
            result: 'REJECT',
            theme: 'md-warn',
            icon: 'fa-exclamation'
          });
        }
      }
      return actions;
    }
  });
});
