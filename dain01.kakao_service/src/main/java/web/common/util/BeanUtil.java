package web.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import lombok.extern.slf4j.Slf4j;

/**
 * @Project : SCUP
 * @Class : BeanUtil.java
 * @Description : 스프링 Bean 유틸.
 * @Author : im7015
 * @Since : 2019. 5. 10
 */
@Slf4j
public class BeanUtil implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    private static ApplicationContext CONTEXT;

    public BeanUtil() {
        logger.info("init SpringApplicationContext");
    }

    public static Object getBean(String beanName) {
        if (CONTEXT == null) {
            log.info("CONTEXT is null.");
            return null;
        }
        return CONTEXT.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> requiredType) {
        return CONTEXT.getBean(beanName, requiredType);
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        CONTEXT = context;
    }

}
