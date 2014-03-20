CBR.Controllers.AdminLogin = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initValidation();
        this._initEvents();
    },

    initElements: function () {
        this.parent();

        this.$authFailed = jQuery(".other-form-error");
        this.$form = jQuery("form");
        this.$submitBtn = jQuery("[type=submit]");
    },

    _initValidation: function () {
        this.validator = new CBR.Services.Validator({
            fieldIds: [
                "username",
                "password"
            ]
        });
    },

    _initEvents: function () {
        this.$form.submit(jQuery.proxy(this._doSubmit, this));
    },

    _doSubmit:function (e) {
        e.preventDefault();

        this.$authFailed.slideUpCustom();

        if (this.validator.isValid()) {
            this.$submitBtn.button('loading');

            var account = {
                username: jQuery("#username").val(),
                password: jQuery("#password").val()
            };

            var _this = this;

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/authenticate",
                data: CBR.JsonUtil.stringifyModel(account),
                onSuccess: function (responseText, responseXML) {
                    if (this.status === _this.httpStatusCode.noContent) {
                        // We delay because seeing the loading state a bit longer looks better
                        _.delay(function() { _this.$submitBtn.button('reset'); }, 500);

                        _this.$authFailed.slideDownCustom();
                    }
                    else {
                        location.replace("/admin");
                    }
                },
                onFailure: function (xhr) {
                    this.$submitBtn.button('reset');
                    alert("AJAX fail :(");
                }.bind(this)
            }).post();
        }
    }
});
