package org.jeecg.modules.front;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.hudong.aboutUs.entity.AboutUs;
import org.jeecg.modules.hudong.aboutUs.service.IAboutUsService;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.customer.entity.Cusomter;
import org.jeecg.modules.hudong.customer.service.ICusomterService;
import org.jeecg.modules.hudong.feedback.entity.Feedback;
import org.jeecg.modules.hudong.feedback.service.IFeedbackService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.service.IParentService;
import org.jeecg.modules.hudong.qu.entity.Question;
import org.jeecg.modules.hudong.qu.service.IQuestionService;
import org.jeecg.modules.shiro.authc.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/front/parent/vipCenter")
public class ParentVipCenterController extends BaseController {

    @Autowired
    private IParentService parentService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IChildService childService;
    @Autowired
    private IQuestionService questionService;
    @Autowired
    private IAboutUsService aboutUsService;
    @Autowired
    private IFeedbackService feedbackService;
    @Autowired
    private ICusomterService cusomterService;


    @PostMapping(value = "")
    @ApiOperation("个人中心首页")
    public Result<JSONObject> home(@RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", user);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    @PostMapping(value = "/updateName")
    @ApiOperation("修改姓名")
    public Result<JSONObject> updateName(@RequestParam("name") String name,
                                   @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {
                user.setPtName(name);
                parentService.updateById(user);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    @PostMapping(value = "/updatePhone")
    @ApiOperation("修改手机号")
    public Result<JSONObject> updatePhone(@RequestParam("phone") String phone,
                                   @RequestParam("verify") String verify,
                                   @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {

                List<Parent> pt_phone = parentService.list(new QueryWrapper<Parent>().eq("pt_phone", phone));
                if(pt_phone.size() > 0){
                    //验证码错误
                    result.error500("该手机号已绑定,请更换手机号!");
                    return result;
                }

                List<Child> cd_phone = childService.list(new QueryWrapper<Child>().eq("cd_phone", phone));
                if(cd_phone.size() > 0){
                    //验证码错误
                    result.error500("该手机号已绑定,请更换手机号!");
                    return result;
                }

                Object o = redisUtil.get(phone);
                if(o != null){
                    String sign = String.valueOf(o);
                    if(!sign.equals(verify)){
                        //验证码错误
                        result.error500("验证码错误!");
                        return result;
                    }
                }else {
                    result.error500("验证码错误");
                    return result;
                }
                user.setPtPhone(phone);
                parentService.updateById(user);
                redisUtil.del(CommonConstant.PREFIX_FRONT_USER_TOKEN+token);
                result.success("操作成功,请重新登录");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

    @PostMapping(value = "/forgetPwd")
    @ApiOperation("修改密码")
    public Result<Object> forgetPwd(@RequestParam("phone") String phone,
                                    @RequestParam("confirm") String confirm,
                                    @RequestParam("password") String password,
                                    @RequestParam("verify") String verify,
                                    @RequestHeader("token") String token) {

        Result<Object> result = new Result<Object>();
        try {
            Parent user = verify(token);
            if (user != null) {
                Object o = redisUtil.get(phone);
                if(o != null){
                    String sign = String.valueOf(o);
                    if(!sign.equals(verify)){
                        //验证码错误
                        result.error500("验证码错误!");
                        return result;
                    }
                    if(!confirm.equals(password)){
                        //密码不同
                        result.error500("两次密码不相同!");
                        return result;
                    }
                    String userpassword = SecureUtil.md5(password);
                    user.setPtPassword(userpassword);
                    parentService.updateById(user);
                    redisUtil.del(CommonConstant.PREFIX_FRONT_USER_TOKEN+token);
                    result.success("操作成功");
                    return result;
                }else {
                    result.error500("验证码错误");
                    return result;
                }
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    @PostMapping(value = "/childList")
    @ApiOperation("孩子列表")
    public Result<Object> childList(@RequestHeader("token") String token) {

        Result<Object> result = new Result<Object>();
        try {
            Parent user = verify(token);
            if (user != null) {
                List<Child> ptList = childService.list(new QueryWrapper<Child>().eq("pt_id", user.getId()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data",ptList);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    /**
     * 增加孩子
     * @param
     * @param verify       //验证码
     * @param confirm       //确认密码
     * @param token
     * @return
     */
    @PostMapping(value = "/addChild")
    @ApiOperation("增加孩子")
    public Result<Object> addChild(
            @RequestParam("cdName") String cdName,
            @RequestParam("cdBirthday") String cdBirthday,
            @RequestParam("cdSex") String cdSex,
            @RequestParam("cdPhone") String cdPhone,
            @RequestParam("cdPassword") String cdPassword,
            @RequestParam("verify") String verify,
            @RequestParam("confirm") String confirm,
            @RequestHeader("token") String token) {

        Result<Object> result = new Result<Object>();
        try {

            List<Child> cd_phone = childService.list(new QueryWrapper<Child>().eq("cd_phone", cdPhone));
            if(cd_phone.size() > 0){
                //验证码错误
                result.error500("该手机已经被注册!");
                return result;
            }
            Parent user = verify(token);
            if (user != null) {
                Object o = redisUtil.get(cdPhone);
                if (o != null) {
                    String sign = String.valueOf(o);
                    if (!sign.equals(verify)) {
                        //验证码错误
                        result.error500("验证码错误!");
                        return result;
                    }
                    if (!confirm.equals(cdPassword)) {
                        //密码不同
                        result.error500("两次密码不相同!");
                        return result;
                    }

                    Child child = new Child();
                    child.setCdName(cdName);
                    child.setCdPhone(cdPhone);
                    String userpassword = SecureUtil.md5(cdPassword);
                    child.setCdPassword(userpassword);
                    child.setCdSex(cdSex);
                    child.setCdBirthday(cdBirthday);
                    child.setPtId(user.getId());
                    child.setChildAvater("/child.png");
                    childService.save(child);
                    result.success("操作成功");
                    return result;
                } else {
                    //token失效,重新登陆
                    result.error500("请先接受验证码");
                    return result;
                }
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }

    @PostMapping(value = "/childDetailById")
    @ApiOperation("孩子详情")
    public Result<JSONObject> childDetailById(@RequestParam("id") String id,
                                              @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {
                Child child = childService.getById(id);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", child);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    @PostMapping(value = "/updateChildName")
    @ApiOperation("修改孩子姓名")
    public Result<JSONObject> updateChildName(@RequestParam("name") String name,
                                              @RequestParam("id") String id,
                                              @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {
                Child child = new Child();
                child.setId(id);
                child.setCdName(name);
                childService.updateById(child);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", name);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

    @PostMapping(value = "/updateChildbirthday")
    @ApiOperation("修改孩子生日")
    public Result<JSONObject> updateChildBirthday(@RequestParam("birthday") String birthday,
                                              @RequestParam("id") String id,
                                              @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {
                Child child = new Child();
                child.setId(id);
                child.setCdBirthday(birthday);
                childService.updateById(child);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", birthday);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 修改孩子性别
     * @param sex       1/2
     * @param id
     * @param token
     * @return
     */
    @PostMapping(value = "/updateChildsex")
    @ApiOperation("修改孩子性别")
    public Result<JSONObject> updateChildsex(@RequestParam("sex") String sex,
                                                  @RequestParam("id") String id,
                                                  @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {
                Child child = new Child();
                child.setId(id);
                child.setCdSex(sex);
                childService.updateById(child);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", sex);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    @PostMapping(value = "/updateChildPhone")
    @ApiOperation("修改孩子手机号")
    public Result<JSONObject> updateChildPhone(@RequestParam("phone") String phone,
                                          @RequestParam("verify") String verify,
                                          @RequestParam("id") String id,
                                          @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            List<Child> cd_phone = childService.list(new QueryWrapper<Child>().eq("cd_phone", phone));
            if(cd_phone.size() > 0){
                //验证码错误
                result.error500("该手机已经被注册!");
                return result;
            }
            Parent user = verify(token);
            if (user != null) {

                Object o = redisUtil.get(phone);
                if(o != null){
                    String sign = String.valueOf(o);
                    if(!sign.equals(verify)){
                        //验证码错误
                        result.error500("验证码错误!");
                        return result;
                    }
                }else {
                    result.error500("请先发送验证码");
                    return result;
                }
                Child byId = childService.getById(id);
                String ctoken = JwtUtil.sign(byId.getCdPhone(), byId.getCdPassword());
                redisUtil.del(CommonConstant.PREFIX_FRONT_USER_TOKEN+ctoken);
                byId.setCdPhone(phone);
                childService.updateById(byId);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

    @PostMapping(value = "/forgetChildPwd")
    @ApiOperation("修改孩子密码")
    public Result<Object> forgetChildPwd(@RequestParam("phone") String phone,
                                    @RequestParam("confirm") String confirm,
                                    @RequestParam("password") String password,
                                    @RequestParam("verify") String verify,
                                    @RequestParam("id") String id,
                                    @RequestHeader("token") String token) {

        Result<Object> result = new Result<Object>();
        try {
            Parent user = verify(token);
            if (user != null) {
                Object o = redisUtil.get(phone);
                if(o != null){
                    String sign = String.valueOf(o);
                    if(!sign.equals(verify)){
                        //验证码错误
                        result.error500("验证码错误!");
                        return result;
                    }
                    if(!confirm.equals(password)){
                        //密码不同
                        result.error500("两次密码不相同!");
                        return result;
                    }

                    Child child = childService.getById(id);
                    String ctoken = JwtUtil.sign(child.getCdPhone(), child.getCdPassword());
                    redisUtil.del(CommonConstant.PREFIX_FRONT_USER_TOKEN+ctoken);
                    String userpassword = SecureUtil.md5(password);
                    child.setCdPassword(userpassword);
                    childService.updateById(child);
                    result.success("操作成功");
                    return result;
                }else {
                    result.error500("验证码错误");
                    return result;
                }
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    @PostMapping(value = "/uploadAvater")
    @ApiOperation("修改头像")
    public Result<Object> uploadAvater(HttpServletRequest request, HttpServletResponse response,
                                         @RequestHeader("token") String token) {

        Result<Object> result = new Result<Object>();
        try {
            Parent user = verify(token);
            if (user != null) {
                String upload = uploadImg(request, response);
                user.setPtAvater(upload);
                parentService.updateById(user);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("avater",upload);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    @PostMapping(value = "/uploadChildAvater")
    @ApiOperation("修改孩子头像")
    public Result<Object> uploadChildAvater(HttpServletRequest request, HttpServletResponse response,
                                       @RequestHeader("token") String token,
                                       @RequestParam("childid") String childid) {

        Result<Object> result = new Result<Object>();
        try {
            Parent user = verify(token);
            Child child = childService.getById(childid);
            if (user != null) {
                String upload = uploadImg(request, response);
                child.setChildAvater(upload);
                childService.updateById(child);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("avater",upload);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    @PostMapping(value = "/questlist")
    @ApiOperation("常见问题列表")
    public Result<Object> questlist() {

        Result<Object> result = new Result<Object>();
        try {
            List<Question> list = questionService.list();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("question",list);
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }

    @PostMapping(value = "/questById")
    @ApiOperation("指定常见问题")
    public Result<Object> questlist(@RequestParam("id") String id) {

        Result<Object> result = new Result<Object>();
        try {
            Question byId = questionService.getById(id);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("question",byId);
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }

    @PostMapping(value = "/aboutUs")
    @ApiOperation("关于我们")
    public Result<Object> aboutUs() {

        Result<Object> result = new Result<Object>();
        try {
            List<AboutUs> list = aboutUsService.list();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("aboutUs",list);
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    @PostMapping(value = "/feedback")
    @ApiOperation("用户反馈")
    public Result<Object> feedback(@RequestParam("content") String content,
                                   @RequestHeader("token") String token) {

        Result<Object> result = new Result<Object>();
        try {
            Parent user = verify(token);
            if (user != null) {
                Feedback feedback = new Feedback();
                feedback.setPtId(user.getId());
                feedback.setPtName(user.getPtName());
                feedback.setPtPhone(user.getPtPhone());
                feedback.setContent(content);
                feedbackService.save(feedback);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }

    @PostMapping(value = "/customer")
    @ApiOperation("客服电话")
    public Result<Object> customer() {

        Result<Object> result = new Result<Object>();
        try {
            List<Cusomter> list = cusomterService.list();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customer",list);
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


}
