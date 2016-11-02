<html>
  <head>
    <title>打开任务</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
		<style type="text/css">
		.mytable {
    		width : 500px;
    		align : center;
    		border : 1px solid gray;
    		cellpadding : 0;
    		cellspacing : 0;
    		border-collapse:collapse;
    	}
		.mytable tr{
    		height : 24px;
    	}
	</style>
  </head>
  <script type="text/javascript" src="${basePath}/jquery-1.9.1.min.js"></script>
  <body>
  	<form name="dataForm" action="${basePath}/report/assignTask">
  	<input type="hidden" name="taskList" id="taskList" />
  	
  	<input id="assign" type="button" value="指派人"  />
    <table class="mytable" width="800" border="1">
    	<tr>
    		<th width="30%">任务名</th>
    		<th width="30%">接手人</th>
    	</tr>
    	<#list FFTaskList as entry>
    		<tr>
	    		<td>${entry.taskName} </td>
	    		<td><input nodeList name="${entry.taskId}" value="" /></td>
	    	</tr>
    	</#list>
    </table>
  </body>
  	<script type="text/javascript">
  		$("#assign").click(function(){
  			var taskList = "";
  			$("input[nodeList]").each(function(){
  				taskList+=$(this).attr("name")+";";
  			});
  			$("#taskList").val(taskList);
			dataForm.submit();
  		});
	</script>
</html>