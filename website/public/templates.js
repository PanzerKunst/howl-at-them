this["CBR"] = this["CBR"] || {};
this["CBR"]["Templates"] = this["CBR"]["Templates"] || {};

this["CBR"]["Templates"]["deleteReportModal"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"modal fade\" id=\"delete-report-modal\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\r\n    <div class=\"modal-dialog modal-sm\">\r\n        <div class=\"modal-content\">\r\n            <div class=\"modal-body\">\r\n                <p>Delete this report?</p>\r\n            </div>\r\n            <div class=\"modal-footer\">\r\n                <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Cancel</button>\r\n                <button type=\"button\" class=\"btn btn-primary\" data-loading-text=\"Deleting report...\" id=\"confirm-modal\">Delete report</button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>\r\n";
  });