'use strict';

angular.module('etmApp', [
  'LocalStorageModule', 'tmh.dynamicLocale', 'ngResource', 'ui.router', 'ngCookies', 'pascalprecht.translate', 'ngCacheBuster', 'ngMaterial', 'ngMessages', 'ngSanitize'
]).run(function ($rootScope, $location, $window, $http, $state, $translate, Auth, Principal, Language, ENV, VERSION) {
  $rootScope.ENV = ENV;
  $rootScope.VERSION = VERSION;

  $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {
    $rootScope.toState = toState;
    $rootScope.toStateParams = toStateParams;

    if (Principal.isIdentityResolved()) {
      Auth.authorize();
    }

    // Update the language
    Language.getCurrent().then(function (language) {
      $translate.use(language);
    });
  });

  $rootScope.$on('$stateChangeSuccess',  function (event, toState, toParams, fromState, fromParams) {
    var titleKey = 'global.title';

    $rootScope.previousStateName = fromState.name;
    $rootScope.previousStateParams = fromParams;

    // Set the page title key to the one configured in state or use default one
    if (toState.data.pageTitle) {
      titleKey = toState.data.pageTitle;
    }
    $translate(titleKey).then(function (title) {
      // Change window title with translated one
      $window.document.title = title;
    });
  });

  $rootScope.back = function () {
    // If previous state is 'activate' or do not exist go to 'home'
    if ($rootScope.previousStateName === 'activate' || $state.get($rootScope.previousStateName) === null) {
      $state.go('home');
    } else {
      $state.go($rootScope.previousStateName, $rootScope.previousStateParams);
    }
  };
}).config(["$mdThemingProvider", function ($mdThemingProvider) {
    $mdThemingProvider.definePalette('prft-red', {
      '50': 'fae2e2',
      '100': 'efb9ba',
      '200': 'e68e90',
      '300': 'db6161',
      '400': 'd33f41',
      '500': 'cc1f20',
      '600': 'b91b1b',
      '700': 'a51819',
      '800': '911514',
      '900': '6a0f0e',
      'A100': 'efb9ba',
      'A200': 'e68e90',
      'A400': 'd33f41',
      'A700': 'a51819',
      'contrastDefaultColor': 'light',
      'contrastDarkColors': ['50', '100', '200', '300', '400', 'A100'],
      'contrastLightColors': undefined
    });
    $mdThemingProvider.theme('default')
      .primaryPalette('prft-red')
      .accentPalette('prft-red',{
        'hue-1':'500'
      });
}]).config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, $translateProvider, tmhDynamicLocaleProvider, httpRequestInterceptorCacheBusterProvider) {

  //enable CSRF
  $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
  $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
  
  //enable global http error handling
  $httpProvider.interceptors.push('HttpErrorInterceptor');

  //Cache everything except rest api requests
  httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api\/(?!authorities).*/, /.*protected.*/], true);

  $urlRouterProvider.otherwise('/');
  $stateProvider.state('site', {
    'abstract': true,
    views: {
      'toolbar@': {
        templateUrl: 'scripts/components/toolbar/toolbar.html',
        controller: 'ToolbarController'
      },
      'sidenav@': {
        templateUrl: 'scripts/components/sidenav/sidenav.html',
        controller: 'SidenavController'
      }
    },
    resolve: {
      authorize: ['Auth',
                  function (Auth) {
                    return Auth.authorize();
                  }
                 ],
      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
        $translatePartialLoader.addPart('global');
        $translatePartialLoader.addPart('language');
        return $translate.refresh();
      }]
    }
  });


  // Initialize angular-translate
  $translateProvider.useLoader('$translatePartialLoader', {
    urlTemplate: 'i18n/{lang}/{part}.json'
  });

  $translateProvider.preferredLanguage('en');
  $translateProvider.useCookieStorage();
  $translateProvider.directivePriority(222); // https://github.com/angular-translate/angular-translate/issues/949
  $translateProvider.useSanitizeValueStrategy('sanitize'); // http://angular-translate.github.io/docs/#/guide/19_security

  tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js');
  tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
}).config(['$compileProvider', 'ENV', function ($compileProvider, ENV) {
  $compileProvider.debugInfoEnabled(ENV === 'dev');
}]);
