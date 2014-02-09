CBR.Controllers.NewOfficial = new Class({
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

        this.$saveSuccessfulAlert = jQuery(".alert");
        this.$saveSuccessfulAlert.bind('close.bs.alert', function (e) {
            e.preventDefault();
            jQuery(this).slideUpCustom();
        });

        this.$firstNameField = jQuery("#first-name");
        this.$lastNameField = jQuery("#last-name");
        this.$titleOfOfficialSelect = jQuery("#title-of-official");
        this.$politicalPartySelect = jQuery("#political-party");
        this.$usStateSelect = jQuery("#us-state");
        this.$districtField = jQuery("#district");
        this.$phoneNumberField = jQuery("#phone-number");
        this.$ideologyRankingField = jQuery("#ideology-ranking");

        this.$submitBtn = jQuery("[type=submit]");

        this._initInputsFromLocalStorage();
    },

    _initValidation:function () {
        this.validator = new CBR.Services.Validator({
            fieldIds:[
                "first-name",
                "last-name",
                "district",
                "ideology-ranking"
            ]
        });
    },

    _initEvents:function () {
        jQuery("form").submit(jQuery.proxy(this._doSubmit, this));

        if (Modernizr.localstorage) {
            this.$firstNameField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$lastNameField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$titleOfOfficialSelect.change(jQuery.proxy(this.localStoreInput, this));
            this.$politicalPartySelect.change(jQuery.proxy(this.localStoreInput, this));
            this.$usStateSelect.change(jQuery.proxy(this.localStoreInput, this));
            this.$districtField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$phoneNumberField.keyup(jQuery.proxy(this.localStoreInput, this));
            this.$ideologyRankingField.keyup(jQuery.proxy(this.localStoreInput, this));
        }
    },

    _doSubmit:function (e) {
        e.preventDefault();

        if (this.validator.isValid()) {

            this.$saveSuccessfulAlert.slideUpCustom();
            this.$submitBtn.button('loading');

            var official = new CBR.Models.Official({
                firstName: this.$firstNameField.val(),
                lastName: this.$lastNameField.val(),
                titleId: this.$titleOfOfficialSelect.val(),
                politicalPartyId: this.$politicalPartySelect.val(),
                usStateId: this.$usStateSelect.val(),
                district: this.$districtField.val(),
                phoneNumber: this.$phoneNumberField.val() ? this.$phoneNumberField.val() : null,
                ideologyRanking: this.$ideologyRankingField.val() ? this.$ideologyRankingField.val() : null
            });

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/officials",
                data: CBR.JsonUtil.stringifyModel(official),
                onSuccess: function (responseText, responseXML) {
                    this._clearFormValuesInLocalStorage();
                    location.replace("/admin/officials/new?from=new-official");
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
            this.initInputFromLocalStorage(this.$titleOfOfficialSelect);
            this.initInputFromLocalStorage(this.$politicalPartySelect);
            this.initInputFromLocalStorage(this.$usStateSelect);
            this.initInputFromLocalStorage(this.$districtField);
            this.initInputFromLocalStorage(this.$phoneNumberField);
            this.initInputFromLocalStorage(this.$ideologyRankingField);
        }
    },

    _clearFormValuesInLocalStorage: function() {
        if (Modernizr.localstorage) {
            this.removeLocalStorageValueForInput(this.$firstNameField);
            this.removeLocalStorageValueForInput(this.$lastNameField);
            this.removeLocalStorageValueForInput(this.$titleOfOfficialSelect);
            this.removeLocalStorageValueForInput(this.$politicalPartySelect);
            this.removeLocalStorageValueForInput(this.$usStateSelect);
            this.removeLocalStorageValueForInput(this.$districtField);
            this.removeLocalStorageValueForInput(this.$phoneNumberField);
            this.removeLocalStorageValueForInput(this.$ideologyRankingField);
        }
    }
});
