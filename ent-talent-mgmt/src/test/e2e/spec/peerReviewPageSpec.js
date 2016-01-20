'use strict';

var LoginPage = require('../page/loginPage.js');
var userData = require('../data/userData.js');
var AnnualReviewPage = require('../page/annualReviewPage.js');
var PeerReviewPage = require('../page/peerReviewPage.js');

describe('Enterprise Talent Management', function () {
  describe('Peer Review Tab', function () {
    var annualReviewPage;
    var peerReviewPage;

    beforeAll(function () {
      var loginPage = new LoginPage();
      loginPage.get();
      loginPage.login(userData.users.counselor);
      annualReviewPage = new AnnualReviewPage();
      annualReviewPage.get(' Annual Review for Dev UserFour by Dev UserThree ');
      peerReviewPage = new PeerReviewPage();
      peerReviewPage.get();
    });


    it('should take input value', function () {
      peerReviewPage.search('value');

    });

    it('shoud give error response if search term does not match available peer', function(){
      var availablePeers = peerReviewPage.getPeerOptions('test');
      expect(availablePeers.count()).toBe(1);
      expect(availablePeers.get(0).getText()).toBe('No peers matching "test" were found.');

    });
    
    it('shoud give valid response if search term match available peer', function(){
      var availablePeers = peerReviewPage.getPeerOptions('dev');
      expect(availablePeers.count()).toBe(2);
      expect(availablePeers.get(0).getText()).toBe('Dev UserSeven');
      expect(availablePeers.get(1).getText()).toBe('Dev UserEight');
    });
    
    it('shoud allow users to select a peer', function(){
    
     // peerReviewPage.selectPeer('Dev UserEight');
     
      var selectedPeer = peerReviewPage.getSelectedPeer();
      expect(selectedPeer.element(by.css('.peer-name')).getText()).toBe('Dev UserEight');
      
    });
    
    it('should allow users to delete a peer', function(){
      
      peerReviewPage.deletePeer('Dev UserEight');
     
      expect(element(by.tagName('md-list-item')).element(by.cssContainingText('.peer-name', 'Dev UserEight')).isPresent()).toBe(false);
    });
  });
});


