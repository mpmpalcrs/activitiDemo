package com.fitechsoft.activiti.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fitechsoft.activiti.core.FFInstance;
import com.fitechsoft.activiti.core.FFProcess;
import com.fitechsoft.activiti.core.FFProcessRegistry;
import com.fitechsoft.activiti.core.FFTask;


@Controller
@RequestMapping(value="/report")
public class ReportController {
	@Autowired
	private FFProcess fFProcess;
	@Autowired
	private FFProcessRegistry fProcessRegistry;
	@Autowired
	private FFTask fFTask;
	/**
	 * activiti测试页面
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index")
	public String listProcess(HttpServletRequest request, Model model) {
		fProcessRegistry.register();
		List<FFProcess> processList = fProcessRegistry.listProcessDefinition();
		model.addAttribute("processList", processList);
		//流程模型管理集合
		model.addAttribute("bpmnFiles", fProcessRegistry.listBpmnFiles("bpmn"));
		model.addAttribute("basePath", this.basePath(request));
		return "index";
	}

    /**
     * 查询指定用户的任务列表
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/listTasks/{processDefId}")
    public String listTasks(@PathVariable("processDefId") String processDefId,Model model,HttpServletRequest request) {
    	String userId = "";
        // 待办列表
        List<FFTask> todoList = fFTask.getToDoTasksForUser(processDefId, userId);

        model.addAttribute("toDoList", todoList);
        model.addAttribute("basePath", this.basePath(request));
        return "testAct/taskList";
    }


	/**
	 * 开启并指派一个新的任务
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/startProcess")
	public @ResponseBody String startProcess(String processDefId,Model model,HttpServletRequest request) {
		//指定第一个任务处理人
		String assigneeId = "user1";
		
        FFInstance instance = fFProcess.startInstance(processDefId);
        //获取开启流程的第一个任务节点
        FFTask ffTask = fFTask.getTaskByProcInstId(instance.getInstance().getProcessInstanceId());
        //指定 指派人
        fFTask.setTaskAssignee(ffTask.getTaskId(),assigneeId);
        
        JSONObject obj = new JSONObject();
		obj.put("retObj", "流程开启成功");
		return obj.toJSONString();
	}
	
	/**
	 * 执行任务流程节点
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/handleTask")
	public String handleTask(String task_id,Model model,HttpServletRequest request) {
		FFTask ffTask = fFTask.getTaskByTaskId(task_id);
		
		model.addAttribute("taskObj", ffTask);
		model.addAttribute("basePath", this.basePath(request));
		//跳转的业务功能
        return ffTask.getHandleEntry();

	}
	/**
	 * 提交任务流程节点
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/completeTask", method = { RequestMethod.POST,RequestMethod.GET })
	public @ResponseBody String complete(String task_id,String nextUser,String action) throws Exception {
		//设置提交参数
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("input", action);
		//获取当前待提交任务
		FFTask ffTask = fFTask.getTaskByTaskId(task_id);
		//提交当前任务
		fFTask.complete(ffTask.getTaskId(),condition);
		//获取下一待办任务，用于指派人
		ffTask = fFTask.getTaskByProcInstId(ffTask.getProcInstId());
		if(null!=ffTask && null != ffTask.getTaskId()){
			//指定 指派人
	        fFTask.setTaskAssignee(ffTask.getTaskId(),nextUser);
		}
        
        JSONObject obj = new JSONObject();
		obj.put("retObj", "任务提交成功！");
		return obj.toJSONString();
	}
	/**
	 * 流程部署
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deployFlows")
	public @ResponseBody String deployFlows(String fileName) {
		fFProcess.setProcessName(fileName);
		fFProcess.setProcessResource("bpmn/"+fileName);
		fFProcess.deploy();
		JSONObject obj = new JSONObject();
		obj.put("retObj", "部署流程deploymentId:"+fFProcess.getDeploymentId());
		return obj.toJSONString();
	}
	/**
	 * 删除流程定义
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delFlows")
	public @ResponseBody String delFlows(String deploymentId) {
		//部署完成后生成的流程编码，用于流程任务控制
		fFProcess.delProcDeployment(deploymentId);
		JSONObject obj = new JSONObject();
		obj.put("retObj", "删除流程deploymentId:"+deploymentId);
		return obj.toJSONString();
	}
	
	private String basePath(HttpServletRequest request){
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
		return basePath;
	}
	
}
