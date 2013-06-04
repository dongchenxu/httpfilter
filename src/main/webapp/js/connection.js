function findObj(theObj, theDoc){ 
	var p, i, foundObj; 
	if(!theDoc) theDoc = document; 
	if( (p = theObj.indexOf("?")) > 0 && parent.frames.length) {    
		theDoc = parent.frames[theObj.substring(p+1)].document;    
		theObj = theObj.substring(0,p); 
	} 
	if(!(foundObj = theDoc[theObj]) && theDoc.all) 
		foundObj = theDoc.all[theObj]; 
	for (i=0; !foundObj && i < theDoc.forms.length; i++)     
		foundObj = theDoc.forms[i][theObj]; 
	for(i=0; !foundObj && theDoc.layers && i < theDoc.layers.length; i++)     
		foundObj = findObj(theObj,theDoc.layers[i].document); 
	if(!foundObj && document.getElementById) 
		foundObj = document.getElementById(theObj);    
	return foundObj;
}

//添加一个参与人填写行
function showResponse(response){ //读取最后一行的行号，存放在txtTRLastIndex文本框中 
	paramArray = new Array;
	paramArray = params.split("&");
	//alert(paramArray);
	var responseFrame = findObj("responseFrame",document);
	var rownum = responseFrame.rows.length;
	// 删除行
	for( j = 0; j < rownum; j++){
		//alert(rownum + "==" + j);
		paramFrame.deleteRow(0);
	}
	for( i = 0; i < paramArray.length; i++){
		AddOneRow("'" + response + "'");
	}
}

function AddOneRow(content){
	var txtTRLastIndex = findObj("txtTRLastIndex",document);
	var rowID = parseInt(txtTRLastIndex.value);
	// alert(rowID);
	var responseFrame = findObj("responseFrame",document);
	// 添加行
	var newTR = responseFrame.insertRow(responseFrame.rows.length);
	newTR.id = "Response" + rowID;
		
	// 添加列:key
	var newNameTD=newTR.insertCell(0);
	// 添加列内容
	newNameTD.innerHTML = content;
}

//添加一个参与人填写行
function showParams(params){ //读取最后一行的行号，存放在txtTRLastIndex文本框中 
	paramArray = new Array;
	paramArray = params.split("&");
	//alert(paramArray);
	var paramFrame = findObj("paramFrame",document);
	var rownum = paramFrame.rows.length;
	// 删除行
	for( j = 0; j < rownum; j++){
		//alert(rownum + "==" + j);
		paramFrame.deleteRow(0);
	}
	for( i = 0; i < paramArray.length; i++){
		AddOneParamRow("'" + paramArray[i].split("=")[0] + "'", "'" + paramArray[i].split("=")[1] + "'");
	}
}

// 添加一个参数行
function AddOneParamRow(key, value){ // 读取最后一行的行号，存放在txtTRLastIndex文本框中
	var txtTRLastIndex = findObj("txtTRLastIndex",document);
	var rowID = parseInt(txtTRLastIndex.value);
	// alert(rowID);
	var paramFrame = findObj("paramFrame",document);
	// 添加行
	var newTR = paramFrame.insertRow(paramFrame.rows.length);
	newTR.id = "SignItem" + rowID;
		
	// 添加列:key
	var newNameTD=newTR.insertCell(0);
	// 添加列内容
	if(key == null){
		newNameTD.innerHTML = "<input name='txtName" + rowID + "' id='txtName" + rowID + "' type='text' value='please input key' style='color:#ff0000;font-family:黑体'></input>";
	}else{
		newNameTD.innerHTML = "<input name='txtName" + rowID + "' id='txtName" + rowID + "' type='text' value=" + key + "></input>";
	}	
	
	// 添加列:value
	var newNameTD=newTR.insertCell(1);
	// 添加列内容
	if(value == null){
		newNameTD.innerHTML = "<input name='txtName" + rowID + "' id='txtName" + rowID + "' type='text' value='please input value' style='color:#ff0000;font-family:黑体'></input>";
	}else{
		newNameTD.innerHTML = "<input name='txtName" + rowID + "' id='txtName" + rowID + "' type='text' value=" + value + "></input>";
	}
	
	// 添加列:删除按钮
	var newDeleteTD=newTR.insertCell(2);
	// 添加列内容
	newDeleteTD.innerHTML = "<div align='center' style='width:40px'><a href='javascript:;' onclick=\"DeleteSignRow('SignItem" + rowID + "')\">DEL</a></div>";
	
	// 将行号推进下一行
	txtTRLastIndex.value = (rowID + paramArray.length).toString() ;
}
//删除指定行
function DeleteSignRow(rowid){
	//alert(rowid);
	var paramFrame = findObj("paramFrame",document);
	var signItem = findObj(rowid,document);
	
	//获取将要删除的行的Index
	var rowIndex = signItem.rowIndex;
	//删除指定Index的行
	paramFrame.deleteRow(rowIndex);
	
	//重新排列序号，如果没有序号，这一步省略
	//for(i=rowIndex;i<paramFrame.rows.length;i++){
	//	paramFrame.rows[i].cells[0].innerHTML = i.toString();
	//}
}

//删除多行
function DeleteRows(rowid){
	//alert(rowid);
	var paramFrame = findObj("paramFrame",document);
	var signItem = findObj("'SignItem" + 0 +"'",document);
		
	//获取将要删除的行的Index
	var rowIndex = signItem.rowIndex;
	alert("rowIndex="+rowIndex);
	for( k = (rowIndex-1); k>paramFrame.rows.length; k++){
		alert("k=" + k);
		alert("paramFrame.rows.length" + paramFrame.rows.length);
		//删除指定Index的行
		paramFrame.deleteRow(rowIndex)
	}
	//重新排列序号，如果没有序号，这一步省略
	//for(i=rowIndex;i<paramFrame.rows.length;i++){
	//	paramFrame.rows[i].cells[0].innerHTML = i.toString();
	//}
}


var _index = 0;
function switchSysBar(){
	//alert(index);
	var reqList = document.getElementsByClassName("item");
	for(i = 0; i < reqList.length; i++){
		reqList[i].style.backgroundColor = "#FFFFFF";
	}
	reqList[_index].style.backgroundColor = "#0099FF";
	if (switchPoint.innerText==3){
		switchPoint.innerText=4
		document.all("frmTitle").style.display="none"
	}else{
		switchPoint.innerText=3
		document.all("frmTitle").style.display=""
	}
}

function showSysBar(index, params, resopnse){
	_index = index;
	//alert(params);
	var reqList = document.getElementsByClassName("item");
	for(i = 0; i < reqList.length; i++){
		reqList[i].style.backgroundColor = "#FFFFFF";
	}
	reqList[index].style.backgroundColor = "#0099FF";
	document.all("frmTitle").style.display=""
	//alert(params);
	showParams(params);
	showResponse(response);
	//alert(params);
}

function dismissSysBar(index, params, response){
	_index = index;
	//alert(index);
	var reqList = document.getElementsByClassName("item");
	for(i = 0; i < reqList.length; i++){
		reqList[i].style.backgroundColor = "#FFFFFF";
	}
	reqList[index].style.backgroundColor = "#0099FF";
	document.all("frmTitle").style.display="none"
	showParams(params);
	showResponse(response);
}

function getTab(flag) {  
	  var elList, i;
	  elList = document.getElementsByTagName("li");
	  for (i = 0; i < elList.length; i++){
		 elList[i].className ="";
	  }
	  elList[flag].className ="Selected";
	  elList[flag].blur();

	}

	function goto(flag){
		if("0"==flag){
			document.all("responseFrame").style.display="none"
			document.all("paramFrame").style.display=""
		}
		else if("1"==flag){
			document.all("paramFrame").style.display="none"
			document.all("responseFrame").style.display=""
		}
		getTab(parseInt(flag));
	}