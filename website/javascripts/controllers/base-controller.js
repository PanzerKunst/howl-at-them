CBR.Controllers.BaseController = new Class({
    initialize: function (options) {
        this.options = options;
    },

    getEl: function () {
        return jQuery(this.options.el);
    },

    setActivePill: function (e) {
        e.preventDefault();

        var $a = jQuery(e.currentTarget);
        var $li = $a.parent();
        var $ul = $li.parent();
        $ul.children().removeClass("active");

        $li.addClass("active");

        $a.trigger("active-toggled");
    },

    initElements: function () {
        this._applyModernizrRules();
    },

    isDesktopBrowser: function() {
        return Modernizr.mq("screen and (min-width: " + CBR.desktopBreakPoint + ")");
    },

    initInputFromLocalStorage: function($input) {
        var valueInLocalStorage = localStorage.getItem($input[0].id);
        if (valueInLocalStorage) {
            $input.val(valueInLocalStorage);
        }
    },

    localStoreInput:function (e) {
        var input = e.currentTarget;
        localStorage.setItem(input.id, jQuery(input).val());
    },

    removeLocalStorageValueForInput: function($input) {
        localStorage.removeItem($input[0].id);
    },

    _applyModernizrRules: function () {
        if (!Modernizr.input.placeholder) {
            jQuery(".mdnz-polyfill.placeholder").show();
        }
    },

    httpStatusCode: {
        noContent: 204,
        unauthorized: 401
    }
});