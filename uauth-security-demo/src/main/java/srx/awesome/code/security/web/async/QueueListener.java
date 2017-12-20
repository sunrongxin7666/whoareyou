package srx.awesome.code.security.web.async;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class QueueListener
        implements ApplicationListener<ContextRefreshedEvent>{// Spring容器启动时间
    @Autowired
    private MockQueue mockQueue;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //Spring 容器启动时
        new Thread(() -> {
            while ( 1==1 ){
                if(StringUtils.isNotBlank(mockQueue.getCompletedOrder())){
                    String order = mockQueue.getCompletedOrder();
                    logger.info("return order result :"+order);
                    deferredResultHolder.getMap().get(order)
                            .setResult("placeOrder Success :"+order);
                    mockQueue.setCompletedOrder(null);
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ).start();

    }
}
