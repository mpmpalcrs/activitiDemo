package com.fitechsoft.activiti.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fitechsoft.activiti.core.FFInstance;
import com.fitechsoft.activiti.core.FFProcess;
import com.fitechsoft.activiti.core.FFProcessRegistry;
import com.fitechsoft.activiti.core.FFTask;
import com.fitechsoft.activiti.domain.RelationActiviti;
import com.fitechsoft.activiti.repository.RelateRepository;


@Controller
@RequestMapping(value="/report")
public class ReportController {
	@Autowired
	private FFProcessRegistry fProcessRegistry;
	@Autowired
	private FFTask fFTask;
	@Autowired
	private RelateRepository relateRepository;
	/**
	 * activiti测试页面
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index")
	public String listProcess(HttpServletRequest request, Model model) {
		fProcessRegistry.init(false);
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
	public @ResponseBody String startProcess(String processDefId, Model model,HttpServletRequest request) {
		FFProcess process = fProcessRegistry.getRegisteredProcessesById(processDefId);
        FFInstance instance = process.startInstance();
        
        JSONObject obj = new JSONObject();
		obj.put("retObj", "流程开启成功");
		return obj.toJSONString();
       
	}
	
	/**
	 * 获取待办任务，设定指派人
	 * @param instanceID
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("currentTasks")
	public String getCurrentTasks(String instanceID,Model model,HttpServletRequest request){
		//获取流程任务节点
        List<FFTask> ffTasks = fFTask.getTasksByProcInstId(instanceID);
        model.addAttribute("FFTaskList", ffTasks);
        model.addAttribute("basePath", this.basePath(request));
		return "testAct/taskAssign";
	}
	/**
	 * 任务指派人
	 * @param task_id
	 * @param user_id
	 * @return
	 */
	@RequestMapping("assignTask")
	public void assignTask(String taskList,HttpServletRequest request,HttpServletResponse response){
		String[] taskStr = taskList.split(";");
		for (String taskId : taskStr) {
			String assigneeId = request.getParameter(taskId);
			fFTask.setTaskAssignee(taskId, assigneeId);
		}
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			PrintWriter out = response.getWriter();
			out.write("人员指派成功。<br/>");
			out.write("<script>window.opener.location.href = window.opener.location.href;setTimeout(function(){window.close()},3000);</script>");
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public @ResponseBody String complete(String task_id,String action,String data) throws Exception {
		//获取当前待提交任务
		FFTask ffTask = fFTask.getTaskByTaskId(task_id);
		
		//TODO 保存流程业务关联表
		this.saveRelateActiviti(ffTask.getProcInstId(), data);
		
		//设置提交参数
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(ffTask.getArgname())){
			condition.put(ffTask.getArgname(), action);
		}
		//提交当前任务
		fFTask.complete(ffTask.getTaskId(),condition);
		
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
		JSONObject obj = new JSONObject();
		obj.put("retObj", "部署流程deploymentId:"+fProcessRegistry.createRegisteredProcesses(fileName));
		return obj.toJSONString();
	}
	/**
	 * 删除流程定义
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delFlows")
	public @ResponseBody String delFlows(String processDefId) {
		//部署完成后生成的流程编码，用于流程任务控制
		fProcessRegistry.destroyRegisteredProcessesById(processDefId);
		JSONObject obj = new JSONObject();
		obj.put("retObj", "删除流程processDefId:"+processDefId);
		return obj.toJSONString();
	}
	
	private String basePath(HttpServletRequest request){
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
		return basePath;
	}
	private void saveRelateActiviti(String procInstId, String data){
		String[] ss = data.split(";");
		List list = new ArrayList();
		for (String str : ss) {
			if(!StringUtil.isEmpty(str)){
				RelationActiviti relation = new RelationActiviti();
				relation.setProcInstId(procInstId);
				relation.setBussinessId(str);
				list.add(relation);
			}
		}
		if(list.size()>0){
			relateRepository.save(list);
		}
	}
	@RequestMapping("/relationList")
	public @ResponseBody String relationList(String task_id){
		FFTask ffTask = fFTask.getTaskByTaskId(task_id);
		Collection<RelationActiviti> relationList = relateRepository.findByProcInstId(ffTask.getProcInstId());
		return JSONObject.toJSONString(relationList);
	}
}
