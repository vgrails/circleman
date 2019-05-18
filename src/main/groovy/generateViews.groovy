import grails.gorm.annotation.Entity
import org.reflections.Reflections

println "Generating Views"

Reflections reflections = new Reflections("com.circleman")

entities = reflections.getTypesAnnotatedWith(Entity)

entities.each { Class clazz ->
    println "${clazz.simpleName}"
}