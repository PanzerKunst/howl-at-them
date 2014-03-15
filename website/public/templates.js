this["CBR"] = this["CBR"] || {};
this["CBR"]["Templates"] = this["CBR"]["Templates"] || {};

this["CBR"]["Templates"]["deleteReportModal"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"modal fade\" id=\"delete-report-modal\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\r\n    <div class=\"modal-dialog modal-sm\">\r\n        <div class=\"modal-content\">\r\n            <div class=\"modal-body\">\r\n                <p>Delete this report?</p>\r\n            </div>\r\n            <div class=\"modal-footer\">\r\n                <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Cancel</button>\r\n                <button type=\"button\" class=\"btn btn-primary\" data-loading-text=\"Deleting report...\" id=\"confirm-delete\">Delete report</button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>\r\n";
  });

this["CBR"]["Templates"]["editReportModal"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  
  return " selected ";
  }

function program3(depth0,data) {
  
  
  return " checked ";
  }

function program5(depth0,data) {
  
  
  return "\r\n                                    checked\r\n                                    ";
  }

function program7(depth0,data) {
  
  
  return "\r\n                                selected\r\n                                ";
  }

  buffer += "<div class=\"modal fade\" id=\"edit-report-modal\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\r\n    <div class=\"modal-dialog\">\r\n        <div class=\"modal-content\">\r\n            <div class=\"modal-body\">\r\n                <form>\r\n                    <section class=\"form-groups\">\r\n                        <div class=\"form-group\">\r\n                            <label for=\"edit-author-name\">Your name <span>*</span></label><!--\r\n                         --><input type=\"text\" id=\"edit-author-name\" class=\"form-control\" maxlength=\"64\" data-min-length=\"2\" value=\""
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.report)),stack1 == null || stack1 === false ? stack1 : stack1.authorName)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\" />\r\n\r\n                            <p class=\"field-error\" data-check=\"empty\"></p>\r\n                            <p class=\"field-error\" data-check=\"min-length\">Two characters minimun</p>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label for=\"edit-contact\">Contact</label><!--\r\n                         --><select id=\"edit-contact\" class=\"form-control\">\r\n                                <option value=\"MET_LEGISLATOR\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isContact)),stack1 == null || stack1 === false ? stack1 : stack1.metLegislator), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.ContactWithLegislator)),stack1 == null || stack1 === false ? stack1 : stack1.MET_LEGISLATOR)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n                                <option value=\"TALKED_TO_LEGISLATOR\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isContact)),stack1 == null || stack1 === false ? stack1 : stack1.talkedToLegislator), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.ContactWithLegislator)),stack1 == null || stack1 === false ? stack1 : stack1.TALKED_TO_LEGISLATOR)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n                                <option value=\"CONTACT_WITH_STAFF\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isContact)),stack1 == null || stack1 === false ? stack1 : stack1.contactWithStaff), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.ContactWithLegislator)),stack1 == null || stack1 === false ? stack1 : stack1.CONTACT_WITH_STAFF)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n                                <option value=\"WAITING_FOR_CALLBACK\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isContact)),stack1 == null || stack1 === false ? stack1 : stack1.waitingForCallback), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.ContactWithLegislator)),stack1 == null || stack1 === false ? stack1 : stack1.WAITING_FOR_CALLBACK)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n                                <option value=\"LEFT_VOICEMAIL\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isContact)),stack1 == null || stack1 === false ? stack1 : stack1.leftVoiceMail), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.ContactWithLegislator)),stack1 == null || stack1 === false ? stack1 : stack1.LEFT_VOICEMAIL)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n                                <option value=\"NONE\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isContact)),stack1 == null || stack1 === false ? stack1 : stack1.none), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += ">"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.ContactWithLegislator)),stack1 == null || stack1 === false ? stack1 : stack1.NONE)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n                            </select>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label>Money in politics is a problem</label>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-MPP\" value=\"?\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isYesNoAnwerUndefined)),stack1 == null || stack1 === false ? stack1 : stack1.moneyInPoliticsAProblemUndefined), {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += " /></div>\r\n                                <div><label>?</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-MPP\" value=\"true\"\r\n                                    ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.report)),stack1 == null || stack1 === false ? stack1 : stack1.isMoneyInPoliticsAProblem), {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>Yes</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-MPP\" value=\"false\"\r\n                                    ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isFalse)),stack1 == null || stack1 === false ? stack1 : stack1.moneyInPoliticsAProblem), {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>No</label></div>\r\n                            </article>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label>Supports amendment to fix it</label>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-SAFI\" value=\"?\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isYesNoAnwerUndefined)),stack1 == null || stack1 === false ? stack1 : stack1.supportingAmendmentToFixIt), {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += " /></div>\r\n                                <div><label>?</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-SAFI\" value=\"true\"\r\n                                    ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.report)),stack1 == null || stack1 === false ? stack1 : stack1.isSupportingAmendmentToFixIt), {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>Yes</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-SAFI\" value=\"false\"\r\n                                    ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isFalse)),stack1 == null || stack1 === false ? stack1 : stack1.supportingAmendmentToFixIt), {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>No</label></div>\r\n                            </article>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label>Opposes Citizens United</label>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-OCU\" value=\"?\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isYesNoAnwerUndefined)),stack1 == null || stack1 === false ? stack1 : stack1.opposingCitizensUnited), {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += " /></div>\r\n                                <div><label>?</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-OCU\" value=\"true\"\r\n                                    ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.report)),stack1 == null || stack1 === false ? stack1 : stack1.isOpposingCitizensUnited), {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "/></div>\r\n                                <div><label>Yes</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-OCU\" value=\"false\"\r\n                                    ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isFalse)),stack1 == null || stack1 === false ? stack1 : stack1.opposingCitizensUnited), {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "/></div>\r\n                                <div><label>No</label></div>\r\n                            </article>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label>Previous vote for convention</label>\r\n                            <article class=\"check-or-radio\">\r\n                                <div><input type=\"radio\" name=\"edit-PVC\" value=\"?\" ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isYesNoAnwerUndefined)),stack1 == null || stack1 === false ? stack1 : stack1.previousVoteForConvention), {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += " /></div>\r\n                                <div><label>?</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-PVC\" value=\"true\"\r\n                                    ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.report)),stack1 == null || stack1 === false ? stack1 : stack1.hasPreviouslyVotedForConvention), {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>Yes</label></div>\r\n                            </article>\r\n                            <article class=\"check-or-radio\">\r\n                                <div>\r\n                                    <input type=\"radio\" name=\"edit-PVC\" value=\"false\"\r\n                                    ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isFalse)),stack1 == null || stack1 === false ? stack1 : stack1.previousVoteForConvention), {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "/>\r\n                                </div>\r\n                                <div><label>No</label></div>\r\n                            </article>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label for=\"edit-support-level\">Support level</label><!--\r\n                         --><select id=\"edit-support-level\" class=\"form-control\">\r\n                                <option value=\"\"></option>\r\n    \r\n                                <option value=\"SUPPORTIVE\"\r\n                                ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isSupportLevel)),stack1 == null || stack1 === false ? stack1 : stack1.supportive), {hash:{},inverse:self.noop,fn:self.program(7, program7, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n                                >"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.SupportLevel)),stack1 == null || stack1 === false ? stack1 : stack1.SUPPORTIVE)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n    \r\n                                <option value=\"NEEDS_CONVINCING\"\r\n                                ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isSupportLevel)),stack1 == null || stack1 === false ? stack1 : stack1.needsConfincing), {hash:{},inverse:self.noop,fn:self.program(7, program7, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n                                >"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.SupportLevel)),stack1 == null || stack1 === false ? stack1 : stack1.NEEDS_CONVINCING)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n    \r\n                                <option value=\"NOT_SUPPORTIVE\"\r\n                                ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 && depth0.isSupportLevel)),stack1 == null || stack1 === false ? stack1 : stack1.notSupportive), {hash:{},inverse:self.noop,fn:self.program(7, program7, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n                                >"
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.SupportLevel)),stack1 == null || stack1 === false ? stack1 : stack1.NOT_SUPPORTIVE)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</option>\r\n                            </select>\r\n                        </div>\r\n\r\n                        <div class=\"form-group\">\r\n                            <label for=\"edit-notes\">Notes</label><!--\r\n                         --><textarea id=\"edit-notes\" class=\"form-control\" maxlength=\"512\">";
  stack1 = ((stack1 = ((stack1 = (depth0 && depth0.report)),stack1 == null || stack1 === false ? stack1 : stack1.notes)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</textarea>\r\n\r\n                            <p class=\"field-error\" data-check=\"max-length\">512 characters maximum</p>\r\n                        </div>\r\n                    </section>\r\n                </form>\r\n            </div>\r\n            <div class=\"modal-footer\">\r\n                <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Cancel</button>\r\n                <button type=\"button\" class=\"btn btn-primary\" data-loading-text=\"Saving report...\" id=\"confirm-edit\">Save report</button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>\r\n";
  return buffer;
  });