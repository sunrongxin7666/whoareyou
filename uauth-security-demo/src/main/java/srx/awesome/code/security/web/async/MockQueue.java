package srx.awesome.code.security.web.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockQueue {
    private String placeOrder;
    private String completedOrder;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public String getPlaceOrder() {
        return placeOrder;
    }

    public void setPlaceOrder(String placeOrder)  {
        new Thread(()->{
            logger.info("get order:"+placeOrder);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //this.placeOrder = placeOrder;
            logger.info("put order: "+placeOrder+" into queue.");
            completedOrder = placeOrder;
        }
        ).start();

    }

    public String getCompletedOrder() {

        return completedOrder;
    }

    public void setCompletedOrder(String completedOrder) {
        this.completedOrder = completedOrder;
    }
}
