package org.jeecg.modules.front;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.*;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.liaotian.entity.LiaoTian;
import org.jeecg.modules.hudong.liaotian.service.ILiaoTianService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.service.IParentService;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;
import org.jeecg.modules.shiro.authc.util.JwtUtil;
import org.jeecg.modules.system.service.impl.SysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/front/parent/home")
public class ParentLoginController extends BaseController {

    @Autowired
    private IParentService parentService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysBaseAPI sysBaseAPI;
    @Autowired
    private IXueXiService xueXiService;
    @Autowired
    private ILiaoTianService liaoTianService;
    @Autowired
    private IChildService childService;



    //TODO 临时
    @GetMapping("/mark")
    @ApiOperation("发送短信")
    public Result mark() {
        return Result.ok(0);
    }



    /**
     * 注册
     *
     * @param phone    手机
     * @param confirm  确认密码
     * @param password 密码
     * @param verify   验证码
     * @return
     */
    @PostMapping(value = "/register")
    @ApiOperation("注册")
    public Result<Object> register(@RequestParam("phone") String phone,
                                   @RequestParam("confirm") String confirm,
                                   @RequestParam("password") String password,
                                   @RequestParam("verify") String verify) {
        Result<Object> result = new Result<Object>();
        try {
            Object o = redisUtil.get(phone);
            if (o != null) {
                String sign = String.valueOf(o);
                if (!sign.equals(verify)) {
                    //验证码错误
                    result.error500("验证码错误!");
                    return result;
                }
            }
            if (!confirm.equals(password)) {
                //密码不同
                result.error500("两次密码不相同!");
                return result;
            }

            Parent parent = new Parent();
            parent.setPtPhone(phone);
            QueryWrapper<Parent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pt_phone", phone);
            int count = parentService.count(queryWrapper);
            if (count > 0) {
                //用户已存在
                result.error500("该手机号已注册,请直接登陆!");
                return result;
            }
            //密码验证
            String passwordEncode = SecureUtil.md5(password);
            parent.setPtPassword(passwordEncode);
            parent.setPtAvater("/parent.png");
            parentService.save(parent);
            result.success("注册成功！");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("注册失败");
        }
        return result;
    }

    /**
     * 发送短信
     */
    @GetMapping("/send")
    @ApiOperation("发送短信")
    public Result phoneMsg(HttpServletRequest request, @RequestParam("phone") String phone) {
        Result<Object> result = new Result<Object>();
        try {
            int i = sendMsg(phone);
            redisUtil.set(phone, i, 5 * 60);
            result.success("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("发送失败");
        }
        return result;
    }

    /**
     * 登陆
     *
     * @param phone
     * @param password
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation("登录接口")
    public Result<JSONObject> login(@RequestParam("phone") String phone,
                                    @RequestParam("password") String password) {
        Result<JSONObject> result = new Result<JSONObject>();
        //调用QueryGenerator的initQueryWrapper
        QueryWrapper<Parent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pt_phone", phone);
        Parent one = parentService.getOne(queryWrapper);
        if (one == null) {
            //用户不存在
            result.error500("未找到该用户");
            return result;
        } else {

            int pt_id = childService.count(new QueryWrapper<Child>().eq("pt_id", one.getId()));

            //密码验证
            String userpassword = SecureUtil.md5(password);
            String syspassword = one.getPtPassword();
            if (!syspassword.equals(userpassword)) {
                result.error500("用户名或密码错误");
                return result;
            }
            //生成token
            String token = JwtUtil.sign(phone, syspassword);
            redisUtil.set(CommonConstant.PREFIX_FRONT_USER_TOKEN + token, phone);
            //设置超时时间
            redisUtil.expire(CommonConstant.PREFIX_FRONT_USER_TOKEN + token, JwtUtil.FRONT_EXPIRE_TIME / 1000);

            JSONObject obj = new JSONObject();
            obj.put("token", token);
            obj.put("count",pt_id);
            result.setResult(obj);
            result.success("登录成功");
            sysBaseAPI.addLog("手机号: " + phone + ",登录成功！", CommonConstant.LOG_TYPE_1, null);
        }
        return result;
    }


    /**
     * 忘记密码/修改密码
     *
     * @param phone    手机号
     * @param confirm  确认密码
     * @param password 密码
     * @param verify   验证码
     * @return
     */
    @PostMapping(value = "/forgetPwd")
    @ApiOperation("忘记密码")
    public Result<Object> forgetPwd(@RequestParam("phone") String phone,
                                    @RequestParam("confirm") String confirm,
                                    @RequestParam("password") String password,
                                    @RequestParam("verify") String verify) {

        Result<Object> result = new Result<Object>();
        try {

            Object o = redisUtil.get(phone);
            if (o != null) {
                String sign = String.valueOf(o);
                if (!sign.equals(verify)) {
                    //验证码错误
                    result.error500("验证码错误!");
                    return result;
                }
            } else {
                result.error500("验证码错误");
                return result;
            }
            if (!confirm.equals(password)) {
                //密码不同
                result.error500("两次密码不相同!");
                return result;
            }

            Parent parent = new Parent();
            parent.setPtPhone(phone);
            QueryWrapper<Parent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pt_phone", phone);
            Parent newParent = parentService.getOne(queryWrapper);
            if (newParent == null) {
                //用户已存在
                result.error500("该手机号未注册!");
                return result;
            }
            //修改登陆密码
            String userpassword = SecureUtil.md5(password);

            newParent.setPtPassword(userpassword);
            parentService.update(newParent, queryWrapper);
            result.success("修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    /**
     * 登出
     *
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ApiOperation("登出")
    public Result<Object> logout(@RequestHeader("token") String token) {
        Result<Object> result = new Result<Object>();
        redisUtil.del(CommonConstant.PREFIX_FRONT_USER_TOKEN + token);
        return Result.ok("退出成功！");
    }


    /**
     * 查询是否有未读消息
     *
     * @param token
     */
    @PostMapping(value = "/readCount")
    @ApiOperation("将语音消息标记为已读")
    public Result<JSONObject> readCount(@RequestHeader("token") String token) {

        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            if (user != null) {
                int xCount = xueXiService.count(new QueryWrapper<XueXi>().
                        eq("XX_PARENT_ID", user.getId()).
                        like("create_time", DateUtils.formatDate(new Date())+"%").
                        eq("XX_READ", "N"));

                int lCount = liaoTianService.count(new QueryWrapper<LiaoTian>().
                        eq("LT_PT_ID", user.getId()).
                        like("create_time",DateUtils.formatDate(new Date())+"%").
                        eq("LT_READ", "N")
                );

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("xCount", xCount);
                jsonObject.put("lCount", lCount);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                result.error500("操作失败");
                return result;
            }
        } catch (
                Exception e) {
            result.error500("操作失败!");
            return result;
        }


    }
}
