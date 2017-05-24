(function(){
    var REGEXP_CACHE = {};
    jQuery.expr[":"].regexp= function (obj, index, meta, stack) {
        var expression = meta[3];
        var regexp = REGEXP_CACHE[expression] = REGEXP_CACHE[expression] || new RegExp("^" + expression + "$", "g");
        var jElem = jQuery(obj);
        var result=((regexp.test((String(jElem.text())).trim()))
            || (regexp.test((String(jElem.val())).trim()))
            || (regexp.test((String(jElem.attr("placeholder"))).trim())));
        return result;
    };
})();

sQuery=function() {
    var configuration={};
    configuration.segmentSeparator='->';
    configuration.interpolatorSeparator='`';

    function keepUniqueDisjunct(nodes){
        nodes=Array.apply(null,nodes);
        for(var i=0;i+1<nodes.length;i++){
            if(nodes[i]==null)continue;
            for(var j=i+1;j<nodes.length;j++){
                if((nodes[j]==null)||(nodes[i]==nodes[j])||(nodes[i].contains(nodes[j]))){
                    nodes[j]=null;
                    continue;
                }
                if(nodes[j].contains(nodes[i])){
                    nodes[i]=null;
                    break;
                }
            }
        }
        return nodes.filter(it=>it!=null);
    }

    function bySelector(selector, parentNode) {
        var nodes = jQuery(selector, parentNode).toArray();
        var parents = [], leaves = [], nodeIdx;
        for (nodeIdx = 0; nodeIdx < nodes.length; nodeIdx++)
            parents.push(nodes[nodeIdx].parentNode);
        for (nodeIdx = 0; nodeIdx < nodes.length; nodeIdx++)
            if (parents.indexOf(nodes[nodeIdx]) < 0)
                leaves.push(nodes[nodeIdx]);
        return leaves;
    }

    function bySelectorPath() {
        var currentNodes = [null];
        for (var i = 0; i < arguments.length; i++) {
            var newNodes = [];
            for(;;){
                for (var currentNodeIdx = 0; currentNodeIdx < currentNodes.length; currentNodeIdx++)
                    newNodes = newNodes.concat(bySelector(arguments[i], currentNodes[currentNodeIdx]));
                newNodes=keepUniqueDisjunct(newNodes);
                if(newNodes.length)break;
                currentNodes=keepUniqueDisjunct(currentNodes);
                for (var currentNodeIdx = 0; currentNodeIdx < currentNodes.length; currentNodeIdx++)
                    currentNodes[currentNodeIdx]=currentNodes[currentNodeIdx].parentNode;
                currentNodes=keepUniqueDisjunct(currentNodes);
            }
            currentNodes = newNodes;
        }
        return currentNodes;
    }

    function bySorescuSelector(sorescuSelector) {
        var cssSelector = String(sorescuSelector).split(configuration.interpolatorSeparator);
        var segments = [];
        for (var segmentIdx = 0; segmentIdx < cssSelector.length; segmentIdx++)
            if (!segmentIdx)
                segments.push(cssSelector[segmentIdx]);
            else if (segmentIdx % 2)
                segments.push(":regexp('" + cssSelector[segmentIdx]);
            else segments.push("'):visible" + cssSelector[segmentIdx]);
        cssSelector = segments.join('');
        return bySelectorPath.apply(null, cssSelector.split(configuration.segmentSeparator));
    }

    if(arguments.length==1)
        return new sQuery(bySorescuSelector(arguments[0]),arguments[0]);

    this._array=arguments[0];
    this._query=arguments[1];
    this.length=arguments[0].length;
    for(var i=0;i<this.length;i++)
        this[i]=arguments[0][i];

    this.toArray=function(){return Array.apply(null,this._array);}

    this.assertUnique=function(){
        if(this.length==0)throw new Error("No element found for `"+this._query+"`.");
        if(this.length!=1)throw new Error("More elements found for `"+this._query+"`.");
        return this;
    }

    this.getValue=function(){
        var jq=jQuery(this[0]);
        var tn=jq.prop('tagName').toLowerCase();
        if(tn=='select')return jq.val();
        if(tn=='input'){
            if(jq[0].type=='checkbox')return jq[0].checked;
            return jq.val();
        }
        if(tn=='textarea')return jq.val();
        return jq.text();
    }
    this.setValue=function(value){
        var jq=jQuery(this[0]);
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
        throw new Error("Setter not supported for "+tn+" nodes.");
    }
    return this;
};