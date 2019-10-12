package com.test;

import groovy.ui.SystemOutputInterceptor;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class BpmnTest {


    /**
     * 1 将流程定义到数据库中
     */
    @Ignore
    public void defineFlow(){
        //创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //Activiti中每一个不同版本的业务流程的定义都需要使用一些定义文件，部署文件和支持数据，这些文件都存储在Activiti内建的Repository中
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //开始部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/holiday.bpmn")
                .addClasspathResource("bpmn/holiday.png")
                .name("请假流程审批")
                .deploy();

        System.out.println("流程部署id:" + deployment.getId());
        System.out.print("流程部署名称:" + deployment.getName());

    }

    /**
     * 2 启动流程实例
     */
    @Ignore
    public void testFlowInstance(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        /**
         * 得到RuntimeService
         * 在Activiti中，每当一个流程定义被启动一次之后，都会生成一个相应的流程对象实例。
         * RuntimeService提供了启动流程、查询流程实例、设置获取流程实例变量等功能
         */
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //创建流程实例，需要知道流程定义的key
        ProcessInstance holiday = runtimeService.startProcessInstanceByKey("holiday");

        System.out.println("流程部署ID "+holiday.getDeploymentId());
        System.out.println("流程实例ID "+holiday.getId());
        System.out.println("流程的Key "+holiday.getBusinessKey());
        System.out.println("活动Id "+holiday.getActivityId());
    }

    /**
     * 3 查询任务
     */
    @Ignore
    public void selectTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        /**
         * 获取TaskService对象，在Activiti中业务流程定义中的每一个执行节点被称为一个Task
         * 对流程中的数据存取，状态变更等操作均需要在Task中完成
         */
        TaskService taskService = processEngine.getTaskService();

        //根据流程定义的key和负责人查询当前用户的任务列表
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday")
                .taskAssignee("zhangsan")
                .list();

        for (Task task : list) {
            System.out.println("流程实例Id "+task.getProcessInstanceId());
            System.out.println("任务Id "+task.getId());
            System.out.println("负责人名称 "+task.getAssignee());
            System.out.println("任务名称 "+task.getName());

        }

    }

    /**
     * 4 处理任务
     */
    @Ignore
    public void completeTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();

        //处理任务 任务ID 2504（上一个@Test查到的的）
        taskService.complete("2504");
    }


    /**
     * 5 流程定义的查询
     */
    @Ignore
    public void selectACTOFlow(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        //这是一个查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //设置流程定义的key
        List<ProcessDefinition> holiday = processDefinitionQuery
                .processDefinitionKey("holiday")
                //设置排序方式，默认按照流程定义的版本号排序
                .orderByProcessDefinitionVersion().desc()
                .list();

        for (ProcessDefinition processDefinition : holiday) {
            System.out.println("流程定义的ID " +processDefinition.getId());
            System.out.println("流程定义名称 " +processDefinition.getName());
            System.out.println("流程定义Key " +processDefinition.getKey());
            System.out.println("流程定义的版本号 " +processDefinition.getVersion());
            System.out.println("流程部署的ID  " +processDefinition.getDeploymentId());
        }
    }


    /**
     * 6 删除已部署的流程
     *
     * 流程删除和流程部署影响的表一样的
     */
    @Ignore
    public void deleteFlow(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        /**
         * 执行删除，参数是部署ID
         * 如果流程没有执行完成 删除时候会出错，如果强制删除也是可以的是级联删除
         * 执行但未完成的也是可以删除的   true会先删除没有完成的流程节点，最后删除流程定义信息
         */
        repositoryService.deleteDeployment("1",true);


    }

    /** 查看历史流程实例 */
    @Ignore
    public void queryHistoricProcessInstanceTest() throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


        HistoryService service = processEngine.getHistoryService();

        // 获取历史流程实例的查询对象
        HistoricProcessInstanceQuery hpiQuery = service.createHistoricProcessInstanceQuery();
        // 设置流程定义KEY
        hpiQuery.processDefinitionKey("holiday");
        // 分页条件
        //hpiQuery.listPage(firstResult, maxResults);
        // 排序
        hpiQuery.orderByProcessInstanceStartTime().desc();

        // 执行查询
        List<HistoricProcessInstance> hpis = hpiQuery.list();
        for (HistoricProcessInstance hpi : hpis) {
            System.out.print("pid:" + hpi.getId() + ",");
            System.out.print("pdid:" + hpi.getProcessDefinitionId() + ",");
            System.out.print("startTime:" + hpi.getStartTime() + ",");
            System.out.print("endTime:" + hpi.getEndTime() + ",");
            System.out.print("duration:" + hpi.getDurationInMillis() + ",");
            System.out.println("vars:" + hpi.getProcessVariables());
        }
    }

    /**
     * 7 流程定义资源查看
     * 这个查的是实例
     */
    @Ignore
    public void selectFlowDefine(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        /**
         * HistoryService用于获取正在运行或已经完成的流程实例的信息
         */
        HistoryService historyService = processEngine.getHistoryService();

        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.
                createHistoricActivityInstanceQuery();

        //查询条件是2 查询实例中的编号
        historicActivityInstanceQuery.processInstanceId("10001");

        //执行流程
        List<HistoricActivityInstance> list = historicActivityInstanceQuery
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();

        for(HistoricActivityInstance historicActivityInstance : list){
            System.out.println(historicActivityInstance.getActivityId());
            System.out.println(historicActivityInstance.getActivityName());
            System.out.println(historicActivityInstance.getProcessDefinitionId());
            System.out.println(historicActivityInstance.getProcessInstanceId());
        }

    }


    /**
     * 10 全部流程挂起与激活
     * 流程定义为挂起状态时，
     * 该流程定义不允许启动新的流程实例，
     * 同时该流程定义下所有的流程实例将全部挂起暂停执行
     */
    @Test
    public void hangAndActive(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        //查询流程实例
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("holiday")
                .singleResult();

        //得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();
        String processDefinitionId = processDefinition.getId();
        if(suspended){
            //说明是暂停，可以使用激活操作
            repositoryService.activateProcessDefinitionById(processDefinitionId,true,null);
            System.out.println("流程定义：" + processDefinitionId + "激活");
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("流程定义：" + processDefinitionId + "挂起");
        }

    }


}
