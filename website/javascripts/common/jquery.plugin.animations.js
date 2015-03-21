(function ($) {
    $.fn.slideDownCustom = function () {
        if (CBR.isBrowserLargeScreen()) {
            return this.slideDown();
        }

        return this.show();
    };

    $.fn.slideUpCustom = function () {
        if (CBR.isBrowserLargeScreen()) {
            return this.slideUp();
        }

        return this.hide();
    };
})(jQuery);
