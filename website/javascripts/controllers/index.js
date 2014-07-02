CBR.Controllers.Index = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
    },

    initElements: function () {
        this.parent();

        jQuery("#map").vectorMap({
            map: 'us_aea_en',
            backgroundColor: "#fff",
            regionStyle: {
                initial: {
                    fill: '#999' // this adds color too all regions (Bootstrap's $gray-light)
                }
            },
            onRegionClick: function(event, code) {
                var codePrefix = "US-";
                location.href = "/state-legislators?usStateId=" + code.substring(codePrefix.length);
            }
        });
    }
});
