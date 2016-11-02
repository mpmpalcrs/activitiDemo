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
    			this.completeEntry = doc.getString("completeEntry");
    			this.argname = doc.getString("argname");
    			
    			//TODO 流程节点处理方式
    			String[] completeStr = doc.getString("argvalues").split(",");
    			List<FFActionFlows> actionFlows = new ArrayList<>();
    			for (String str : completeStr) {
    				FFActionFlows actionFlow = new FFActionFlows();
    				String[] strs = str.split(":");
    				actionFlow.setName(strs[0]);
    				actionFlow.setAction(strs[1]);
    				actionFlows.add(actionFlow);
    			}
    			this.FFActionFlows = actionFlows;
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
	private String completeEntry;
	//分支判断标记
	private String argname;
    private List<FFActionFlows> FFActionFlows;


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
//    public FFTask getTaskByProcInstId(String proc_inst_Id) {
//        return new FFTask(taskService.createTaskQuery().processInstanceId(proc_inst_Id).singleResult());
//    }
//    
//    public FFTask getTaskByProcInst(FFInstance instance) {
//        return new FFTask(taskService.createTaskQuery().processInstanceId(instance.getInstanceID()).singleResult());
//    }
//    
    public List<FFTask> getTasksByProcInst(FFInstance instance) {
    	List<Task> tasks = taskService.createTaskQuery().processInstanceId(instance.getInstanceID()).list();
    	
    	List<FFTask> fftasks = new ArrayList<>();
    	
    	for (int i =0; i< tasks.size();i++){
    		fftasks.set(i, new FFTask(tasks.get(i)));
    	}
        //return new FFTask(taskService.createTaskQuery().processInstanceId(instance.getInstanceID()));
    	return fftasks;
    }
    
    public List<FFTask> getTasksByProcInstId(String instanceId) {
    	List<Task> tasks = taskService.createTaskQuery().processInstanceId(instanceId).list();
    	
    	List<FFTask> fftasks = new ArrayList<FFTask>();
    	for (int i =0; i< tasks.size();i++){
    		fftasks.add(new FFTask(tasks.get(i)));
    	}
    	return fftasks;
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
	public String getCompleteEntry() {
		return completeEntry;
	}
	public void setCompleteEntry(String completeEntry) {
		this.completeEntry = completeEntry;
	}
	public String getArgname() {
		return argname;
	}
	public void setArgname(String argname) {
		this.argname = argname;
	}
	public List<FFActionFlows> getFFActionFlows() {
		return FFActionFlows;
	}
	public void setFFActionFlows(List<FFActionFlows> fFActionFlows) {
		FFActionFlows = fFActionFlows;
	}
	
}
