function removeWhitespace(xml) {
	var loopIndex;

	for (loopIndex = 0; loopIndex < xml.childNodes.length; loopIndex++) {
		var currentNode = xml.childNodes[loopIndex];
		if (currentNode.nodeType == 1) {
			debugger;
			var elecss = null;
			if (window.getComputedStyle) {
				elecss = window.getComputedStyle(currentNode, null);
			} else {
				elecss = currentNode.currentStyle;
			}
			var elecssJson = $.toJSON(elecss);
			// http://localhost:8080/httpfilter/dctest.do
			// $.ajax({
			// type: ¡°POST¡±,
			// dataType: ¡°JSONP¡±,
			// data:{css : elecssJson}
			// url: "http://item.taobao.com/__httpfilter_kuaiyu.do",
			// async: true,
			// success: function(){}});

			 $.post(
			 "http://localhost:8080/httpfilter/dctest.do", {
			 css : elecssJson,
			 itemId : "123456",
			 element:"leftelement"
			 }, function(data){
			 });

			removeWhitespace(currentNode);
		}

		if (((/^\s+$/.test(currentNode.nodeValue)))
				&& (currentNode.nodeType == 3)) {
			xml.removeChild(xml.childNodes[loopIndex--]);
		}
	}
}

window.onload = function() {
	removeWhitespace(document.getElementsByClassName("col-sub J_TRegion")[0]);
}