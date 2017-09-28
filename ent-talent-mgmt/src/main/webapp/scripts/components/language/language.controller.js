'use strict';

angular.module('etmApp').controller('LanguageController', function ($scope, $translate, Language) {
  $scope.changeLanguage = function (languageKey) {
    // TODO: once we decide to move forward with translation to other languages, enable this.
    //$translate.use(languageKey);
    $translate.use('en'); // force use en
  };

  Language.getAll().then(function (languages) {
    $scope.languages = languages;
  });
});
