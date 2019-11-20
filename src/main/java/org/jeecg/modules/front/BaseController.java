package org.jeecg.modules.front;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.FrontCodeConstant;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.service.IParentService;
import org.jeecg.modules.shiro.authc.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import ws.schild.jave.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class BaseController {


    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IParentService parentService;

    /**
     * 发送短信
     *
     * @param phone
     * @throws Exception
     */
    public int sendMsg(String phone) throws Exception {
        String url = "https://dx.ipyy.net/sms.aspx";
        String accountName = "yfly190718";                            //改为实际账号名
        String password = "yfly190718";                                //改为实际发送密码

        int i = RandomUtil.randomInt(100000, 1000000);
        String text = "";
        //注册
        text = String.format(FrontCodeConstant.REGISTER_MSG_TEMPLATE, String.valueOf(i));

        Map map = new HashMap();
        map.put("action", "send");
        map.put("userid", "");
        map.put("account", accountName);
        map.put("password", password);
        map.put("mobile", phone);       //多个手机号用逗号分隔
        map.put("content", text);
        map.put("sendTime", "");
        map.put("extno", "");

        HttpResponse execute = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .timeout(10000)
                .form(map)
                .execute();
        return i;
    }

    /**
     * 校验token是否过期
     *
     * @param token
     * @return
     */
    public Parent verify(String token) {
        Object o = redisUtil.get(CommonConstant.PREFIX_FRONT_USER_TOKEN + token);
        if (o == null) {
            //token 已经过期
            return null;
        } else {
            //更新过期时间
            redisUtil.set(CommonConstant.PREFIX_FRONT_USER_TOKEN + token, String.valueOf(o), JwtUtil.FRONT_EXPIRE_TIME / 1000);
            String phone = String.valueOf(redisUtil.get(CommonConstant.PREFIX_FRONT_USER_TOKEN + token));
            QueryWrapper<Parent> userWraper = new QueryWrapper<>();
            userWraper.eq("pt_phone", phone);
            Parent user = parentService.getOne(userWraper);
            return user;
        }
    }


    /**
     * 上传语音文件
     *
     * @param request
     * @param response
     * @return
     */
    public String uploadYY(HttpServletRequest request, HttpServletResponse response) throws Exception {


        String ctxPath = uploadpath;
        String fileName = null;
        String bizPath = "user";
        String nowday = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File file = new File(ctxPath + File.separator + bizPath + File.separator + nowday);
        if (!file.exists()) {
            file.mkdirs();// 创建文件根目录
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
        String orgName = mf.getOriginalFilename();// 获取文件名
        fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis();
        String savePath = file.getPath() + File.separator;
        File savefile = new File(savePath + fileName +  orgName.substring(orgName.indexOf(".")));
        File targetFile = new File(savePath + fileName + ".mp3");
        FileCopyUtils.copy(mf.getBytes(), savefile);


        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();


        MultimediaObject multimediaObject  = new MultimediaObject(savefile);
        encoder.encode(multimediaObject,targetFile, attrs);

        String dbpath = bizPath + File.separator + nowday + File.separator + fileName +".mp3";
        if (dbpath.contains("\\")) {
            dbpath = dbpath.replace("\\", "/");
        }
        return dbpath;
    }


    public String uploadImg(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String ctxPath = uploadpath;
        String fileName = null;
        String bizPath = "user";
        String nowday = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File file = new File(ctxPath + File.separator + bizPath + File.separator + nowday);
        if (!file.exists()) {
            file.mkdirs();// 创建文件根目录
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
        String orgName = mf.getOriginalFilename();// 获取文件名
        fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
        String savePath = file.getPath() + File.separator + fileName;
        File savefile = new File(savePath);
        FileCopyUtils.copy(mf.getBytes(), savefile);
        String dbpath = bizPath + File.separator + nowday + File.separator + fileName;
        if (dbpath.contains("\\")) {
            dbpath = dbpath.replace("\\", "/");
        }
        return dbpath;
    }

}
