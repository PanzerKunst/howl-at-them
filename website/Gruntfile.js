module.exports = function (grunt) {

    require("matchdep").filterDev("grunt-*").forEach(grunt.loadNpmTasks);

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        jshint: {
            dist: [
                'Gruntfile.js',
                'javascripts/**/*.js'
            ],
            options: {
                globals: {
                    debug: true,
                    forin: true,
                    eqnul: true,
                    noarg: true,
                    noempty: true,
                    eqeqeq: true,
                    boss: true,
                    loopfunc: true,
                    evil: true,
                    laxbreak: true,
                    bitwise: true,
                    undef: true,
                    curly: true,
                    nonew: true,
                    browser: true,
                    devel: true,
                    jquery: true,
                    mootools: true
                }
            }
        },

        handlebars: {
            options: {
                namespace: 'CBR.Templates',
                processName: function(filePath) {
                    return filePath.replace(/^javascripts\/templates\//, '').replace(/\.hbs$/, '');
                }
            },
            all: {
                files: {
                    "public/templates.js": ["javascripts/templates/**/*.hbs"]
                }
            }
        },

        concat: {
            options: {
                separator:';'
            },
            dist: {
                src: [
                    // Libs
                    "libs/h5bp/modernizr-custom.js",
                    "libs/jquery-2.1.0.min.js",
                    "javascripts/common/jquery.noConflict.js",
                    "libs/mootools/mootools-core-1.5.0-full-nocompat-yc.js",
                    "libs/mootools/mootools-more-1.5.0.js",
                    "libs/bootstrap/js/bootstrap.min.js",
                    "libs/fastclick.js",
                    "libs/handlebars.runtime-v1.3.0.js",
                    "libs/lodash.min.js",
                    "libs/jquery.visible.min.js",
                    "libs/js-breakpoints/breakpoints.js",
                    "libs/jvectormap/jquery-jvectormap-1.2.2.min.js",
                    "libs/jvectormap/jquery-jvectormap-us-aea-en.js",

                    // Global
                    "javascripts/global.js",

                    // Common
                    "javascripts/common/jquery.plugin.animations.js",
                    "javascripts/common/jsonUtil.js",

                    // Services
                    "javascripts/services/validator.js",

                    // Models
                    "javascripts/models/jsonSerializable.js",
                    "javascripts/models/account.js",
                    "javascripts/models/report.js",
                    "javascripts/models/stateLegislator.js",

                    // Controllers
                    "javascripts/controllers/baseController.js",
                    "javascripts/controllers/legislatorListing.js",
                    "javascripts/controllers/admin.js",
                    "javascripts/controllers/adminLogin.js",
                    "javascripts/controllers/findYourLegislator.js",
                    "javascripts/controllers/index.js",
                    "javascripts/controllers/searchLegislators.js",
                    "javascripts/controllers/stateLegislator.js",
                    "javascripts/controllers/stateReports.js",

                    // Templates
                    "javascripts/templates/handlebarsHelpers.js",
                    "public/templates.js"
                ],
                dest: 'public/<%= pkg.name %>.js'
            }
        },

        sass: {
            build: {
                files: {
                    'public/<%= pkg.name %>.css': 'sass/main.scss'
                }
            }
        },

        /* Task fails
        cssc: {
            build: {
                options: {
                    consolidateViaDeclarations: true,
                    consolidateViaSelectors: true,
                    consolidateMediaQueries: true
                },
                files: {
                    'public/<%= pkg.name %>.css': 'public/<%= pkg.name %>.css'
                }
            }
        }, */

        cssmin: {
            build: {
                src: [
                    // Libs
                    'libs/bootstrap/css/bootstrap.css',
                    'libs/jvectormap/jquery-jvectormap-1.2.2.css',

                    // Rest
                    'public/<%= pkg.name %>.css'
                ],
                dest: 'public/<%= pkg.name %>-v1.css'
            }
        },

        watch: {
            js: {
                files: ['<%= concat.dist.src %>'],
                tasks: ['buildjs']
            },

            css: {
                files: ['sass/**/*.scss'],
                tasks: ['buildcss']
            }
        }
    });

    grunt.registerTask('default', ['buildjs', 'buildcss']);
    grunt.registerTask('buildjs',  ['jshint', 'handlebars', 'concat']);
    grunt.registerTask('buildcss',  ['sass', 'cssmin']);
};
