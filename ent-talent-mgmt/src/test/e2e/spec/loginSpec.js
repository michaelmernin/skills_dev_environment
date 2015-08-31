'use strict';

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');

describe('Enterprise Talent Management', function () {
  describe('Login Page', function () {
    var loginPage;

    beforeAll(function () {
      loginPage = new LoginPage();
      loginPage.get();
    });

    it('should have a title', function () {
      expect(browser.getTitle()).toEqual('Log In');
    });

    it('should allow username and password input', function () {
      loginPage.userName = 'some.user';
      loginPage.password = 'their.password';
      expect(loginPage.userName).toEqual('some.user');
      expect(loginPage.password).toEqual('their.password');
    });

    it('should display error with incorrect password', function () {
      loginPage.login('some.user', 'not.their.password');
      expect(loginPage.ui.authError.isPresent()).toBe(true);
    });

    it('should successfuly authenticate', function () {
      loginPage.login(userData.users.admin);
      expect(loginPage.ui.authError.isPresent()).toBe(false);
      expect(browser.getTitle()).not.toEqual('Log In');
    });
  });
});
