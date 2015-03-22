module.exports = function (grunt) {

    require("matchdep").filterDev("grunt-*").forEach(grunt.loadNpmTasks);

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        eslint: {
            target: [
                "javascripts/**/*.js"
            ],
            options: {
                configFile: "eslint.json"
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

        react: {
            site: {
                files: {
                    "public/react.js": [
                        "javascripts/controllers/**/*.react.js"
                    ]
                }
            }
        },

        concat: {
            options: {
                separator:';'
            },
            dist: {
                src: [
                    // Non-CDN libs
                    "libs/jquery.visible.min.js",
                    "libs/js-breakpoints/breakpoints.js",
                    "libs/jvectormap/jquery-jvectormap-1.2.2.min.js",
                    "libs/jvectormap/jquery-jvectormap-us-aea-en.js",
                    "libs/jquery.browser.mobile.min.js",

                    // Global
                    "javascripts/global.js",

                    // Common
                    "javascripts/common/jquery.plugin.animations.js",
                    "javascripts/common/jsonUtil.js",

                    // Services
                    "javascripts/services/validator.js",
                    "javascripts/services/string.js",

                    // Models
                    "javascripts/models/jsonSerializable.js",
                    "javascripts/models/account.js",
                    "javascripts/models/report.js",
                    "javascripts/models/stateLegislator.js",
                    "javascripts/models/whipCount.js",

                    // Controllers
                    "javascripts/controllers/baseController.js",
                    "javascripts/controllers/legislatorListing.js",
                    "javascripts/controllers/admin.js",
                    "javascripts/controllers/adminLogin.js",
                    "javascripts/controllers/findYourLegislator.js",
                    "javascripts/controllers/index.js",
                    "javascripts/controllers/stateLegislator.js",
                    "javascripts/controllers/stateLegislators.js",

                    // React
                    "public/react.js",

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

        cssmin: {
            build: {
                src: [
                    // Libs
                    "libs/h5bp/normalize.css",
                    'libs/jvectormap/jquery-jvectormap-1.2.2.css',

                    // Rest
                    'public/<%= pkg.name %>.css'
                ],
                dest: 'public/<%= pkg.name %>-v1.css'
            }
        },

        watch: {
            js: {
                files: [
                    '<%= concat.dist.src %>',
                    "javascripts/templates/**/*.hbs",
                    "javascripts/controllers/**/*.react.js"
                ],
                tasks: ['buildjs']
            },

            css: {
                files: ['sass/**/*.scss'],
                tasks: ['buildcss']
            }
        }
    });

    grunt.registerTask('default', ['buildjs', 'buildcss']);
    grunt.registerTask('buildjs',  ['eslint', 'handlebars', 'react', 'concat']);
    grunt.registerTask('buildcss',  ['sass', 'cssmin']);
};
