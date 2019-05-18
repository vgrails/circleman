package com.circleman.util

import com.google.gson.Gson
import groovy.json.JsonOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 环境敏感的配置类
 *
 * @NOTE 必须先进行LoadConfig才可以使用getEnv, getConfig等操作
 */
class EnvConfig {
    //环境相关
    static String env = null
    static String DEVELOPMENT = "development"
    static String TEST = "test"
    static String INTEGRATION = "integration"
    static String PRODUCTION = "production"
    static Map<String, Object> config

    //日志相关
    static Logger log = LoggerFactory.getLogger(EnvConfig)

    /**
     * 加载系统配置
     */
    static synchronized void LoadConfig(){

        if(config == null) {
            try {
                config = new ConfigSlurper("development").parse(new File("./src/main/resources/config/AppConfig.groovy").toURI().toURL()).flatten()

                env = config.environment

                if(env != 'development'){
                    config = new ConfigSlurper(env).parse(new File("./src/main/resources/config/AppConfig.groovy").toURI().toURL()).flatten()
                }

                if(env != PRODUCTION) {
                    log.info("App Config:\n ${JsonOutput.prettyPrint(new Gson().toJson(config))}")
                }
            } catch (Exception e) {
                log.error "环境加载异常，烦请检查：'/config/AppConfig.groovy'"
            }
        }
    }

    static Object getConfig(String key){
        return config[key]
    }
}
