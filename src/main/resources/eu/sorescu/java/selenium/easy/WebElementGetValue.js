var jq=jQuery(arguments[0]);
var tn=jq.prop('tagName').toLowerCase();
if(tn=='select')return jq.val();
if(tn=='input'){
	if(jq[0].type=='checkbox')
		return jq[0].checked;
	return jq.val();
}
if(tn=='textarea')return jq.val();
return jq.text();