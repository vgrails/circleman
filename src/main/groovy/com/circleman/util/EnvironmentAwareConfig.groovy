package com.circleman.util

import com.google.gson.Gson
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 环境敏感的配置类
 *
 * @NOTE 必须先进行LoadConfig才可以使用getEnv, getConfig等操作
 */
@Slf4j(category = "EnvironmentAwareConfig")
class EnvironmentAwareConfig {
    //环境相关
    static String env = null
    static String DEVELOPMENT = "development"
    static String TEST = "test"
    static String INTEGRATION = "integration"
    static String PRODUCTION = "production"
    private static Map<String, Object> config

    //默认配置值表
    static HashMap <String, Object> defaultConfigs =[
            "framework.port" : 8080,
            "framework.reflectionScan": "com.circleman",
            "framework.app.name":"demo",
            "framework.app.name":"Circleman演示系统",
            "framework.app.version":"0.0.0",
            "framework.threadPool.min":10,
            "framework.threadPool.max":100,
            "framework.threadPool.timeout":10,

            "codegen.generateDomains":true,
            "codegen.domainsPackage":"com.circleman.domains",
            "codegen.overridden":false,

            "hbm2ddl":"create-drop",
            "database.url":"jdbc:h2:mem:demo"
    ]

    /**
     * 加载系统配置
     */
    static synchronized void initConfig(){

        if(config == null) {
            //加载配置，如果环境与假设不匹配，需要重新加载
            try {
                config = new ConfigSlurper(DEVELOPMENT).parse(new File("./src/main/resources/config/AppConfig.groovy").toURI().toURL()).flatten()

                env = config.environment

                if(env != DEVELOPMENT){
                    config = new ConfigSlurper(env).parse(new File("./src/main/resources/config/AppConfig.groovy").toURI().toURL()).flatten()
                }
            } catch (Exception e) {
                log.error "环境加载异常，检查：'/config/AppConfig.groovy'"
            }

            //设定默认
            if(config == null) config =[:]
            if(env == null) env = DEVELOPMENT

            defaultConfigs.each{ String key, Object value ->
                if(config[key]==[:]) config[key]=defaultConfigs[key]
            }

            if(env != PRODUCTION) {
                log.info("App Config:\n ${JsonOutput.prettyPrint(new Gson().toJson(config))}")
            }
        }
    }

    /**
     * 获取配置
     */
    static Object getConfig(String key){
        Object result = config[key]

        //空值处理
        if(result == [:]){
            result = null
        }

        return result
    }
}