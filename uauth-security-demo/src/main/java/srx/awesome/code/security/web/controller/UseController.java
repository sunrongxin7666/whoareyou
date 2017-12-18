package srx.awesome.code.security.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import srx.awesome.code.security.dto.User;
import srx.awesome.code.security.dto.UserQueryCondition;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UseController {

    @PostMapping
    @JsonView(User.UserDetailView.class)
    //@RequestBody 将上传的数据绑定到参数中
    //@Valid 校验数据有效性 无效则直接拦截 错误信息将会绑定到BindingResult对象中
    public User create(@Valid @RequestBody  User user, BindingResult errors){
        if(errors.hasErrors()){
            errors.getAllErrors()
                    .forEach(error -> System.out.println(error.getDefaultMessage()));
        }
        System.out.println(user);

        user.setId("1");
        System.out.println(user.getBirthday());
        return user;
    }

    @PutMapping("/{id:\\d+}")
    @JsonView(User.UserDetailView.class)
    public User update(@Valid @RequestBody User user, BindingResult errors, @PathVariable("id") String id){
        if(errors.hasErrors()){
            errors.getAllErrors()
                    .forEach(error -> {
                        FieldError fieldError = ((FieldError) error);
                        System.out.println( fieldError.getField() + " : " + error.getDefaultMessage() );
                    });
            return null;
        }
        System.out.println(user);

        user.setId(id);
        System.out.println(user.getBirthday());
        return user;
    }


    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable String id){
        System.out.println(id);
    }

    @GetMapping//==requestMapping method=get
    @JsonView(User.UserSimpleView.class)
    //Pageable Spring Data中的设置
    public List<User> query(UserQueryCondition condition,
                            @PageableDefault(page = 1, size = 15, sort = "username", direction = Sort.Direction.DESC) Pageable pageable){

        System.out.println(condition);

        System.out.println(ReflectionToStringBuilder
                .toString(pageable, ToStringStyle.MULTI_LINE_STYLE));

        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }

    @JsonView(User.UserDetailView.class)
    @GetMapping(value = "/{id:\\d+}")// \\d+增则表达式数字
    //@PathVariable 将URL中的变量绑定到Java代码中
    public User getInfo(@PathVariable(name = "id") String id){
        User user = new User();
        user.setUsername("tom");
        user.setId(id);
        user.setPassword("1求爱者wsx");
        return user;
    }
}
