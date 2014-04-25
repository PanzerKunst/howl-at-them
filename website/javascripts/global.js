if (typeof String.prototype.startsWith !== 'function') {
    String.prototype.startsWith = function (str) {
        return this.slice(0, str.length) === str;
    };
}

// create the base namespace
var CBR = CBR || {};

// create additional namespace
CBR.Models = CBR.Models || {};
CBR.Controllers = CBR.Controllers || {};
CBR.Services = CBR.Services || {};

CBR.isEmptyObject = function (obj) {
    for (var prop in obj) {
        if (obj.hasOwnProperty(prop))
            return false;
    }

    return true;
};

CBR.isBrowserSmallScreen = function () {
    return window.getComputedStyle(
        document.querySelector("html"), ":after"
    ).getPropertyValue("content") === "none";
};

CBR.isBrowserMediumScreen = function () {
    return window.getComputedStyle(
        document.querySelector("html"), ":after"
    ).getPropertyValue("content") === "\"GLOBAL_MEDIUM_SCREEN_BREAKPOINT\"";
};

CBR.isBrowserLargeScreen = function () {
    return window.getComputedStyle(
        document.querySelector("html"), ":after"
    ).getPropertyValue("content") === "\"GLOBAL_LARGE_SCREEN_BREAKPOINT\"";
};
