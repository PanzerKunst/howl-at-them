(function ($) {
    $.fn.slideDownCustom = function () {
        if (Modernizr.mq("screen and (min-width: " + CBR.largeScreenBreakPoint + ")"))
            return this.slideDown();

        return this.show();
    };

    $.fn.slideUpCustom = function () {
        if (Modernizr.mq("screen and (min-width: " + CBR.largeScreenBreakPoint + ")"))
            return this.slideUp();

        return this.hide();
    };
})(jQuery);
