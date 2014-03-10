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

    isBrowserMediumScreen: function() {
        return Modernizr.mq("screen and (min-width: " + CBR.mediumScreenBreakPoint + ")");
    },

    isBrowserLargeScreen: function() {
        return Modernizr.mq("screen and (min-width: " + CBR.largeScreenBreakPoint + ")");
    },

    saveInLocalStorage: function(key, value) {
        if (Modernizr.localstorage) {
            var pageId = jQuery("body").attr("id");

            var pageDataInLocalStorage = JSON.parse(localStorage.getItem(pageId)) || {};
            pageDataInLocalStorage[key] = value;

            localStorage.setItem(pageId, JSON.stringify(pageDataInLocalStorage));
        }
    },

    getFromLocalStorage: function(key) {
        if (Modernizr.localstorage) {
            var pageId = jQuery("body").attr("id");

            var pageDataInLocalStorage = JSON.parse(localStorage.getItem(pageId)) || {};

            return pageDataInLocalStorage[key];
        }
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