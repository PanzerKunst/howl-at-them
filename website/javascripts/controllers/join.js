CBR.Controllers.Join = new Class({
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

        this.$firstNameField = jQuery("#first-name");
        this.$lastNameField = jQuery("#last-name");
        this.$emailField = jQuery("#email");
        this.$emailConfirmationField = jQuery("#email-confirmation");
        this.$usStateSelect = jQuery("#us-state");

        this.$emailAlreadyRegisteredParagraph = jQuery("#email-already-registered");
        this.$emailsDoNotMatchParagraph = jQuery("#emails-do-not-match");

        this.$submitBtn = jQuery("[type=submit]");

        this._initInputsFromLocalStorage();
    },

    _initValidation:function () {
        this.validator = new CBR.Services.Validator({
            fieldIds:[
                "first-name",
                "last-name",
                "email",
                "email-confirmation",
                "password"
            ]
        });

        this.$emailField.blur(jQuery.proxy(this._checkIfEmailIsAlreadyRegistered, this));
        this.$emailConfirmationField.blur(jQuery.proxy(this._checkIfEmailConfirmationMatches, this));
    },

    _initEvents:function () {
        jQuery("form").submit(jQuery.proxy(this._doSubmit, this));

        if (Modernizr.localstorage) {
            this.$firstNameField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$lastNameField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$emailField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$emailConfirmationField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$usStateSelect.change(jQuery.proxy(this.localStoreInput, this));
        }
    },

    _doSubmit:function (e) {
        e.preventDefault();

        if (this.validator.isValid() &&
            this._isEmailNotRegisteredYet() &&
            this._isEmailConfirmationMatching()) {

            this.$submitBtn.button('loading');

            var account = new CBR.Models.Account({
                firstName: this.$firstNameField.val(),
                lastName: this.$lastNameField.val(),
                emailAddress: this.$emailField.val().toLowerCase(),
                password: jQuery("#password").val(),
                usStateId: this.$usStateSelect.val()
            });

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/accounts",
                data: CBR.JsonUtil.stringifyModel(account),
                onSuccess: function (responseText, responseXML) {
                    this._clearFormValuesInLocalStorage();
                    location.replace("/?from=join&email=" + account.getEmailAddress());
                }.bind(this),
                onFailure: function (xhr) {
                    alert("AJAX fail :(");
                }
            }).post();
        }
    },

    _initInputsFromLocalStorage: function() {
        if (Modernizr.localstorage) {
            this.initInputFromLocalStorage(this.$firstNameField);
            this.initInputFromLocalStorage(this.$lastNameField);
            this.initInputFromLocalStorage(this.$emailField);
            this.initInputFromLocalStorage(this.$emailConfirmationField);
            this.initInputFromLocalStorage(this.$usStateSelect);
        }
    },

    _clearFormValuesInLocalStorage: function() {
        if (Modernizr.localstorage) {
            this.removeLocalStorageValueForInput(this.$firstNameField);
            this.removeLocalStorageValueForInput(this.$lastNameField);
            this.removeLocalStorageValueForInput(this.$emailField);
            this.removeLocalStorageValueForInput(this.$emailConfirmationField);
            this.removeLocalStorageValueForInput(this.$usStateSelect);
        }
    },

    _checkIfEmailIsAlreadyRegistered: function () {
        this.$emailAlreadyRegisteredParagraph.slideUpCustom();

        if (this.$emailField.val()) {
            var _this = this;

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/accounts/first?email=" + this.$emailField.val().toLowerCase(),
                onSuccess: function (responseText, responseXML) {
                    if (this.status !== _this.httpStatusCode.noContent && !_this.validator.isFlaggedInvalid(_this.$emailField)) {
                        _this.validator.flagInvalid(_this.$emailField);
                        _this.$emailAlreadyRegisteredParagraph.slideDownCustom();
                    }
                },
                onFailure: function (xhr) {
                    alert("AJAX fail :(");
                }
            }).get();
        }
    },

    _checkIfEmailConfirmationMatches: function () {
        this.$emailsDoNotMatchParagraph.slideUpCustom();

        var email = this.$emailField.val();
        var emailConfirmation = this.$emailConfirmationField.val();

        if ((email || emailConfirmation) &&
            email !== emailConfirmation &&
            !this.validator.isFlaggedInvalid(this.$emailConfirmationField)) {

            this.validator.flagInvalid(this.$emailConfirmationField);
            this.$emailsDoNotMatchParagraph.slideDownCustom();
        }
    },

    _isEmailNotRegisteredYet: function() {
        return true;
    },

    _isEmailConfirmationMatching: function() {
        var email = this.$emailField.val();
        var emailConfirmation = this.$emailConfirmationField.val();

        var isMatching = (email !== "" || emailConfirmation !== "") && email === emailConfirmation;

        if (!isMatching) {
            this.validator.flagInvalid(this.$emailConfirmationField);
            this.$emailsDoNotMatchParagraph.slideDownCustom();
        }

        return isMatching;
    }
});
