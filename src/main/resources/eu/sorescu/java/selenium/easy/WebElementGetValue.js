var jq=jQuery(arguments[0]);
var tn=jq.prop('tagName').toLowerCase();
if(tn=='select')return jq.val();
if(tn=='input')return jq.val();
if(tn=='textarea')return jq.val();
return jq.text();