package srx.awesome.code.security.web.async;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;


@RestController
public class AsyncController {

    @Autowired
    private MockQueue mockQueue;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @GetMapping("/order")
    public DeferredResult<String> order() {
        logger.info("main thread start");

        String randomNum = RandomStringUtils.randomNumeric(8);
        mockQueue.setPlaceOrder(randomNum);
        DeferredResult<String> result = new DeferredResult<>();
        deferredResultHolder.getMap().put(randomNum, result);


//        Callable<String> callable = new Callable<String>() {
//
//            @Override
//            public String call() throws Exception {
//                logger.info("sub thread start");
//                Thread.sleep(1000);
//                logger.info("sub thread stop");
//                return "success";
//            }
//        };
        logger.info("main thread end");
        return result;//返回异步结果
    }
}
