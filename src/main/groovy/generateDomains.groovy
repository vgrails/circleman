import com.circleman.Bootstrap
import grails.gorm.annotation.Entity
import org.reflections.Reflections

import static com.circleman.core.BaseApp.metaDomainMap
import static spark.Spark.*
import static com.circleman.Bootstrap.*

Bootstrap.main()

println "Generating Domains"

Reflections reflections = new Reflections("com.circleman")

reflections.getTypesAnnotatedWith(Entity).each { Class clazz ->
    println "${clazz.simpleName}"
    println metaDomainMap[clazz.simpleName]
}



stop()