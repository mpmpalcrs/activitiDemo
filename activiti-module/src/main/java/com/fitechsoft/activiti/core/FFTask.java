package com.fitechsoft.activiti.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fitechsoft.activiti.domain.ActionFlows;
import com.fitechsoft.activiti.domain.FDObject;

/**
 * Created by chun on 16/9/17.
 */
@Service
public class FFTask extends FDObject {

    @Autowired
    private TaskService taskService;
    
    private Task task;
    
    public Task getTask() {
        return task;
    }
    public FFTask(Task task) {
    	if(null != task){
    		this.procInstId = task.getProcessInstanceId();
        	this.taskId = task.getId();
        	this.taskName = task.getName();
        	this.assingedTime = task.getCreateTime();
        	this.assignee = task.getAssignee();
            this.task = task;
            
            String dest = (String)task.getDescription();
    		if(!StringUtil.isEmpty(dest)){
    			JSONObject doc = JSONObject.parseObject(dest);
    			this.handleEntry = doc.getString("handleEntry");
    			//TODO 流程节点处理方式需修改======================
    			String[] completeStr = doc.getString("completeEntry").split(",");
    			List<ActionFlows> actionFlows = new ArrayList<>();
    			for (String str : completeStr) {
    				ActionFlows actionFlow = new ActionFlows();
    				String[] strs = str.split(":");
    				actionFlow.setName(strs[0]);
    				actionFlow.setAction(strs[1]);
    				actionFlows.add(actionFlow);
    			}
    			this.completeEntry = actionFlows;
    		}
    	}
    }
    public FFTask() {
		super();
	}
    
    //流程实例ID
    private String procInstId;
    //任务ID
  	private String taskId;
  	//任务名称
  	private String taskName;
  	//分配时间（任务开始时间）
  	private Date assingedTime;
  	//指派人
  	private String assignee;
  	
	private String handleEntry;
    private List<ActionFlows> completeEntry;


    public List<FFTask> getToDoTasksForUser(String proc_def_id_, String user_Id) {
    	List<Task> tasks = new ArrayList<>();
    	if(StringUtil.isEmpty(proc_def_id_)&&StringUtil.isEmpty(user_Id)){					//查询所有任务列表
    		tasks = taskService.createTaskQuery().list();
    		
    	}else if(!StringUtil.isEmpty(proc_def_id_)&&StringUtil.isEmpty(user_Id)){			//查询指定流程定义ID的任务列表
    		tasks = taskService.createTaskQuery().processDefinitionId(proc_def_id_).orderByTaskCreateTime().desc().list();
    		
    	}else if(!StringUtil.isEmpty(proc_def_id_)&&!StringUtil.isEmpty(user_Id)){		//查询指定流程定义ID和用户的任务列表
    		tasks = taskService.createTaskQuery().processDefinitionId(proc_def_id_).taskCandidateOrAssigned(user_Id).list();
    		
    	}else if(StringUtil.isEmpty(proc_def_id_)&&!StringUtil.isEmpty(user_Id)){
    		tasks = taskService.createTaskQuery().taskCandidateOrAssigned(user_Id).list();
    	}
        List<FFTask> ffTasks = new ArrayList<>();
        for (Task task : tasks) {
        	ffTasks.add(new FFTask(task));
		}
        return ffTasks;
    }

    public FFTask getTask(String task){
        return new FFTask(taskService.createTaskQuery().taskDefinitionKey(task).list().get(0));
    }
    public FFTask getTaskByTaskId(String taskId) {
        return new FFTask(taskService.createTaskQuery().taskId(taskId).singleResult());
    }
    public FFTask getTaskByProcInstId(String proc_inst_Id) {
        return new FFTask(taskService.createTaskQuery().processInstanceId(proc_inst_Id).singleResult());
    }
    
    public void setTaskAssignee(String taskId, String assigneeId) {
        taskService.claim(taskId, assigneeId);
    }

    public void complete(String taskId,Map<String, Object> condition){
        taskService.complete(taskId,condition);
    }
	public TaskService getTaskService() {
		return taskService;
	}
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public Date getAssingedTime() {
		return assingedTime;
	}
	public void setAssingedTime(Date assingedTime) {
		this.assingedTime = assingedTime;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public String getHandleEntry() {
		return handleEntry;
	}
	public void setHandleEntry(String handleEntry) {
		this.handleEntry = handleEntry;
	}
	public List<ActionFlows> getCompleteEntry() {
		return completeEntry;
	}
	public void setCompleteEntry(List<ActionFlows> completeEntry) {
		this.completeEntry = completeEntry;
	}
}
