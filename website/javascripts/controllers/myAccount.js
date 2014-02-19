CBR.Controllers.MyAccount = new Class({
    Extends:CBR.Controllers.BaseController,

    initialize:function (options) {
        this.parent(options);
    },

    run:function () {
        this.initElements();
        this._initValidation();
        this._initEvents();
    },

    _getAccount: function () {
        return this.options.account;
    },

    initElements:function () {
        this.parent();

        this.$saveSuccessfulAlert = jQuery(".alert");
        this.$saveSuccessfulAlert.bind('close.bs.alert', function (e) {
            e.preventDefault();
            jQuery(this).slideUpCustom();
        });

        this.$firstNameField = jQuery("#first-name");
        this.$lastNameField = jQuery("#last-name");
        this.$usStateSelect = jQuery("#us-state");

        this.$submitBtn = jQuery("[type=submit]");

        this._initInputsFromLocalStorage();
    },

    _initValidation:function () {
        this.validator = new CBR.Services.Validator({
            fieldIds:[
                "first-name",
                "last-name"
            ]
        });
    },

    _initEvents:function () {
        jQuery("form").submit(jQuery.proxy(this._doSubmit, this));

        if (Modernizr.localstorage) {
            this.$firstNameField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$lastNameField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$usStateSelect.change(jQuery.proxy(this.localStoreInput, this));
        }
    },

    _doSubmit:function (e) {
        e.preventDefault();

        if (this.validator.isValid()) {

            this.$saveSuccessfulAlert.slideUpCustom();
            this.$submitBtn.button('loading');

            var account = new CBR.Models.Account({
                id: this._getAccount().id,
                firstName: this.$firstNameField.val(),
                lastName: this.$lastNameField.val(),
                emailAddress: this._getAccount().emailAddress,
                usStateId: this.$usStateSelect.val()
            });

            var password = jQuery("#password").val();
            if (password) {
                account.setPassword(password);
            }

            new Request({
                urlEncoded: false,
                emulation:false, // Otherwise PUT and DELETE requests are sent as POST
                headers: { "Content-Type": "application/json" },
                url: "/api/accounts",
                data: CBR.JsonUtil.stringifyModel(account),
                onSuccess: function (responseText, responseXML) {
                    this._clearFormValuesInLocalStorage();
                    this.$submitBtn.button('reset');
                    jQuery(window).scrollTop(0);
                    this.$saveSuccessfulAlert.slideDownCustom();
                }.bind(this),
                onFailure: function (xhr) {
                    alert("AJAX fail :(");
                }
            }).put();
        }
    },

    _initInputsFromLocalStorage: function() {
        if (Modernizr.localstorage) {
            this.initInputFromLocalStorage(this.$firstNameField);
            this.initInputFromLocalStorage(this.$lastNameField);
            this.initInputFromLocalStorage(this.$usStateSelect);
        }
    },

    _clearFormValuesInLocalStorage: function() {
        if (Modernizr.localstorage) {
            this.removeLocalStorageValueForInput(this.$firstNameField);
            this.removeLocalStorageValueForInput(this.$lastNameField);
            this.removeLocalStorageValueForInput(this.$usStateSelect);
        }
    }
});
