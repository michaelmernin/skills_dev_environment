/* DO NOT REMOVE: Protractor globals to be ignored by JsLint */
/* globals exports: false, require: false, jasmine:false, window:false, Promise:false, setTimeout: false, browser:false, protractor: false*/

var HtmlScreenshotReporter = require('protractor-jasmine2-screenshot-reporter');

var htmlReporter = new HtmlScreenshotReporter({
  dest: './build/protractor-screenshots',
  filename: 'protractor-test-report.html',
  reportTitle: "ETM Protractor Test Report",
  reportOnlyFailedSpecs: false,
  captureOnlyFailedSpecs: true,
  cleanDestination: true,
  showSummary: true,
  showQuickLinks: true,
});

// A reference configuration file.
exports.config = {
  // ----- How to setup Selenium -----
  //
  // There are three ways to specify how to use Selenium. Specify one of the
  // following:
  //
  // 1. seleniumServerJar - to start Selenium Standalone locally.
  // 2. seleniumAddress - to connect to a Selenium server which is already
  //    running.
  // 3. sauceUser/sauceKey - to use remote Selenium servers via SauceLabs.

  // The location of the selenium standalone server .jar file.
  seleniumServerJar: null,
  // The port to start the selenium server on, or null if the server should
  // find its own unused port.
  seleniumPort: null,
  // Chromedriver location is used to help the selenium standalone server
  // find chromedriver. This will be passed to the selenium jar as
  // the system property webdriver.chrome.driver. If null, selenium will
  // attempt to find chromedriver using PATH.
  chromeDriver: null,
  // Additional command line options to pass to selenium. For example,
  // if you need to change the browser timeout, use
  // seleniumArgs: ['-browserTimeout=60'],
  seleniumArgs: [],

  // If sauceUser and sauceKey are specified, seleniumServerJar will be ignored.
  // The tests will be run remotely using SauceLabs.
  sauceUser: null,
  sauceKey: null,
  allScriptsTimeout:20000,

  // The address of a running selenium server. If specified, Protractor will
  // connect to an already running instance of selenium. This usually looks like
  seleniumAddress: null,

  // ----- What tests to run -----
  //
  // Spec patterns are relative to the location of this config. For e.g.  'spec/peerReviewPageSpec.js',
  specs: [
    'spec/**/*.js',
  ],

  // ----- Capabilities to be passed to the webdriver instance ----
  //
  // For a full list of available capabilities, see
  // https://code.google.com/p/selenium/wiki/DesiredCapabilities
  // and
  // https://code.google.com/p/selenium/source/browse/javascript/webdriver/capabilities.js
  capabilities: {
    'browserName': 'chrome',
    'chromeOptions': {
      'args': ['no-sandbox','no-default-browser-check','no-first-run','disable-default-apps']
    }
  },

  directConnect: false,

  // A base URL for your application under test. Calls to protractor.get()
  // with relative paths will be prepended with this. For e.g.  baseUrl: 'https://stlerappdev.perficient.com:8443/',
  baseUrl: 'http://localhost:8080',

  // Selector for the element housing the angular app - this defaults to
  // body, but is necessary if ng-app is on a descendant of <body>
  rootElement: 'body',

  // A callback function called once protractor is ready and available, and
  // before the specs are executed
  // You can specify a file containing code to run by setting onPrepare to
  // the filename string.



  // Setup the report before any tests start
  beforeLaunch: function() {
    'use strict';
     return new Promise(function(resolve){
    	 htmlReporter.beforeLaunch(resolve);
     });
  },

  onPrepare: function () {
    'use strict';
    // At this point, global 'protractor' object will be set up, and jasmine
    // will be available. For example, you can add a Jasmine reporter with:
    var jasmineReporters = require('jasmine-reporters');

    var jUnitReporter = new jasmineReporters.JUnitXmlReporter({
      savePath: 'build/e2e-test-reports',
      filePrefix: 'TEST-protractor-results',
      consolidateAll: true
    });

    jasmine.getEnv().addReporter(jUnitReporter);
    jasmine.getEnv().addReporter(htmlReporter);

    // add jasmine spec reporter
    var SpecReporter = require('jasmine-spec-reporter');
    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: 'all',
      displayPendingSpec: true,    // display each pending spec
      displaySpecDuration: true,   // display each spec duration
      displaySuiteNumber: true,    // display each suite number (hierarchical)
    }));

    setTimeout(function() {
      browser.driver.executeScript(function() {
          return {
              width: window.screen.availWidth,
              height: window.screen.availHeight
          };
      }).then(function(result) {
          browser.driver.manage().window().setSize(result.width, result.height);
      });
    });

    // Add custom locators
    var Locators = require('./locators.js'),
    locators = new Locators();
    locators.addLocators(protractor);
    console.log('jasmine-version:' + jasmine.version);
  },


  framework: 'jasmine2',
  // ----- Options to be passed to minijasminenode -----
  jasmineNodeOpts: {
    // onComplete will be called just before the driver quits.
    onComplete: null,
    // If true, display spec names.
    isVerbose: false,
    // If true, print colors to the terminal.
    showColors: true,
    // If true, include stack traces in failures.
    includeStackTrace: true,
    // Default time to wait in ms before a test fails.
    defaultTimeoutInterval: 30000,
    // remove default print function
    print: function(){}
  }
};
