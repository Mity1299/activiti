package com.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.task.Task;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * 用于演示Activiti的首例程序
 * 学生请假
 * -》班主任查看
 * -》班主任审核
 * -》教务处审核
 */
public class ActivitiTest {

    /**
     * 1. 部署一个Activity流程
     * 着重查看数据库表
     */
    @Ignore
    public void init(){
        //获得之前的流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    }


    @Ignore
    public void createActivitiTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //查看act_ge_bytearray
        processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("shenqing.bpmn")//访问resources路径
                .addClasspathResource("shenqing.png")
                .deploy();

    }

    /**
     * 2.启动流程实例
     * //这个是查看数据库中act_re_procdef表
     */
    @Test
    public void testStartProcessInstance(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        processEngine.getRuntimeService()
                .startProcessInstanceById("shenqing:1:4");
    }


    @Ignore
    public void testApply4Leave(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        processEngine.getTaskService()
                .complete("104");//查看act_ru_task表
    }

    /**
     * 小明的班主任小毛在查询当前执行任务
     */
    @Ignore
    public void testQueryTask(){

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        List<Task> tasks = processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee("小毛")
                .list();
    }

    /**
     * 班主任小毛完成任务
     */
    @Ignore
    public void testFinishTask_manager(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        processEngine.getTaskService()
                .complete("202");//查看act_ru_task数据表
    }

    /**
     * 教务处大毛完成任务
     */
    @Ignore
    public void testFinishTask_Boss(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        processEngine.getTaskService()
                .complete("302");//查看act_ru_task数据表
    }


}
