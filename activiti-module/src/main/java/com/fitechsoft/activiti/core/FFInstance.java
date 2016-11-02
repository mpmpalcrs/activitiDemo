package com.fitechsoft.activiti.core;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import com.fitechsoft.activiti.domain.FDObject;

/**
 * Created by chun on 16/9/17.
 */
public class FFInstance extends FDObject {

    private ProcessInstance instance;

    @Autowired
    TaskService taskService;

    public FFInstance(ProcessInstance instance) {
        this.instance = instance;
    }

    public List<FFTask> getCurrentTasks() {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(instance.getId())
                .orderByTaskName().asc()
                .list();

        List<FFTask> ffTasks = new ArrayList<>();
        for (Task task : tasks) {
        	ffTasks.add(new FFTask(task));
		}
//        tasks.forEach((task) -> ffTasks.add(new FFTask(task)));

        return ffTasks;
    }

    public List<FFTask>  getTasksForUser(String user){
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(user)
                .orderByTaskName().asc()
                .list();

        List<FFTask> ffTasks = new ArrayList<>();
        for (Task task : tasks) {
        	ffTasks.add(new FFTask(task));
		}
//        tasks.forEach((task) -> ffTasks.add(new FFTask(task)));

        return ffTasks;
    }

	public ProcessInstance getInstance() {
		return instance;
	}

	public void setInstance(ProcessInstance instance) {
		this.instance = instance;
	}
	
	public String getInstanceID(){
		return this.getInstance().getId();
	}
}
