package srx.awesome.code.security.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import srx.awesome.code.security.exception.UserNotExistException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice//处理controller的异常
public class ControllerExceptionHandler {

    @ExceptionHandler(UserNotExistException.class)//响应特定的异常
    @ResponseBody//将请求转为JSon写入Response
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handlerUserNotExistException(UserNotExistException ex){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",ex.id);
        hashMap.put("msg", ex.getMessage());
        return hashMap;
    }
}
