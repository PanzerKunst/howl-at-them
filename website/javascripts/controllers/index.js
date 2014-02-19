CBR.Controllers.Index = new Class({
    Extends:CBR.Controllers.BaseController,

    initialize:function (options) {
        this.parent(options);
    },

    run:function () {
        this.initElements();
        this._initValidation();
        this._initEvents();
    },

    initElements:function () {
        this.parent();

        this.$authFailed = jQuery("#auth-failed");
        this.$form = jQuery("form");
        this.$submitBtn = jQuery("[type=submit]");

        if (this._getAction()) {
            this.$form.show();
        } else {
            this.$form.hide();
        }
    },

    _getAction: function () {
        return this.options.action;
    },

    _initValidation:function () {
        this.validator = new CBR.Services.Validator({
            fieldIds:[
                "email",
                "password"
            ]
        });
    },

    _initEvents:function () {
        jQuery("#login-link").click(jQuery.proxy(this._toggleLoginForm, this));

        this.$form.submit(jQuery.proxy(this._doSubmit, this));
    },

    _toggleLoginForm: function(e) {
        if (this.$form.is(":visible")) {
            this.$form.slideUpCustom();
        } else {
            this.$form.slideDownCustom();
        }
    },

    _doSubmit:function (e) {
        e.preventDefault();

        this.$authFailed.slideUpCustom();

        if (this.validator.isValid()) {
            this.$submitBtn.button('loading');

            var account = {
                emailAddress: jQuery("#email").val().toLowerCase(),
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
                        _this.$submitBtn.button('reset');
                        _this.$authFailed.slideDownCustom();
                    }
                    else {
                        location.href = "/home";
                    }
                },
                onFailure: function (xhr) {
                    alert("AJAX fail :(");
                }
            }).post();
        }
    }
});
