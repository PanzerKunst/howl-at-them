this["CBR"] = this["CBR"] || {};
this["CBR"]["Templates"] = this["CBR"]["Templates"] || {};

this["CBR"]["Templates"]["committeeSelect"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  var lambda=this.lambda, escapeExpression=this.escapeExpression;
  return "<option value=\""
    + escapeExpression(lambda(depth0, depth0))
    + "\">"
    + escapeExpression(lambda(depth0, depth0))
    + "</option>\r\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, buffer = "<option value=\"\">All committees</option>\r\n<option disabled>----</option>\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.committeeNames : depth0), {"name":"each","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer;
},"useData":true});

this["CBR"]["Templates"]["deleteReportModal"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<div class=\"modal fade\" id=\"delete-report-modal\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\r\n    <div class=\"modal-dialog modal-sm\">\r\n        <div class=\"modal-content\">\r\n            <div class=\"modal-body\">\r\n                <p>Delete this report?</p>\r\n            </div>\r\n            <div class=\"modal-footer\">\r\n                <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Cancel</button>\r\n                <button type=\"button\" class=\"btn btn-primary\" data-loading-text=\"Deleting report...\" id=\"confirm-delete\">Delete report</button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>\r\n";
  },"useData":true});

this["CBR"]["Templates"]["editReportModal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  return " selected ";
  },"3":function(depth0,helpers,partials,data) {
  return " checked ";
  },"5":function(depth0,helpers,partials,data) {
  return "                                    checked\r\n                                    ";
  },"7":function(depth0,helpers,partials,data) {
  return "                                selected\r\n";
  },"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, lambda=this.lambda, escapeExpression=this.escapeExpression, buffer = "<div class=\"modal fade\" id=\"edit-report-modal\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\r\n    <div class=\"modal-dialog\">\r\n        <div class=\"modal-content\">\r\n            <div class=\"modal-body\">\r\n                <form>\r\n                    <section class=\"form-groups report\">\r\n                        <div class=\"form-group\">\r\n                            <label for=\"edit-author-name\">Your name <span>*</span></label><!--\r\n                         --><input type=\"text\" id=\"edit-author-name\" class=\"form-control\" maxlength=\"64\" data-min-length=\"2\" value=\""
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.report : depth0)) != null ? stack1.authorName : stack1), depth0))
    + "\" />\r\n\r\n                            <p class=\"field-error\" data-check=\"empty\"></p>\r\n                            <p class=\"field-error\" data-check=\"min-length\">Two characters minimun</p>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label for=\"edit-contact\">Contact</label><!--\r\n                         --><select id=\"edit-contact\" class=\"form-control\">\r\n                                <option value=\"\">"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.noContact : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.metLegislator : stack1)) != null ? stack1.code : stack1), depth0))
    + "\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isContact : depth0)) != null ? stack1.metLegislator : stack1), {"name":"if","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.metLegislator : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.talkedToLegislator : stack1)) != null ? stack1.code : stack1), depth0))
    + "\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isContact : depth0)) != null ? stack1.talkedToLegislator : stack1), {"name":"if","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.talkedToLegislator : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.contactWithStaff : stack1)) != null ? stack1.code : stack1), depth0))
    + "\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isContact : depth0)) != null ? stack1.contactWithStaff : stack1), {"name":"if","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.contactWithStaff : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.waitingForCallback : stack1)) != null ? stack1.code : stack1), depth0))
    + "\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isContact : depth0)) != null ? stack1.waitingForCallback : stack1), {"name":"if","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.waitingForCallback : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.leftVoicemail : stack1)) != null ? stack1.code : stack1), depth0))
    + "\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isContact : depth0)) != null ? stack1.leftVoicemail : stack1), {"name":"if","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.ContactsWithLegislator : depth0)) != null ? stack1.leftVoicemail : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n                            </select>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label>Money in politics is a problem</label>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-MPP\" value=\"?\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isYesNoAnwerUndefined : depth0)) != null ? stack1.moneyInPoliticsIsAProblem : stack1), {"name":"if","hash":{},"fn":this.program(3, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " /></div>\r\n                                <div><label>?</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-MPP\" value=\"true\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.report : depth0)) != null ? stack1.isMoneyInPoliticsAProblem : stack1), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>Yes</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-MPP\" value=\"false\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isFalse : depth0)) != null ? stack1.moneyInPoliticsIsAProblem : stack1), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>No</label></div>\r\n                            </article>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label>Supports amendment to fix it</label>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-SAFI\" value=\"?\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isYesNoAnwerUndefined : depth0)) != null ? stack1.supportsAmendmentToFixIt : stack1), {"name":"if","hash":{},"fn":this.program(3, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " /></div>\r\n                                <div><label>?</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-SAFI\" value=\"true\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.report : depth0)) != null ? stack1.isSupportingAmendmentToFixIt : stack1), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>Yes</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-SAFI\" value=\"false\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isFalse : depth0)) != null ? stack1.supportsAmendmentToFixIt : stack1), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>No</label></div>\r\n                            </article>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label>Opposes Citizens United</label>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-OCU\" value=\"?\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isYesNoAnwerUndefined : depth0)) != null ? stack1.opposesCitizensUnited : stack1), {"name":"if","hash":{},"fn":this.program(3, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " /></div>\r\n                                <div><label>?</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-OCU\" value=\"true\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.report : depth0)) != null ? stack1.isOpposingCitizensUnited : stack1), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "/></div>\r\n                                <div><label>Yes</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-OCU\" value=\"false\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isFalse : depth0)) != null ? stack1.opposesCitizensUnited : stack1), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "/></div>\r\n                                <div><label>No</label></div>\r\n                            </article>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label>Supports convention process</label>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-SCP\" value=\"?\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isYesNoAnwerUndefined : depth0)) != null ? stack1.supportsConventionProcess : stack1), {"name":"if","hash":{},"fn":this.program(3, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " /></div>\r\n                                <div><label>?</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-SCP\" value=\"true\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.report : depth0)) != null ? stack1.isSupportingConventionProcess : stack1), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>Yes</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-SCP\" value=\"false\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isFalse : depth0)) != null ? stack1.supportsConventionProcess : stack1), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>No</label></div>\r\n                            </article>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label for=\"edit-support-level\">Support level</label><!--\r\n                         --><select id=\"edit-support-level\" class=\"form-control\">\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.unknown : stack1)) != null ? stack1.code : stack1), depth0))
    + "\">"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.unknown : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.primarySponsor : stack1)) != null ? stack1.code : stack1), depth0))
    + "\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isSupportLevel : depth0)) != null ? stack1.primarySponsor : stack1), {"name":"if","hash":{},"fn":this.program(7, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "                                >"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.primarySponsor : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.coSponsor : stack1)) != null ? stack1.code : stack1), depth0))
    + "\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isSupportLevel : depth0)) != null ? stack1.coSponsor : stack1), {"name":"if","hash":{},"fn":this.program(7, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "                                >"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.coSponsor : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n\r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.supportive : stack1)) != null ? stack1.code : stack1), depth0))
    + "\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isSupportLevel : depth0)) != null ? stack1.supportive : stack1), {"name":"if","hash":{},"fn":this.program(7, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "                                >"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.supportive : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n    \r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.needsConvincing : stack1)) != null ? stack1.code : stack1), depth0))
    + "\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isSupportLevel : depth0)) != null ? stack1.needsConfincing : stack1), {"name":"if","hash":{},"fn":this.program(7, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "                                >"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.needsConvincing : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n    \r\n                                <option value=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.notSupportive : stack1)) != null ? stack1.code : stack1), depth0))
    + "\"\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.isSupportLevel : depth0)) != null ? stack1.notSupportive : stack1), {"name":"if","hash":{},"fn":this.program(7, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "                                >"
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.SupportLevels : depth0)) != null ? stack1.notSupportive : stack1)) != null ? stack1.label : stack1), depth0))
    + "</option>\r\n                            </select>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label for=\"edit-notes\">Notes</label><!--\r\n                         --><textarea id=\"edit-notes\" class=\"form-control\" maxlength=\"512\">";
  stack1 = lambda(((stack1 = (depth0 != null ? depth0.report : depth0)) != null ? stack1.notes : stack1), depth0);
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</textarea>\r\n\r\n                            <p class=\"field-error\" data-check=\"max-length\">512 characters maximum</p>\r\n                        </div>\r\n                    </section>\r\n                </form>\r\n            </div>\r\n            <div class=\"modal-footer\">\r\n                <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Cancel</button>\r\n                <button type=\"button\" class=\"btn btn-primary\" data-loading-text=\"Saving report...\" id=\"confirm-edit\">Save report</button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>\r\n";
},"useData":true});

this["CBR"]["Templates"]["leadershipPositionSelect"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return "<option value=\""
    + escapeExpression(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"id","hash":{},"data":data}) : helper)))
    + "\">"
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + "</option>\r\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, buffer = "<option value=\"\">All leadership positions</option>\r\n<option disabled>----</option>\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.leadershipPositions : depth0), {"name":"each","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer;
},"useData":true});

this["CBR"]["Templates"]["stateLegislatorsResultRow"] = Handlebars.template({"1":function(depth0,helpers,partials,data,depths) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "    <tr data-id=\""
    + escapeExpression(((helper = (helper = helpers.getId || (depth0 != null ? depth0.getId : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getId","hash":{},"data":data}) : helper)))
    + "\" class=\"clickable\">\r\n        <td class=\"profile-pic\"><img src=\"http://static.votesmart.org/canphoto/"
    + escapeExpression(((helper = (helper = helpers.getId || (depth0 != null ? depth0.getId : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getId","hash":{},"data":data}) : helper)))
    + ".jpg\"></td>\r\n        <td class=\"title\">";
  stack1 = ((helper = (helper = helpers.getTitleAbbr || (depth0 != null ? depth0.getTitleAbbr : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getTitleAbbr","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n        <td class=\"name\">"
    + escapeExpression(((helper = (helper = helpers.getLastName || (depth0 != null ? depth0.getLastName : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getLastName","hash":{},"data":data}) : helper)))
    + " "
    + escapeExpression(((helper = (helper = helpers.getFirstName || (depth0 != null ? depth0.getFirstName : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getFirstName","hash":{},"data":data}) : helper)))
    + "</td>\r\n        <td class=\"political-parties\"><span class=\"centered-contents\">";
  stack1 = ((helper = (helper = helpers.getPoliticalPartiesAbbr || (depth0 != null ? depth0.getPoliticalPartiesAbbr : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getPoliticalPartiesAbbr","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</span></td>\r\n        <td class=\"district\">";
  stack1 = helpers['with'].call(depth0, (depth0 != null ? depth0.getUsState : depth0), {"name":"with","hash":{},"fn":this.program(2, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " ";
  stack1 = helpers['with'].call(depth0, (depth0 != null ? depth0.getChamber : depth0), {"name":"with","hash":{},"fn":this.program(4, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " "
    + escapeExpression(((helper = (helper = helpers.getDistrict || (depth0 != null ? depth0.getDistrict : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getDistrict","hash":{},"data":data}) : helper)))
    + "</td>\r\n        <td class=\"support-level\">";
  stack1 = ((helper = (helper = helpers.getCurrentSupportLevelSpan || (depth0 != null ? depth0.getCurrentSupportLevelSpan : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getCurrentSupportLevelSpan","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n        <td class=\"mpp\">";
  stack1 = ((helpers.getSpanForYesNoAnswerLegislatorLevel || (depth0 && depth0.getSpanForYesNoAnswerLegislatorLevel) || helperMissing).call(depth0, "MPP", depth0, {"name":"getSpanForYesNoAnswerLegislatorLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n        <td class=\"safi\">";
  stack1 = ((helpers.getSpanForYesNoAnswerLegislatorLevel || (depth0 && depth0.getSpanForYesNoAnswerLegislatorLevel) || helperMissing).call(depth0, "SAFI", depth0, {"name":"getSpanForYesNoAnswerLegislatorLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n        <td class=\"ocu\">";
  stack1 = ((helpers.getSpanForYesNoAnswerLegislatorLevel || (depth0 && depth0.getSpanForYesNoAnswerLegislatorLevel) || helperMissing).call(depth0, "OCU", depth0, {"name":"getSpanForYesNoAnswerLegislatorLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n        <td class=\"scp\">";
  stack1 = ((helpers.getSpanForYesNoAnswerLegislatorLevel || (depth0 && depth0.getSpanForYesNoAnswerLegislatorLevel) || helperMissing).call(depth0, "SCP", depth0, {"name":"getSpanForYesNoAnswerLegislatorLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n        <td class=\"is-a-priority-target\"><input type=\"checkbox\" ";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.isAPriorityTarget : depth0), {"name":"if","hash":{},"fn":this.program(6, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " ";
  stack1 = helpers.unless.call(depth0, (depths[1] != null ? depths[1].isAdmin : depths[1]), {"name":"unless","hash":{},"fn":this.program(8, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + " /></td>\r\n    </tr>\r\n";
},"2":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return escapeExpression(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"id","hash":{},"data":data}) : helper)));
  },"4":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return escapeExpression(((helper = (helper = helpers.abbr || (depth0 != null ? depth0.abbr : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"abbr","hash":{},"data":data}) : helper)));
  },"6":function(depth0,helpers,partials,data) {
  return " checked ";
  },"8":function(depth0,helpers,partials,data) {
  return " disabled ";
  },"10":function(depth0,helpers,partials,data) {
  var stack1, buffer = "";
  stack1 = helpers['with'].call(depth0, (depth0 != null ? depth0.legislator : depth0), {"name":"with","hash":{},"fn":this.program(11, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer;
},"11":function(depth0,helpers,partials,data) {
  var stack1, buffer = "";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.getReports : depth0), {"name":"if","hash":{},"fn":this.program(12, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer;
},"12":function(depth0,helpers,partials,data) {
  var stack1, buffer = "<section class=\"reports\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.getReports : depth0), {"name":"each","hash":{},"fn":this.program(13, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</section>\r\n";
},"13":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "    <article data-id=\""
    + escapeExpression(((helper = (helper = helpers.getId || (depth0 != null ? depth0.getId : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getId","hash":{},"data":data}) : helper)))
    + "\">\r\n        <div>\r\n            <span class=\"existing-report-date\">"
    + escapeExpression(((helper = (helper = helpers.getReadableCreationTimestamp || (depth0 != null ? depth0.getReadableCreationTimestamp : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getReadableCreationTimestamp","hash":{},"data":data}) : helper)))
    + "</span>\r\n            <span class=\"existing-report-contact\">"
    + escapeExpression(((helper = (helper = helpers.getReadableContact || (depth0 != null ? depth0.getReadableContact : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getReadableContact","hash":{},"data":data}) : helper)))
    + "</span>\r\n            <span class=\"existing-report-author-name\">"
    + escapeExpression(((helper = (helper = helpers.getAuthorName || (depth0 != null ? depth0.getAuthorName : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getAuthorName","hash":{},"data":data}) : helper)))
    + "</span>\r\n        </div>\r\n        <div>\r\n            ";
  stack1 = ((helper = (helper = helpers.getSupportLevelSpan || (depth0 != null ? depth0.getSupportLevelSpan : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getSupportLevelSpan","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                    <span class=\"existing-report-yes-no-answers\">";
  stack1 = ((helpers.getSpanForYesNoAnswerReportLevel || (depth0 && depth0.getSpanForYesNoAnswerReportLevel) || helperMissing).call(depth0, "MPP", depth0, {"name":"getSpanForYesNoAnswerReportLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                    ";
  stack1 = ((helpers.getSpanForYesNoAnswerReportLevel || (depth0 && depth0.getSpanForYesNoAnswerReportLevel) || helperMissing).call(depth0, "SAFI", depth0, {"name":"getSpanForYesNoAnswerReportLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                    ";
  stack1 = ((helpers.getSpanForYesNoAnswerReportLevel || (depth0 && depth0.getSpanForYesNoAnswerReportLevel) || helperMissing).call(depth0, "OCU", depth0, {"name":"getSpanForYesNoAnswerReportLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                    ";
  stack1 = ((helpers.getSpanForYesNoAnswerReportLevel || (depth0 && depth0.getSpanForYesNoAnswerReportLevel) || helperMissing).call(depth0, "SCP", depth0, {"name":"getSpanForYesNoAnswerReportLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</span>\r\n        </div>\r\n        <p>";
  stack1 = ((helper = (helper = helpers.getNotesForWeb || (depth0 != null ? depth0.getNotesForWeb : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getNotesForWeb","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</p>\r\n    </article>\r\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,depths) {
  var stack1, buffer = "<table class=\"table table-striped table-bordered table-condensed\">\r\n    <thead>\r\n    <tr>\r\n        <th class=\"profile-pic\"></th>\r\n        <th class=\"title\">Title</th>\r\n        <th class=\"name\">Name</th>\r\n        <th class=\"political-parties\">Party</th>\r\n        <th class=\"district\">District</th>\r\n        <th class=\"support-level\">Support level</th>\r\n        <th class=\"mpp\"><span class=\"yes-no-answer\">Money in<br />politics is<br />a problem</span></th>\r\n        <th class=\"safi\"><span class=\"yes-no-answer\">Supports<br />amendment<br />to fix it</span></th>\r\n        <th class=\"ocu\"><span class=\"yes-no-answer\">Opposes<br />Citizens<br />United</span></th>\r\n        <th class=\"scp\"><span class=\"yes-no-answer\">Supports<br />convention<br />process</span></th>\r\n        <th class=\"is-a-priority-target\">Target</th>\r\n    </tr>\r\n    </thead>\r\n    <tbody>\r\n";
  stack1 = helpers['with'].call(depth0, (depth0 != null ? depth0.legislator : depth0), {"name":"with","hash":{},"fn":this.program(1, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "    </tbody>\r\n</table>\r\n";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.isAdmin : depth0), {"name":"if","hash":{},"fn":this.program(10, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer;
},"useData":true,"useDepths":true});

this["CBR"]["Templates"]["stateLegislatorsResults"] = Handlebars.template({"1":function(depth0,helpers,partials,data,depths) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "<article data-id=\""
    + escapeExpression(((helper = (helper = helpers.getId || (depth0 != null ? depth0.getId : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getId","hash":{},"data":data}) : helper)))
    + "\">\r\n    <table class=\"table table-striped table-bordered table-condensed\">\r\n        <thead>\r\n        <tr>\r\n            <th class=\"profile-pic\"></th>\r\n            <th class=\"title\">Title</th>\r\n            <th class=\"name\">Name</th>\r\n            <th class=\"political-parties\">Party</th>\r\n            <th class=\"district\">District</th>\r\n            <th class=\"support-level\">Support level</th>\r\n            <th class=\"mpp\"><span class=\"yes-no-answer\">Money in<br />politics is<br />a problem</span></th>\r\n            <th class=\"safi\"><span class=\"yes-no-answer\">Supports<br />amendment<br />to fix it</span></th>\r\n            <th class=\"ocu\"><span class=\"yes-no-answer\">Opposes<br />Citizens<br />United</span></th>\r\n            <th class=\"scp\"><span class=\"yes-no-answer\">Supports<br />convention<br />process</span></th>\r\n            <th class=\"is-a-priority-target\">Target</th>\r\n        </tr>\r\n        </thead>\r\n        <tbody>\r\n        <tr data-id=\""
    + escapeExpression(((helper = (helper = helpers.getId || (depth0 != null ? depth0.getId : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getId","hash":{},"data":data}) : helper)))
    + "\" class=\"clickable\">\r\n            <td class=\"profile-pic\"><img src=\"http://static.votesmart.org/canphoto/"
    + escapeExpression(((helper = (helper = helpers.getId || (depth0 != null ? depth0.getId : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getId","hash":{},"data":data}) : helper)))
    + ".jpg\"></td>\r\n            <td class=\"title\">";
  stack1 = ((helper = (helper = helpers.getTitleAbbr || (depth0 != null ? depth0.getTitleAbbr : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getTitleAbbr","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n            <td class=\"name\">"
    + escapeExpression(((helper = (helper = helpers.getLastName || (depth0 != null ? depth0.getLastName : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getLastName","hash":{},"data":data}) : helper)))
    + " "
    + escapeExpression(((helper = (helper = helpers.getFirstName || (depth0 != null ? depth0.getFirstName : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getFirstName","hash":{},"data":data}) : helper)))
    + "</td>\r\n            <td class=\"political-parties\"><span class=\"centered-contents\">";
  stack1 = ((helper = (helper = helpers.getPoliticalPartiesAbbr || (depth0 != null ? depth0.getPoliticalPartiesAbbr : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getPoliticalPartiesAbbr","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</span></td>\r\n            <td class=\"district\">";
  stack1 = helpers['with'].call(depth0, (depth0 != null ? depth0.getUsState : depth0), {"name":"with","hash":{},"fn":this.program(2, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " ";
  stack1 = helpers['with'].call(depth0, (depth0 != null ? depth0.getChamber : depth0), {"name":"with","hash":{},"fn":this.program(4, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " "
    + escapeExpression(((helper = (helper = helpers.getDistrict || (depth0 != null ? depth0.getDistrict : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getDistrict","hash":{},"data":data}) : helper)))
    + "</td>\r\n            <td class=\"support-level\">";
  stack1 = ((helper = (helper = helpers.getCurrentSupportLevelSpan || (depth0 != null ? depth0.getCurrentSupportLevelSpan : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getCurrentSupportLevelSpan","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n            <td class=\"mpp\">";
  stack1 = ((helpers.getSpanForYesNoAnswerLegislatorLevel || (depth0 && depth0.getSpanForYesNoAnswerLegislatorLevel) || helperMissing).call(depth0, "MPP", depth0, {"name":"getSpanForYesNoAnswerLegislatorLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n            <td class=\"safi\">";
  stack1 = ((helpers.getSpanForYesNoAnswerLegislatorLevel || (depth0 && depth0.getSpanForYesNoAnswerLegislatorLevel) || helperMissing).call(depth0, "SAFI", depth0, {"name":"getSpanForYesNoAnswerLegislatorLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n            <td class=\"ocu\">";
  stack1 = ((helpers.getSpanForYesNoAnswerLegislatorLevel || (depth0 && depth0.getSpanForYesNoAnswerLegislatorLevel) || helperMissing).call(depth0, "OCU", depth0, {"name":"getSpanForYesNoAnswerLegislatorLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n            <td class=\"scp\">";
  stack1 = ((helpers.getSpanForYesNoAnswerLegislatorLevel || (depth0 && depth0.getSpanForYesNoAnswerLegislatorLevel) || helperMissing).call(depth0, "SCP", depth0, {"name":"getSpanForYesNoAnswerLegislatorLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</td>\r\n            <td class=\"is-a-priority-target\"><input type=\"checkbox\" ";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.isAPriorityTarget : depth0), {"name":"if","hash":{},"fn":this.program(6, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " ";
  stack1 = helpers.unless.call(depth0, (depths[1] != null ? depths[1].isAdmin : depths[1]), {"name":"unless","hash":{},"fn":this.program(8, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += " /></td>\r\n        </tr>\r\n        </tbody>\r\n    </table>\r\n";
  stack1 = helpers['if'].call(depth0, (depths[1] != null ? depths[1].isAdmin : depths[1]), {"name":"if","hash":{},"fn":this.program(10, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</article>\r\n";
},"2":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return escapeExpression(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"id","hash":{},"data":data}) : helper)));
  },"4":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return escapeExpression(((helper = (helper = helpers.abbr || (depth0 != null ? depth0.abbr : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"abbr","hash":{},"data":data}) : helper)));
  },"6":function(depth0,helpers,partials,data) {
  return " checked ";
  },"8":function(depth0,helpers,partials,data) {
  return " disabled ";
  },"10":function(depth0,helpers,partials,data) {
  var stack1, buffer = "";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.getReports : depth0), {"name":"if","hash":{},"fn":this.program(11, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer;
},"11":function(depth0,helpers,partials,data) {
  var stack1, buffer = "    <section class=\"reports\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.getReports : depth0), {"name":"each","hash":{},"fn":this.program(12, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "    </section>\r\n";
},"12":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "        <article data-id=\""
    + escapeExpression(((helper = (helper = helpers.getId || (depth0 != null ? depth0.getId : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getId","hash":{},"data":data}) : helper)))
    + "\">\r\n            <div>\r\n                <span class=\"existing-report-date\">"
    + escapeExpression(((helper = (helper = helpers.getReadableCreationTimestamp || (depth0 != null ? depth0.getReadableCreationTimestamp : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getReadableCreationTimestamp","hash":{},"data":data}) : helper)))
    + "</span>\r\n                <span class=\"existing-report-contact\">"
    + escapeExpression(((helper = (helper = helpers.getReadableContact || (depth0 != null ? depth0.getReadableContact : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getReadableContact","hash":{},"data":data}) : helper)))
    + "</span>\r\n                <span class=\"existing-report-author-name\">"
    + escapeExpression(((helper = (helper = helpers.getAuthorName || (depth0 != null ? depth0.getAuthorName : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getAuthorName","hash":{},"data":data}) : helper)))
    + "</span>\r\n            </div>\r\n            <div>\r\n                ";
  stack1 = ((helper = (helper = helpers.getSupportLevelSpan || (depth0 != null ? depth0.getSupportLevelSpan : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getSupportLevelSpan","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                    <span class=\"existing-report-yes-no-answers\">";
  stack1 = ((helpers.getSpanForYesNoAnswerReportLevel || (depth0 && depth0.getSpanForYesNoAnswerReportLevel) || helperMissing).call(depth0, "MPP", depth0, {"name":"getSpanForYesNoAnswerReportLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                    ";
  stack1 = ((helpers.getSpanForYesNoAnswerReportLevel || (depth0 && depth0.getSpanForYesNoAnswerReportLevel) || helperMissing).call(depth0, "SAFI", depth0, {"name":"getSpanForYesNoAnswerReportLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                    ";
  stack1 = ((helpers.getSpanForYesNoAnswerReportLevel || (depth0 && depth0.getSpanForYesNoAnswerReportLevel) || helperMissing).call(depth0, "OCU", depth0, {"name":"getSpanForYesNoAnswerReportLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                    ";
  stack1 = ((helpers.getSpanForYesNoAnswerReportLevel || (depth0 && depth0.getSpanForYesNoAnswerReportLevel) || helperMissing).call(depth0, "SCP", depth0, {"name":"getSpanForYesNoAnswerReportLevel","hash":{},"data":data}));
  if (stack1 != null) { buffer += stack1; }
  buffer += "</span>\r\n            </div>\r\n            <p>";
  stack1 = ((helper = (helper = helpers.getNotesForWeb || (depth0 != null ? depth0.getNotesForWeb : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getNotesForWeb","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</p>\r\n        </article>\r\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,depths) {
  var stack1, buffer = "";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.legislators : depth0), {"name":"each","hash":{},"fn":this.program(1, data, depths),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer;
},"useData":true,"useDepths":true});

this["CBR"]["Templates"]["whipCountListItem"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return escapeExpression(((helper = (helper = helpers.label || (depth0 != null ? depth0.label : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"label","hash":{},"data":data}) : helper)));
  },"3":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return escapeExpression(((helper = (helper = helpers.code || (depth0 != null ? depth0.code : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"code","hash":{},"data":data}) : helper)));
  },"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "<span class=\"support-level\">";
  stack1 = helpers['with'].call(depth0, (depth0 != null ? depth0.getSupportLevel : depth0), {"name":"with","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "</span>\r\n<span class=\"count\">"
    + escapeExpression(((helper = (helper = helpers.getCount || (depth0 != null ? depth0.getCount : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getCount","hash":{},"data":data}) : helper)))
    + "</span>\r\n<span class=\"percentage\">"
    + escapeExpression(((helper = (helper = helpers.getPercentage || (depth0 != null ? depth0.getPercentage : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getPercentage","hash":{},"data":data}) : helper)))
    + "%</span>\r\n<div class=\"support-level-bar-wrapper\">\r\n    <div class=\"";
  stack1 = helpers['with'].call(depth0, (depth0 != null ? depth0.getSupportLevel : depth0), {"name":"with","hash":{},"fn":this.program(3, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "\" style=\"width: "
    + escapeExpression(((helper = (helper = helpers.getPercentage || (depth0 != null ? depth0.getPercentage : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"getPercentage","hash":{},"data":data}) : helper)))
    + "%\"></div>\r\n</div>\r\n";
},"useData":true});