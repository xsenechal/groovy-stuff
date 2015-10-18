import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.io.ByteArrayResource
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Grapes(
        @Grab(group='org.springframework', module='spring-context', version='3.2.8.RELEASE')
)

class Log {
    static ln(String str) {
        println "[${new Date().format("HH:mm:ss.SSS")}] $str"
    }
}

class MyService{
    def duration = 5000
    @Scheduled(fixedDelay=1L)
    void myMethod(){
        try {
            Log.ln "TASK    Start working for $duration"
            Thread.sleep(duration)
            Log.ln "TASK    Ended working for $duration"
        } catch (Throwable t) {
            Log.ln "TASK    interupted with: $t"
        }
    }
}

class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {
    ThreadPoolTaskScheduler scheduler;

    void onApplicationEvent(ContextClosedEvent event) {
        def start = new Date().getTime()

        Log.ln "HANDLER shutdown hook"

        //The scheduler MUST be shutdown before the underlying executor
        scheduler.shutdown()

        //scheduler.waitForTasksToCompleteOnShutdown = true         //No new incoming task (enqueue)
        //scheduler.awaitTerminationSeconds = 1000                  //Wait for running task to complete

        Log.ln "HANDLER shutdown hook finished"
        Log.ln "HANDLER Waited ${new Date().getTime() - start} ms for running tasks to complete"
    }
}

class MyApp{
    def contextXML = """
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context-3.2.xsd
                           http://www.springframework.org/schema/task
                           http://www.springframework.org/schema/task/spring-task-3.2.xsd"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:task="http://www.springframework.org/schema/task">

    <bean id="taskExecutor" class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor" >
        <property name="corePoolSize" value="5" />
        <property name="maxPoolSize" value="10" />
    </bean>
    <bean id="taskScheduler" class="org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler" >
         <property name="poolSize" value="10" />
         <property name="waitForTasksToCompleteOnShutdown" value="true" />
         <property name="awaitTerminationSeconds" value="100" />
    </bean>
    <task:annotation-driven executor="taskExecutor" scheduler="taskScheduler"/>

    <bean id="myService" class="MyService"></bean>
    <bean id="closeHandler" class="ContextClosedHandler">
        <property name="scheduler" ref="taskScheduler" />
    </bean>

</beans>

"""
    void main(){
        ApplicationContext context = new GenericXmlApplicationContext(new ByteArrayResource( contextXML.getBytes() ))

        Thread.sleep(2000)
        Log.ln "MAIN    context is closing... "
        context.close()
        Log.ln "MAIN    context is closed... "
    }

}
new MyApp().main()
