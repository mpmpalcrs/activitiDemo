package com.fitechsoft.activiti.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chun on 2016/10/7.
 */
@Service
public class FFProcessRegistry {
	@Autowired
	RepositoryService repositoryService;
	@Autowired
    RuntimeService runtimeService;
	@Autowired
	FFProcess fFProcess;
	private Map<String, FFProcess> processMap;
	
//    private static FFProcessRegistry registry;
//    static {
//        registry = new FFProcessRegistry();
//    }
//
//    protected FFProcessRegistry() {
//        super();
//        processMap = new HashMap<>();
//    }
//
//    public static FFProcessRegistry getProcessRegistry() {
//        return registry;
//    }
	
	
    public void init(boolean bool) {
    	if(bool){
    		List<FFProcess> processList = this.listProcessDefinition();
    		processMap = new HashMap<String, FFProcess>();
        	for (FFProcess ffProcess : processList) {
        		//TODO 暂时强行注入runtimeService
        		ffProcess.setRuntimeService(runtimeService);
        		this.processMap.put(ffProcess.getProcessDefId(), ffProcess);
    		}
    	}else{
    		if(null == processMap){
    			List<FFProcess> processList = this.listProcessDefinition();
        		processMap = new HashMap<String, FFProcess>();
            	for (FFProcess ffProcess : processList) {
            		ffProcess.setRuntimeService(runtimeService);
            		this.processMap.put(ffProcess.getProcessDefId(), ffProcess);
        		}
    		}
    	}
    }
    //TODO 页面展示所有流程定义使用
    public List<FFProcess> listProcessDefinition() {
    	List<FFProcess> processList = new ArrayList<>();
    	List<ProcessDefinition> procDefList = repositoryService.createProcessDefinitionQuery().list();
    	for (ProcessDefinition processDefinition : procDefList) {
    		String deploymentId = processDefinition.getDeploymentId();
    		String procDefId = processDefinition.getId();
    		String processName = processDefinition.getName();
    		String resourceName = processDefinition.getResourceName();
    		String category = processDefinition.getCategory();
    		FFProcess process = new FFProcess(deploymentId,procDefId,processName,resourceName,category);
    		processList.add(process);
    	}
		return processList;
	}
    
    
    public List<FFProcess> getRegisteredProcessesByCategory(String category) {
        if (null == category) {
            return null;
        }

        List<FFProcess> processes = new ArrayList<>();
        for (Map.Entry<String, FFProcess> entry : processMap.entrySet()) {
            FFProcess value = entry.getValue();

            if (category.equals(value.getCategory())) {
                processes.add(value);
            }

        }

        return processes;
    }

    public List<FFProcess> getRegisteredProcessesByName(String name) {
        if (null == name) {
            return null;
        }
        List<FFProcess> processes = new ArrayList<>();
        for (Map.Entry<String, FFProcess> entry : processMap.entrySet()) {
        	FFProcess value = entry.getValue();
        	
            if (name.equals(value.getProcessName())) {
            	processes.add(value);
            }
        }

        return processes;
    }
    public FFProcess getRegisteredProcessesById(String procDefId) {
    	this.init(false);
        return processMap.get(procDefId);
    }
    
    /**
     * 删除流程定义
     * @param procssDefId
     * @return
     */
    public FFProcess destroyRegisteredProcessesById(String procssDefId) {
    	FFProcess ffProcess =processMap.get(procssDefId);
    	ffProcess.setRepositoryService(repositoryService);
    	ffProcess.delProcDeployment();
		//删除内存中的流程定义
    	this.processMap.remove(procssDefId);
    	return ffProcess;
    }
    /**
     * 创建流程定义
     * @param fileName
     * @return
     */
    public String createRegisteredProcesses(String fileName){
    	fFProcess.setRepositoryService(repositoryService);
    	fFProcess.setProcessName(fileName);
		fFProcess.setProcessResource("bpmn/"+fileName);
		String deploymentId = fFProcess.deploy();
		init(true);
		return deploymentId;
    }

    /**
     * 获得pbmn资源路径
     * @param bpmnPath
     * @return
     */
    public List<String> listBpmnFiles(String bpmnPath) {
		List<String> bpmnFiles= new ArrayList<String>();
		try {
			String filePath = getClass().getResource("/"+bpmnPath).getFile().toString();
			File file = new File(filePath);
			if(file.exists()){
				File[] files = file.listFiles();
				for (File file2 : files) {
					bpmnFiles.add(file2.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bpmnFiles;
	}
}
