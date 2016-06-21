// Generated on 2015-03-27 using generator-jhipster 2.4.0
'use strict';
var fs = require('fs');

// Returns the first occurence of the version number
var parseVersionFromBuildGradle = function () {
  var versionRegex = /^version\s*=\s*[',"]([^',"]*)[',"]/gm; // Match and group the version number
  var buildGradle = fs.readFileSync('build.gradle', "utf8");
  return versionRegex.exec(buildGradle)[1];
};

// usemin custom step
var useminAutoprefixer = {
  name: 'autoprefixer',
  createConfig: require('grunt-usemin/lib/config/cssmin').createConfig // Reuse cssmins createConfig
};

module.exports = function (grunt) {
  require('load-grunt-tasks')(grunt);
  require('time-grunt')(grunt);

  grunt.initConfig({
    yeoman: {
      // configurable paths
      app: require('./bower.json').appPath || 'app',
      dist: 'src/main/webapp/dist'
    },
    watch: {
      bower: {
        files: ['bower.json'],
        tasks: ['wiredep']
      },
      ngconstant: {
        files: ['Gruntfile.js', 'build.gradle'],
        tasks: ['ngconstant:dev']
      },
      styles: {
        files: ['src/main/webapp/assets/styles/**/*.css']
      }
    },
    autoprefixer: {
      // not used since Uglify task does autoprefixer,
      //    options: ['last 1 version'],
      //    dist: {
      //        files: [{
      //            expand: true,
      //            cwd: '.tmp/styles/',
      //            src: '**/*.css',
      //            dest: '.tmp/styles/'
      //        }]
      //    }
    },
    wiredep: {
      app: {
        src: ['src/main/webapp/index.html'],
        exclude: [
          /angular-i18n/,  // localizations are loaded dynamically
          /swagger-ui/,
          'bower_components/highcharts/highcharts-more.js', // (not needed) wiredep includes this with highcharts bower dependency
          'bower_components/highcharts/modules/exporting.js' // (not needed) wiredep includes this with highcharts bower dependency
        ]
      },
      test: {
        src: 'src/test/javascript/karma.conf.js',
        exclude: [/angular-i18n/, /swagger-ui/, /angular-scenario/],
        ignorePath: /\.\.\/\.\.\//, // remove ../../ from paths of injected javascripts
        devDependencies: true,
        fileTypes: {
          js: {
            block: /(([\s\t]*)\/\/\s*bower:*(\S*))(\n|\r|.)*?(\/\/\s*endbower)/gi,
            detect: {
              js: /'(.*\.js)'/gi
            },
            replace: {
              js: '\'{{filePath}}\','
            }
          }
        }
      }
    },
    browserSync: {
      dev: {
        bsFiles: {
          src : [
            'src/main/webapp/**/*.html',
            'src/main/webapp/**/*.json',
            '{.tmp/,}src/main/webapp/assets/styles/**/*.css',
            '{.tmp/,}src/main/webapp/scripts/**/*.js',
            'src/main/webapp/assets/images/**/*.{png,jpg,jpeg,gif,webp,svg}'
          ]
        }
      },
      options: {
        watchTask: true,
        proxy: "localhost:8080"
      }
    },
    clean: {
      dist: {
        files: [{
          dot: true,
          src: [
            '.tmp',
            '<%= yeoman.dist %>/*',
            '!<%= yeoman.dist %>/.git*'
          ]
        }]
      },
      server: '.tmp'
    },
    jshint: {
      options: {
        jshintrc: '.jshintrc'
      },
      all: [
        'Gruntfile.js',
        'src/main/webapp/scripts/app.js',
        'src/main/webapp/scripts/app/**/*.js',
        'src/main/webapp/scripts/components/**/*.js'
      ]
    },
    concat: {
      // not used since Uglify task does concat,
      // but still available if needed
      //    dist: {}
    },
    rev: {
      dist: {
        files: {
          src: [
            '<%= yeoman.dist %>/scripts/**/*.js',
            '<%= yeoman.dist %>/assets/styles/**/*.css',
            '<%= yeoman.dist %>/assets/images/**/*.{png,jpg,jpeg,gif,webp,svg}',
            '<%= yeoman.dist %>/assets/fonts/*'
          ]
        }
      }
    },
    useminPrepare: {
      html: 'src/main/webapp/**/*.html',
      options: {
        dest: '<%= yeoman.dist %>',
        flow: {
          html: {
            steps: {
              js: ['concat', 'uglifyjs'],
              css: ['cssmin', useminAutoprefixer] // Let cssmin concat files so it corrects relative paths to fonts and images
            },
            post: {}
          }
        }
      }
    },
    usemin: {
      html: ['<%= yeoman.dist %>/**/*.html'],
      css: ['<%= yeoman.dist %>/assets/styles/**/*.css'],
      js: ['<%= yeoman.dist %>/scripts/**/*.js'],
      options: {
        assetsDirs: ['<%= yeoman.dist %>', '<%= yeoman.dist %>/assets/styles', '<%= yeoman.dist %>/assets/images', '<%= yeoman.dist %>/assets/fonts'],
        patterns: {
          js: [
            [/(assets\/images\/.*?\.(?:gif|jpeg|jpg|png|webp|svg))/gm, 'Update the JS to reference our revved images']
          ]
        },
        dirs: ['<%= yeoman.dist %>']
      }
    },
    imagemin: {
      dist: {
        files: [{
          expand: true,
          cwd: 'src/main/webapp/assets/images',
          src: '**/*.{jpg,jpeg}', // we don't optimize PNG files as it doesn't work on Linux. If you are not on Linux, feel free to use '**/*.{png,jpg,jpeg}'
          dest: '<%= yeoman.dist %>/assets/images'
        }]
      }
    },
    svgmin: {
      dist: {
        files: [{
          expand: true,
          cwd: 'src/main/webapp/assets/images',
          src: '**/*.svg',
          dest: '<%= yeoman.dist %>/assets/images'
        }]
      }
    },
    cssmin: {
      // By default, your `index.html` <!-- Usemin Block --> will take care of
      // minification. This option is pre-configured if you do not wish to use
      // Usemin blocks.
      // dist: {
      //     files: {
      //         '<%= yeoman.dist %>/styles/main.css': [
      //             '.tmp/styles/**/*.css',
      //             'styles/**/*.css'
      //         ]
      //     }
      // }
      options: {
        root: 'src/main/webapp' // Replace relative paths for static resources with absolute path
      }
    },
    ngtemplates:    {
      dist: {
        cwd: 'src/main/webapp',
        src: ['scripts/app/**/*.html', 'scripts/components/**/*.html',],
        dest: '.tmp/templates/templates.js',
        options: {
          module: 'etmApp',
          usemin: 'scripts/app.js',
          htmlmin:  {
            removeCommentsFromCDATA: true,
            // https://github.com/yeoman/grunt-usemin/issues/44
            collapseWhitespace: true,
            collapseBooleanAttributes: true,
            conservativeCollapse: true,
            removeAttributeQuotes: true,
            removeRedundantAttributes: true,
            useShortDoctype: true,
            removeEmptyAttributes: true
          }
        }
      }
    },
    htmlmin: {
      dist: {
        options: {
          removeCommentsFromCDATA: true,
          // https://github.com/yeoman/grunt-usemin/issues/44
          collapseWhitespace: true,
          collapseBooleanAttributes: true,
          conservativeCollapse: true,
          removeAttributeQuotes: true,
          removeRedundantAttributes: true,
          useShortDoctype: true,
          removeEmptyAttributes: true,
          keepClosingSlash: true
        },
        files: [{
          expand: true,
          cwd: '<%= yeoman.dist %>',
          src: ['*.html'],
          dest: '<%= yeoman.dist %>'
        }]
      }
    },
    // Put files not handled in other tasks here
    copy: {
      dist: {
        files: [{
          expand: true,
          dot: true,
          cwd: 'src/main/webapp',
          dest: '<%= yeoman.dist %>',
          src: [
            '*.html',
            'scripts/**/*.html',
            'assets/images/**/*.{png,gif,webp}',
            'assets/fonts/*'
          ]
        }, {
          expand: true,
          cwd: '.tmp/assets/images',
          dest: '<%= yeoman.dist %>/assets/images',
          src: [
            'generated/*'
          ]
        }]
      }
    },
    concurrent: {
      server: [
      ],
      test: [
      ],
      dist: [
        'imagemin',
        'svgmin'
      ]
    },
    karma: {
      unit: {
        configFile: 'src/test/javascript/karma.conf.js',
        singleRun: true
      }
    },
    cdnify: {
      dist: {
        html: ['<%= yeoman.dist %>/*.html']
      }
    },
    ngAnnotate: {
      dist: {
        files: [{
          expand: true,
          cwd: '.tmp/concat/scripts',
          src: '*.js',
          dest: '.tmp/concat/scripts'
        }]
      }
    },
    ngconstant: {
      options: {
        name: 'etmApp',
        deps: false,
        wrap: '"use strict";\n// DO NOT EDIT THIS FILE, EDIT THE GRUNT TASK NGCONSTANT SETTINGS INSTEAD WHICH GENERATES THIS FILE\n{%= __ngModule %}'
      },
      dev: {
        options: {
          dest: 'src/main/webapp/scripts/app/app.constants.js',
        },
        constants: {
          ENV: 'dev',
          VERSION: parseVersionFromBuildGradle()
        }
      },
      prod: {
        options: {
          dest: '.tmp/scripts/app/app.constants.js',
        },
        constants: {
          ENV: 'prod',
          VERSION: parseVersionFromBuildGradle()
        }
      },
      test: {
        options: {
          dest: '.tmp/scripts/app/app.constants.js',
        },
        constants: {
          ENV: 'test',
          VERSION: parseVersionFromBuildGradle()
        }
      },
      uat: {
        options: {
          dest: '.tmp/scripts/app/app.constants.js',
        },
        constants: {
          ENV: 'uat',
          VERSION: parseVersionFromBuildGradle()
        }
      }
    },
    protractor: {
      options: {
        configFile: 'src/test/e2e/protractor.conf.js',
        webdriverManagerUpdate: true
      },
      dev: {
        options: {
          args: {
            params: {
              ENV: 'dev'
            }
          }
        }
      },
      test: {
        options: {
          args: {
            baseUrl: 'https://stlerappdev.perficient.com:8443',
            params: {
              ENV: 'test'
            }
          }
        }
      },
      uat: {
        options: {
          args: {
            baseUrl: 'https://stlerappuat.perficient.com:8443',
            params: {
              ENV: 'uat'
            }
          }
        }
      }
    }
  });

  grunt.registerTask('serve', [
    'clean:server',
    'wiredep',
    'ngconstant:dev',
    'concurrent:server',
    'browserSync',
    'watch'
  ]);

  grunt.registerTask('test', [
    'clean:server',
    'wiredep:test',
    'ngconstant:dev',
    'concurrent:test',
    'karma'
  ]);

  grunt.registerTask('build', [
    'clean:dist',
    'wiredep:app',
    'ngconstant:prod',
    'useminPrepare',
    'ngtemplates',
    'concurrent:dist',
    'concat',
    'copy:dist',
    'ngAnnotate',
    'cssmin',
    'autoprefixer',
    'uglify',
    'rev',
    'usemin',
    'htmlmin'
  ]);

  grunt.registerTask('buildUat', [
    'clean:dist',
    'wiredep:app',
    'ngconstant:uat',
    'useminPrepare',
    'ngtemplates',
    'concurrent:dist',
    'concat',
    'copy:dist',
    'ngAnnotate',
    'cssmin',
    'autoprefixer',
    'uglify',
    'rev',
    'usemin',
    'htmlmin'
  ]);
  
  grunt.registerTask('buildTest', [
    'clean:dist',
    'wiredep:app',
    'ngconstant:test',
    'useminPrepare',
    'ngtemplates',
    'concurrent:dist',
    'concat',
    'copy:dist',
    'ngAnnotate',
    'cssmin',
    'autoprefixer',
    'uglify',
    'rev',
    'usemin',
    'htmlmin'
  ]);

  grunt.registerTask('default', [
    'test',
    'build'
  ]);
};
