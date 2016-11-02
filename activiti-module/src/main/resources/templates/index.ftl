<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="${basePath}/jquery-1.9.1.min.js"></script>
<body>
<table border="1">
	<tr>
		<td>bpmn文件名称</td>
		<td>操作</td>
	</tr>
	<#if bpmnFiles?exists>
		<#list bpmnFiles as c>
			<tr>
	    		<td>${c }</td>
	    		<td><input name="createProc" type="button" value="生成流程实例" val="${c }"/></td>
	    	</tr>
		</#list>
	</#if>
</table>

<table border="1">
	<tr>
		<td>deploymentId</td>
		<td>proc_def_id</td>
		<td>bpmn名称</td>
		<td>资源</td>
		<td>操作</td>
	</tr>
	<#if processList?exists>
		<#list processList as process>
			<tr>
				<td>${process.deploymentId!'' }</td>
	    		<td>${process.processDefId!'' }</td>
	    		<td>${process.processName!''}</td>
	    		<td>${process.processResource!''}</td>
	    		<td>
	    			<input name="delProc" type="button" value="删除" val="${process.processDefId }"/>
	    			<input name="taskTodo" type="button" value="待办任务" val="${process.processDefId }"/>
	    		</td>
	    	</tr>
		</#list>
	</#if>
</table>
</body>
</html>
<script type="text/javascript">
  	 $(function(){
	  	$("input[name='createProc']").click(function(){
	  		$.ajax({
				url : '${basePath}/report/deployFlows',
				dataType : 'json',
				data : {
					"fileName" : $(this).attr("val")
				},
				success:function(obj){
					alert(obj.retObj);
					location.reload();
				},
				error:function(){
				}
			});
	  	});
	  	$("input[name='delProc']").click(function(){
	  		$.ajax({
				url : '${basePath}/report/delFlows',
				dataType : 'json',
				data : {
					"processDefId" : $(this).attr("val")
				},
				success:function(obj){
					alert(obj.retObj);
					location.reload();
				},
				error:function(){
				}
			});
	  	});
	  	
	  	$("input[name='taskTodo']").click(function(){
	  		
	  		window.location.href="${basePath}/report/listTasks/"+$(this).attr("val")
	  	});
	  });
  </script>
