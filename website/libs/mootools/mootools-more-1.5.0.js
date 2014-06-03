/*
---
MooTools: the javascript framework

web build:
 - http://mootools.net/more/307a2296984759b807e828038b51b650

packager build:
 - packager build More/String.Extras

copyrights:
  - [MooTools](http://mootools.net)

licenses:
  - [MIT License](http://mootools.net/license.txt)
...
*/

MooTools.More={version:"1.5.0",build:"73db5e24e6e9c5c87b3a27aebef2248053f7db37"};(function(){var e={a:/[àáâãäåăą]/g,A:/[ÀÁÂÃÄÅĂĄ]/g,c:/[ćčç]/g,C:/[ĆČÇ]/g,d:/[ďđ]/g,D:/[ĎÐ]/g,e:/[èéêëěę]/g,E:/[ÈÉÊËĚĘ]/g,g:/[ğ]/g,G:/[Ğ]/g,i:/[ìíîï]/g,I:/[ÌÍÎÏ]/g,l:/[ĺľł]/g,L:/[ĹĽŁ]/g,n:/[ñňń]/g,N:/[ÑŇŃ]/g,o:/[òóôõöøő]/g,O:/[ÒÓÔÕÖØ]/g,r:/[řŕ]/g,R:/[ŘŔ]/g,s:/[ššş]/g,S:/[ŠŞŚ]/g,t:/[ťţ]/g,T:/[ŤŢ]/g,u:/[ùúûůüµ]/g,U:/[ÙÚÛŮÜ]/g,y:/[ÿý]/g,Y:/[ŸÝ]/g,z:/[žźż]/g,Z:/[ŽŹŻ]/g,th:/[þ]/g,TH:/[Þ]/g,dh:/[ð]/g,DH:/[Ð]/g,ss:/[ß]/g,oe:/[œ]/g,OE:/[Œ]/g,ae:/[æ]/g,AE:/[Æ]/g},d={" ":/[\xa0\u2002\u2003\u2009]/g,"*":/[\xb7]/g,"'":/[\u2018\u2019]/g,'"':/[\u201c\u201d]/g,"...":/[\u2026]/g,"-":/[\u2013]/g,"&raquo;":/[\uFFFD]/g},c={ms:1,s:1000,m:60000,h:3600000},b=/(\d*.?\d+)([msh]+)/;
var a=function(h,j){var g=h,i;for(i in j){g=g.replace(j[i],i);}return g;};var f=function(g,i){g=g||"";var j=i?"<"+g+"(?!\\w)[^>]*>([\\s\\S]*?)</"+g+"(?!\\w)>":"</?"+g+"([^>]+)?>",h=new RegExp(j,"gi");
return h;};String.implement({standardize:function(){return a(this,e);},repeat:function(g){return new Array(g+1).join(this);},pad:function(g,j,i){if(this.length>=g){return this;
}var h=(j==null?" ":""+j).repeat(g-this.length).substr(0,g-this.length);if(!i||i=="right"){return this+h;}if(i=="left"){return h+this;}return h.substr(0,(h.length/2).floor())+this+h.substr(0,(h.length/2).ceil());
},getTags:function(g,h){return this.match(f(g,h))||[];},stripTags:function(g,h){return this.replace(f(g,h),"");},tidy:function(){return a(this,d);},truncate:function(g,h,k){var j=this;
if(h==null&&arguments.length==1){h="…";}if(j.length>g){j=j.substring(0,g);if(k){var i=j.lastIndexOf(k);if(i!=-1){j=j.substr(0,i);}}if(h){j+=h;}}return j;
},ms:function(){var g=b.exec(this);if(g==null){return Number(this);}return Number(g[1])*c[g[2]];}});})();