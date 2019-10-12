package com.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

/**
 * 从数据源和流程图中，生成一个数据库表（activiti流控制的关键数据表）
 */
public class ActivitiTable {
    /**
     * 创建相关数据表
     */
    @Test
    public void createTable(){
        ProcessEngine processEngine = ProcessEngineConfiguration.
                createProcessEngineConfigurationFromResource("activiti.cfg.xml")
                .buildProcessEngine();
    }


}
