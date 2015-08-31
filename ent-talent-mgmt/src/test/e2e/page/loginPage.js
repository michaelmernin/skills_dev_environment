'use strict';

require('../locators.js');

var LoginPage = function () {
  this.ui = {
    userNameInput: element(by.model('username')),
    passwordInput: element(by.model('password')),
    logInButton: element(by.translateKey('login.form.button')),
    authError: element(by.translateKey('login.messages.error.authentication'))
  };

  Object.defineProperties(this, {
    userName: {
      get: function () {
        return this.ui.userNameInput.getAttribute('value');
      },
      set: function (userName) {
        this.ui.userNameInput.clear();
        this.ui.userNameInput.sendKeys(userName);
      }
    },
    password: {
      get: function () {
        return this.ui.passwordInput.getAttribute('value');
      },
      set: function (password) {
        this.ui.passwordInput.clear();
        this.ui.passwordInput.sendKeys(password);
      }
    }
  });

  this.get = function () {
    browser.get('/#/login');
  };

  this.submit = function () {
    this.ui.logInButton.click();
  };

  this.login = function (user, password) {
    if (typeof user === 'string' && typeof password === 'string') {
      this.userName = user;
      this.password = password;
    } else if (user.userName && user.password) {
      this.userName = user.userName;
      this.password = user.password;
    }
    this.submit();
  };
};

module.exports = LoginPage;
