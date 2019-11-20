package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.FileUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.hudong.aboutUs.entity.AboutUs;
import org.jeecg.modules.hudong.aboutUs.service.IAboutUsService;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.customer.entity.Cusomter;
import org.jeecg.modules.hudong.customer.service.ICusomterService;
import org.jeecg.modules.hudong.feedback.entity.Feedback;
import org.jeecg.modules.hudong.feedback.service.IFeedbackService;
import org.jeecg.modules.hudong.kc.entity.Kc;
import org.jeecg.modules.hudong.kc.service.IKcService;
import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import org.jeecg.modules.hudong.mqx.service.IMqXQingService;
import org.jeecg.modules.hudong.msfenlei.entity.MsFenLi;
import org.jeecg.modules.hudong.msfenlei.service.IMsFenLiService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.service.IParentService;
import org.jeecg.modules.hudong.qu.entity.Question;
import org.jeecg.modules.hudong.qu.service.IQuestionService;
import org.jeecg.modules.hudong.xthf.entity.Xthf;
import org.jeecg.modules.hudong.xthf.service.IXthfService;
import org.jeecg.modules.hudong.xtzc.entity.Xtzc;
import org.jeecg.modules.hudong.xtzc.service.IXtzcService;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;
import org.jeecg.modules.shiro.authc.util.JwtUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("/front/parent/vipCenter")
public class ParentVipCenterController extends BaseController {


    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

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
    @Autowired
    private IKcService kcService;
    @Autowired
    private IMsFenLiService fenLiService;
    @Autowired
    private IMqXQingService xQingService;
    @Autowired
    private IXthfService xthfService;
    @Autowired
    private IXtzcService xtzcService;
    @Autowired
    private IXueXiService xueXiService;


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


    /**
     * 年级列表
     * @param
     * @param
     * @return
     */
    @GetMapping(value = "/njList")
    public Result<List<Map<String,String>>> valueList(@RequestHeader("token") String token) {
        Result<List<Map<String,String>>> result = new Result<List<Map<String,String>>>();
        try {
            Parent user = verify(token);
            if (user != null) {
                List<MsFenLi> list = fenLiService.list();
                List<Map<String,String>> list1 = new ArrayList<>();
                if(list.size() > 0){
                    list.stream().forEach(msfenli->{
                        Map map = new HashMap();
                        map.put("id",msfenli.getId());
                        map.put("name",msfenli.getFlName());
                        list1.add(map);
                    });
                }
                result.setResult(list1);
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

                List<Map<String,String>> ptList = new ArrayList<>();

                List<Map<String,String>> list = childService.selectMsgAndChild(user.getId(), DateUtils.formatDate(new Date())+"%");
                list.stream().forEach(map -> {
                    Map map1 = new HashMap();

                    map1.put("count",map.get("count")); //学习的未读消息
                    map1.put("lcount",map.get("lcount"));
                    map1.put("id",map.get("id"));
                    map1.put("ptId",map.get("ptId"));
                    map1.put("cdName",map.get("cdName"));
                    map1.put("cdBirthday",map.get("cdBirthday"));
                    map1.put("cdSex",map.get("cdSex"));
                    map1.put("cdPhone",map.get("cdPhone"));
                    map1.put("cdPassword",map.get("cdPassword"));

                    ptList.add(map1);

                });

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
            @RequestParam("njId") String flId,
            @RequestParam("njName") String flName,
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
                    child.setFlId(flId);
                    child.setFlName(flName);
                    if(childService.save(child)){
                        //直接发一条注册成功的系统消息
                        List<Xtzc> ps_time = xtzcService.list(new QueryWrapper<Xtzc>().eq("ps_time", '0'));
                        if(ps_time.size() > 0){
                            String dateTime = DateUtil.format(new Date(),"HH:mm");
                            XueXi xueXi = new XueXi();

                            xueXi.setTxType("0");   //提醒方式
                            xueXi.setChPhone(child.getCdPhone());
                            xueXi.setChName(child.getCdName());
                            xueXi.setPtPhone(user.getPtPhone());
                            xueXi.setPtName(user.getPtName());
                            xueXi.setXxOpion(dateTime);
                            xueXi.setXxContent(String.valueOf(ps_time.get(0).getContent()));
                            xueXi.setXxYtype("WZ");
                            xueXi.setXxVtype("XT");
                            xueXi.setXxChildId(child.getId());
                            xueXi.setXxParentId(user.getId());

                            xueXiService.save(xueXi);
                        }
                    }
                    result.success("操作成功");
                    return result;
                } else {
                    //token失效,重新登陆
                    result.error500("验证码不正确");
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
                Child child = childService.getById(id);
                child.setCdName(name);
                childService.updateNameAndKcById(child);
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


    @PostMapping(value = "/updateNj")
    @ApiOperation("修改孩子年级")
    public Result<JSONObject> updateNj(@RequestParam("njId") String njId,
                                       @RequestParam("njName") String njName,
                                      @RequestParam("id") String id,
                                      @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {
                Child child = new Child();
                child.setId(id);
                child.setFlId(njId);
                child.setFlName(njName);
                childService.updateById(child);
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
                childService.updateNameAndKcById(byId);
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

    /**
     * 家长导入孩子课程表
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportKc")
    public Result<Object> exportXls(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam("childid") String childid) {


        Result<Object> result = new Result<Object>();

        try {
            Child child = childService.getById(childid);
            Parent parent = parentService.getById(child.getPtId());
            kcService.remove(new QueryWrapper<Kc>().eq("ch_id",childid));
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
                MultipartFile file = entity.getValue();// 获取上传文件对象
                ImportParams params = new ImportParams();
                params.setTitleRows(2);
                params.setHeadRows(1);
                params.setNeedSave(true);
                try {
                    List<Kc> listKcs = ExcelImportUtil.importExcel(file.getInputStream(), Kc.class, params);
                    int week = 1;
                    int i=0;
                    for (Kc kcExcel : listKcs) {
                        int kcWeel = (int) Double.parseDouble(kcExcel.getWeekday());
                        if(kcWeel != week){
                            week = kcWeel;
                            i =0;
                        }
                        kcExcel.setNumber(i);
                        kcExcel.setChId(childid);
                        kcExcel.setPhone(child.getCdPhone());
                        kcExcel.setFlId(child.getFlId());
                        kcExcel.setFlName(child.getFlName());
                        int weekday = kcWeel;

                        kcExcel.setWeekday(String.valueOf(weekday));
                        kcService.save(kcExcel);
                        i++;
                    }
                    result.success("操作成功");
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error("文件导入失败！");
                } finally {
                    try {
                        file.getInputStream().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.error500("文件导入失败");
        }
        return result;

    }



    /**
     * 通用下载请求
     *
     */
    @GetMapping("/downExcel")
    public void fileDownload(@RequestParam("path") String path, HttpServletResponse response, HttpServletRequest request) {
        try {
            String filePath = uploadpath + "/" + path;

            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + setFileDownloadHeader(request, path));
            FileUtils.writeBytes(filePath, response.getOutputStream());
        } catch (Exception e) {
            System.out.println("文件下载失败");
           e.printStackTrace();
        }
    }

    public String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }



    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        String nj = request.getHeader("fiId");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<MqXQing> listMqXQings = ExcelImportUtil.importExcel(file.getInputStream(), MqXQing.class, params);
                for (MqXQing mqXQingExcel : listMqXQings) {
                    mqXQingExcel.setFlId(nj);
                    xQingService.save(mqXQingExcel);
                }
                return Result.ok("文件导入成功！数据行数：" + listMqXQings.size());
            } catch (Exception e) {
                return Result.error("文件导入失败！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcelXthf", method = RequestMethod.POST)
    public Result<?> importExcelXthf(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Xthf> xthfList = ExcelImportUtil.importExcel(file.getInputStream(), Xthf.class, params);
                for (Xthf xthf : xthfList) {
                    xthf.setType("0");  //默认导入的数据类型都是文字
                    String gradeName = xthf.getGradeName();
                    List<MsFenLi> fl_name = fenLiService.list(new QueryWrapper<MsFenLi>().eq("fl_name", gradeName));
                    if(fl_name.size() > 0){
                        MsFenLi fenLi = fl_name.get(0);
                        xthf.setGrade(fenLi.getId());
                        xthfService.save(xthf);
                    }else {
                        return Result.error("未找到该年级==="+gradeName);
                    }

                }
                return Result.ok("文件导入成功！数据行数：" + xthfList.size());
            } catch (Exception e) {
                return Result.error("文件导入失败！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }


    @RequestMapping("/uploadTinyImg")
    public Result<?> uploadTinyImg(HttpServletRequest request, HttpServletResponse response) {

        Result<Object> result = new Result<Object>();

        try {
            String path = uploadImg(request, response);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url","/sys/common/view/"+path);
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
