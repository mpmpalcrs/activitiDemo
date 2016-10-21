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
  	<input type="hidden" id="task_id" value="${taskObj.taskId}"/>
  	<input type="hidden" id="action"/>
  	<table class="mytable" width="800" border="1">
    	<tr>
    		<th width="30%">表单内容</th>
    	</tr>
    	<tr>
    		<td width="30%">
    			<#-- 表单内容 -->
    			<table width="450px" border="1" style="TABLE-LAYOUT.fixd;WORD-BREAK:break-all;">
			    	<tr id="dataTitle">
			    		<th>选择</th>
			    		<th>id</th>
			    		<th>name</th>
			    		<th>desc</th>
			    	</tr>
			    </table>
    		</td>
    	</tr>
    </table>
    <table class="mytable" width="800" border="1">
    	<tr>
    		<th width="30%">任务名</th>
    		<th width="30%">接手人</th>
    	</tr>
		<tr>
    		<td>${taskObj.taskName!''}</td>
    		<td>${taskObj.assignee!'' }</td>
    	</tr>	
    </table>
    <table class="mytable" width="800" border="1">
    	<tr>
    		<td>处理：</td>
    		<td>
    		<#list taskObj.completeEntry as completeEntry>
    			<input type="button" onclick="javascipt:completeTask('${completeEntry.action!''}')" value="${completeEntry.name!'' }"/>
    		</#list>
    		</td>
    	</tr>
    	<tr>
    		<td>接收人：</td>
    		<td>
    			<input type="text" name="nextUser" id="nextUser"/>
    		</td>
    	</tr>
    </table>
  </body>
  	<script type="text/javascript">
		function completeTask(action){
  			$.ajax({
				url : '${basePath}/report/completeTask',
				dataType : 'json',
				data : {
					"task_id" : $("#task_id").val(),
					"nextUser" : $("#nextUser").val(),
					"action" : action
				},
				success:function(obj){
					alert(obj.retObj);
					window.opener.location.href = window.opener.location.href;
					window.close();
				},
				error:function(){
				}
			});
		}
	</script>
</html>