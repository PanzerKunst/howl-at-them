CBR.Services.Validator = new Class({
    checkEmpty: "empty",
    checkEmail: "email",
    checkUsername: "username",
    checkDateInFuture: "in-future",
    checkDateInMaxTwoWeeks: "in-max-2-weeks",
    checkMinLength: "min-length",
    checkMaxLength: "max-length",
    checkInteger: "integer",
    checkDecimal: "decimal",

    initialize: function (options) {
        this.options = options;

        for (var i = 0; i < this._getFieldIds().length; i++) {
            var $field = jQuery("#" + this._getFieldIds()[i]);

            if ($field.hasClass("pills"))
                this._addClickEvents($field);
            else {
                this._addBlurEvent($field);
                this._addValueChangedEvent($field);
            }
        }
    },

    isValid: function () {
        var result = true;

        for (var i = 0; i < this._getFieldIds().length; i++)
            if (!this._validateField(jQuery("#" + this._getFieldIds()[i]), false))
                result = false;

        return result;
    },

    flagValid: function ($field) {
        var $wrapper = $field.parent();
        $wrapper.removeClass("has-error");
        //$wrapper.addClass("has-success");
    },

    flagInvalid: function ($field) {
        var $wrapper = $field.parent();
        //$wrapper.removeClass("has-success");
        $wrapper.addClass("has-error");
    },

    isFlaggedInvalid: function ($field) {
        return $field.parent().hasClass("has-error");
    },

    _getFieldIds: function () {
        return this.options.fieldIds;
    },

    _get$empty: function ($field) {
        return this._get$error($field, this.checkEmpty);
    },

    _get$email: function ($field) {
        return this._get$error($field, this.checkEmail);
    },

    _get$username: function ($field) {
        return this._get$error($field, this.checkUsername);
    },

    _get$inFuture: function ($field) {
        return this._get$error($field, this.checkDateInFuture);
    },

    _get$inMaxTwoWeeks: function ($field) {
        return this._get$error($field, this.checkDateInMaxTwoWeeks);
    },

    _get$minLength: function ($field) {
        return this._get$error($field, this.checkMinLength);
    },

    _get$maxLength: function ($field) {
        return this._get$error($field, this.checkMaxLength);
    },

    _get$integer: function ($field) {
        return this._get$error($field, this.checkInteger);
    },

    _get$decimal: function ($field) {
        return this._get$error($field, this.checkDecimal);
    },

    _get$error: function ($field, checkType) {
        return $field.parent().find("p[data-check=" + checkType + "]");
    },

    _isToCheckIfEmpty: function ($field) {
        return this._get$empty($field).length === 1;
    },

    _isToCheckIfEmail: function ($field) {
        return this._get$email($field).length === 1;
    },

    _isToCheckIfUsername: function ($field) {
        return this._get$username($field).length === 1;
    },

    _isToCheckIfInFuture: function ($field) {
        return this._get$inFuture($field).length === 1;
    },

    _isToCheckIfInMaxTwoWeeks: function ($field) {
        return this._get$inMaxTwoWeeks($field).length === 1;
    },

    _isToCheckIfMinLength: function ($field) {
        return this._get$minLength($field).length === 1;
    },

    _isToCheckIfMaxLength: function ($field) {
        return this._get$maxLength($field).length === 1;
    },

    _isToCheckIfInteger: function ($field) {
        return this._get$integer($field).length === 1;
    },

    _isToCheckIfDecimal: function ($field) {
        return this._get$decimal($field).length === 1;
    },

    _isValidEmail: function (email) {
        if (email === "")
            return true;

        var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
        return reg.test(email);
    },

    _isValidUsername: function (username) {
        var reg = /^([A-Za-z0-9_\-])+$/;
        return reg.test(username);
    },

    _isInFuture: function (dateStr) {
        var yearMonthDay = dateStr.split("-");
        var year = parseInt(yearMonthDay[0], 10);
        var month = parseInt(yearMonthDay[1], 10);
        var day = parseInt(yearMonthDay[2], 10);

        var date = new Date(year, month - 1, day);
        var now = new Date();

        var oneDayInMillis = 1000 * 60 * 60 * 24;
        var nbDaysDifference = Math.ceil((date - now) / oneDayInMillis);

        return nbDaysDifference > 0;
    },

    _isInMaxTwoWeeks: function (dateStr) {
        var yearMonthDay = dateStr.split("-");
        var year = parseInt(yearMonthDay[0], 10);
        var month = parseInt(yearMonthDay[1], 10);
        var day = parseInt(yearMonthDay[2], 10);

        var date = new Date(year, month - 1, day);
        var inTwoWeeks = new Date();
        inTwoWeeks.setDate(inTwoWeeks.getDate() + 14);

        var oneDayInMillis = 1000 * 60 * 60 * 24;
        var nbDaysDifference = Math.ceil((inTwoWeeks - date) / oneDayInMillis);

        return nbDaysDifference >= 0;
    },

    _isMinLength: function(value, minLength) {
        if (value === null || value === undefined || value === "")
            return true;
        return value.length >= minLength;
    },

    _isMaxLength: function(value, maxLength) {
        if (value === null || value === undefined || value === "")
            return true;
        return value.length <= maxLength;
    },

    _isInteger: function(value) {
        var reg = /^\d*$/;
        return reg.test(value);
    },

    _isDecimal: function(value) {
        var reg = /^\d*\.?\d*$/;
        return reg.test(value);
    },

    _validateField: function ($field, isOnBlur) {

        // Empty?
        if (this._isToCheckIfEmpty($field)) {
            if ($field.hasClass("nav-pills") &&
                $field.children(".active").length === 0) {

                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$empty($field));
                return false;
            }
            if (!$field.hasClass("nav-pills") &&
                !$field.val().trim()) {

                if (!isOnBlur) {
                    this.flagInvalid($field);
                    this._slideDownErrorMessage(this._get$empty($field));
                }
                return false;
            }

            this._slideUpErrorMessage(this._get$empty($field));
        }

        // Email?
        if (this._isToCheckIfEmail($field)) {
            if (!this._isValidEmail($field.val())) {
                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$email($field));
                return false;
            }

            this._slideUpErrorMessage(this._get$email($field));
        }

        // Username?
        if (this._isToCheckIfUsername($field)) {
            if (!this._isValidUsername($field.val())) {
                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$username($field));
                return false;
            }
            this._slideUpErrorMessage(this._get$username($field));
        }

        // In the future?
        if (this._isToCheckIfInFuture($field)) {
            if (!this._isInFuture($field.val())) {
                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$inFuture($field));
                return false;
            }
            this._slideUpErrorMessage(this._get$inFuture($field));
        }

        // In max 2 weeks?
        if (this._isToCheckIfInMaxTwoWeeks($field)) {
            if (!this._isInMaxTwoWeeks($field.val())) {
                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$inMaxTwoWeeks($field));
                return false;
            }
            this._slideUpErrorMessage(this._get$inMaxTwoWeeks($field));
        }

        // Min length?
        if (this._isToCheckIfMinLength($field)) {
            if (!this._isMinLength($field.val(), $field.data("min-length"))) {
                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$minLength($field));
                return false;
            }
            this._slideUpErrorMessage(this._get$minLength($field));
        }

        // Max length?
        if (this._isToCheckIfMaxLength($field)) {
            if (!this._isMaxLength($field.val(), $field.attr("maxlength"))) {
                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$maxLength($field));
                return false;
            }
            this._slideUpErrorMessage(this._get$maxLength($field));
        }

        // Integer number?
        if (this._isToCheckIfInteger($field)) {
            if (!this._isInteger($field.val())) {
                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$integer($field));
                return false;
            }
            this._slideUpErrorMessage(this._get$integer($field));
        }

        // Decimal number?
        if (this._isToCheckIfDecimal($field)) {
            if (!this._isDecimal($field.val())) {
                this.flagInvalid($field);
                this._slideDownErrorMessage(this._get$decimal($field));
                return false;
            }
            this._slideUpErrorMessage(this._get$decimal($field));
        }

        this.flagValid($field);

        return true;
    },

    _addBlurEvent: function ($field) {
        var _this = this;

        $field.blur(function () {
            _this._validateField($field, true);
        });
    },

    _addValueChangedEvent: function ($field) {
        var _this = this;

        $field.change(function () {
            _this._validateField($field);
        });
    },

    _addClickEvents: function ($field) {
        var _this = this;

        $field.find("a").bind("active-toggled", function () {
            _this._validateField($field);
        });
    },

    _slideDownErrorMessage: function($errorMsgEl) {
        if ($errorMsgEl.html()) {
            $errorMsgEl.slideDownCustom();
        }
    },

    _slideUpErrorMessage: function($errorMsgEl) {
        if ($errorMsgEl.html()) {
            $errorMsgEl.slideUpCustom();
        }
    }
});
