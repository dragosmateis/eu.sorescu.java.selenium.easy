var element=arguments[0];
var expression=arguments[1];
var lambda=function(expression){return eval(expression);};
return lambda.call(element,expression);