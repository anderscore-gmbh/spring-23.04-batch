package de.springbatchdeepdive.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import java.lang.reflect.Proxy;

/**
 * Diese Hilfsklasse listet die Singleton-Beans im {@link ApplicationContext}.
 */
// @Component
public class ApplicationContextDebugger {

    @Autowired
    private ApplicationContext context;

    @EventListener(ApplicationReadyEvent.class)
    public void handleApplicationReadyEvent() {
        for (String beanName : context.getBeanDefinitionNames()) {
            if (context.isSingleton(beanName)) {
                Class<?> type = getType(context.getBean(beanName));
                if (!type.getName().startsWith("org.springframework.boot")
                        && !type.getName().startsWith("org.springframework.context")) {
                    System.out.printf("|%s|%s%n", beanName, type.getName());
                }
            }
        }
    }

    private Class<?> getType(Object bean) {
        if (bean instanceof Proxy) {
            return bean.getClass().getInterfaces()[0];
        }
        return bean.getClass();
    }
}
