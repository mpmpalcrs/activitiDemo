package com.fitechsoft.activiti.core;

import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitechsoft.activiti.domain.FDObject;

/**
 * Created by chun on 16/9/17.
 */
@Service
public class FFProcess extends FDObject {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
    RepositoryService repositoryService;
//    @Autowired
    RuntimeService runtimeService;

    private String processResource;
    private String processName;
    private String deploymentId;
    private String processDefId;
    private String category;

    public FFProcess(String deploymentId,String processDefId, String processName, String processResource, String category) {
        this.processName = processName;
        this.processResource = processResource;
        this.category = category;
        this.deploymentId = deploymentId;
        this.processDefId = processDefId;
    }

	public FFProcess() {
		super();
	}

	public String getProcessResource() {
        return processResource;
    }

    public void setProcessResource(String processResource) {
        this.processResource = processResource;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}

	/**
	 * 部署流程定义
	 */
	public String deploy() {
		return this.deploymentId = repositoryService.createDeployment()
                .addClasspathResource(this.getProcessResource())
                .category(getCategory())
                .deploy()
                .getId();
    }
	/**
	 * 删除流程定义
	 * @param proc_def_id
	 */
	public void delProcDeployment() {
		repositoryService.deleteDeployment(this.deploymentId,true);
	}
    public FFInstance startInstance(Map variables) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(this.getProcessName(), variables);
        logger.debug("发起了一个流程实例，processInstanceId = " + instance.getProcessInstanceId());
        return new FFInstance(instance);
    }
    
    public FFInstance startInstance() {
        ProcessInstance instance = runtimeService.startProcessInstanceById(this.getProcessDefId());
        logger.debug("发起了一个流程实例，processInstanceId = " + instance.getProcessInstanceId());
        return new FFInstance(instance);
    }


    public ProcessDefinition getProcessDefinition() {
        return repositoryService.getProcessDefinition(processName);
    }
    
    public List<ProcessInstance> getInstances() {
        return runtimeService.createProcessInstanceQuery().processDefinitionId(this.getDeploymentId()).list();
    }

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
}
