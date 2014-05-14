var jq=jQuery(arguments[0]);
var tn=jq.prop('tagName').toLowerCase();
if(tn=='input'){
	if(jq[0].type=='checkbox')
		jq[0].checked=arguments[1];
	return jq.val(arguments[1]);
}
if(tn=='textarea')return jq.val(arguments[1]);
if(tn=='select'){
	var options=jq[0];
	if(typeof(arguments[1])=="number"){
		options[arguments[1]].selected=true;
		return true;
	}else{
		for(var i=0;i<options.length;i++)
			if(options[i].value==arguments[1]){
				options[i].selected=true;
				return true;
			}
		for(var i=0;i<options.length;i++)
			if(options[i].label==arguments[1]){
				options[i].selected=true;
				return true;
			}
	}
}