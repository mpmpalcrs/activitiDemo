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
			    	<tr>
			    		<th><input type='checkBox' value='1' name='chkb'></th>
			    		<th>1</th>
			    		<th>Name1</th>
			    		<th>desc1</th>
			    	</tr>
			    	<tr>
			    		<th><input type='checkBox' value='2' name='chkb'></th>
			    		<th>2</th>
			    		<th>name2</th>
			    		<th>desc2</th>
			    	</tr>
			    	<tr>
			    		<th><input type='checkBox' value='3' name='chkb'></th>
			    		<th>3</th>
			    		<th>name3</th>
			    		<th>desc3</th>
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
    		<#list taskObj.FFActionFlows as actionFlow>
    			<input type="button" onclick="javascipt:completeTask('${actionFlow.action!''}')" value="${actionFlow.name!'' }"/>
    		</#list>
    		</td>
    	</tr>
    </table>
  </body>
  	<script type="text/javascript">
		function completeTask(action){
			var datas = "";
  			$("input[name='chkb']:checked").each(function(){
  				datas += $(this).val()+';';
  			});
  			$.ajax({
				url : '${basePath}/report/completeTask',
				dataType : 'json',
				data : {
					"task_id" : $("#task_id").val(),
					"data" : datas,
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
		};
		
		 $(document).ready(function(){
			(function(){
				$.ajax({
					url : '${basePath}/report/relationList',
					dataType : 'json',
					data : {
						"task_id":$("#task_id").val()
					},
					success:function(obj){
						$(obj).each(function(index,elem){
							$("input[name='chkb']").each(function(){
								if(elem.bussinessId==$(this).val()){
									$(this).attr("checked",true)
								}
							});
						})
					},
					error:function(){
					}
				});
			})();
		  });
	</script>
</html>