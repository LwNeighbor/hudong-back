package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.liaotian.entity.LiaoTian;
import org.jeecg.modules.hudong.liaotian.service.ILiaoTianService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.service.IParentService;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/front/chat")
public class FrontLiaotianController extends BaseController{

    @Autowired
    private IParentService parentService;
    @Autowired
    private IChildService childService;
    @Autowired
    private ILiaoTianService liaoTianService;

    @PostMapping(value = "")
    @ApiOperation("聊天孩子列表")
    public Result<JSONObject> home(@RequestHeader("token") String token) {

        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            if (user != null) {
                List list = new ArrayList();
                List<Child> childList = childService.list(new QueryWrapper<Child>().eq("pt_id", user.getId()));
                for (Child child : childList){
                    Map map = new HashMap();
                    map.put("id",child.getId());
                    map.put("name",child.getCdName());
                    map.put("avater",child.getChildAvater());
                    list.add(map);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", list);
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


    @PostMapping(value = "/beginLt")
    @ApiOperation("第一次进入聊天页面")
    public Result<JSONObject> beginLt(@RequestParam("childid") String childid,
                                      @RequestHeader("token") String token) {

        String realContent = "";
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            Child child = childService.getById(childid);
            if (user != null) {
                List<LiaoTian> list = liaoTianService.list(new QueryWrapper<LiaoTian>().
                        eq("LT_PT_ID", user.getId()).
                        eq("LT_CH_ID", childid).
                        like("create_time", DateUtils.formatDate()+"%").
                        orderByAsc("create_time"));

                liaoTianService.updateParentRead(childid);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", list);
                jsonObject.put("pavater",user.getPtAvater());
                jsonObject.put("cavater",child.getChildAvater());
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
     * 父母发消息,  如果是文件,应该也要有file:xx,都在request中,这里没有主动接收
     *
     * @param token
     * @param content //文本内容
     * @param type    //如果是文本,不要该条件 WZ/文字,YY/语音,IMG/图片
     * @param childid //孩子ID
     * @return
     */
    @PostMapping(value = "/parentMsg")
    @ApiOperation("父母发送信息")
    public Result<JSONObject> parentMsg(HttpServletRequest request, HttpServletResponse response,
                                        @RequestHeader("token") String token,
                                        @RequestParam(value = "content", required = false) String content,
                                        @RequestParam(value = "type", defaultValue = "WZ", required = false) String type,
                                        @RequestParam("childid") String childid,
                                        @RequestParam(value = "time",required = false) String time) {

        LiaoTian liaoTian = new LiaoTian();
        String realContent = "";
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            Child child = childService.getById(childid);
            if (user != null) {
                liaoTian.setLtRead("Y");
                liaoTian.setLtVtype("JZ");
                liaoTian.setLtYtype(type);
                liaoTian.setLtChId(childid);
                liaoTian.setLtPtId(user.getId());
                liaoTian.setPtName(user.getPtName());
                liaoTian.setPtPhone(user.getPtPhone());
                liaoTian.setChName(child.getCdName());
                liaoTian.setChPhone(child.getCdPhone());
                if (type.equalsIgnoreCase("WZ")) {
                    //说明上传的是文字信息
                    //XT/系统,HZ/孩子,JZ/家长
                    liaoTian.setLtContent(content);
                    realContent = content;
                } else if (type.equalsIgnoreCase("YY")) {
                    String upload = uploadYY(request, response);
                    liaoTian.setLtContent("sys/common/view/"+ upload);
                    liaoTian.setFilepath(upload);
                    if(type.equalsIgnoreCase("YY")){
                        liaoTian.setYyTime(time);
                    }
                    realContent = "sys/common/view/"+ upload;
                } else if (type.equalsIgnoreCase("IMG")) {
                    String upload = uploadImg(request, response);
                    liaoTian.setLtContent("sys/common/view/"+ upload);
                    liaoTian.setFilepath(upload);
                    if(type.equalsIgnoreCase("YY")){
                        liaoTian.setYyTime(time);
                    }
                    realContent = "sys/common/view/"+ upload;
                }

                liaoTianService.save(liaoTian);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", realContent);
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
     * 将语音消息标记为已读,因为它啥都不需要
     * @param id    消息id
     */
    @PostMapping(value = "/yyConfirm")
    @ApiOperation("将语音消息标记为已读")
    public void yyConfirm(@RequestParam("id") String id){
        LiaoTian liaoTian = new LiaoTian();
        liaoTian.setId(id);
        liaoTian.setLtRead("Y");
        liaoTianService.updateById(liaoTian);
    }


    /**
     * 每次请求查询记录
     * @param time      //最近的一次聊天记录的时间,格式年月日
     * @param token
     * @param childId   //孩子ID
     * @return
     */
    @PostMapping(value = "/ltList")
    @ApiOperation("聊天记录查询")
    public Result<JSONObject> ltList(@RequestParam("time") String time,
                                     @RequestParam("childid") String childId,
                                      @RequestHeader("token") String token) {

        String realContent = "";
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            Child child = childService.getById(childId);
            if (user != null) {

                List<Map<String,String>> list = liaoTianService.selectLtList(user.getId(),childId,time);

                liaoTianService.updateParentRead(childId);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", list);
                jsonObject.put("pavater",user.getPtAvater());
                jsonObject.put("cavater",child.getChildAvater());
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
     * 定时请求聊天记录
     * @param token
     * @param childId   //孩子ID
     * @return
     */
    @PostMapping(value = "/perTime")
    @ApiOperation("定时请求聊天记录")
    public Result<JSONObject> perTime(@RequestParam("childid") String childId,
                                     @RequestHeader("token") String token) {

        String realContent = "";
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            Child child = childService.getById(childId);
            if (user != null) {

                //查询前一分钟的数据
                String time = DateUtils.formatDate(new Date());

                List list = liaoTianService.list(new QueryWrapper<LiaoTian>().
                        eq("lt_pt_id",user.getId()).
                        eq("lt_ch_id",childId).
                        like("create_time",time+"%").
                        orderByAsc("create_time")
                        );

                liaoTianService.updateParentRead(childId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", list);
                jsonObject.put("pavater",user.getPtAvater());
                jsonObject.put("cavater",child.getChildAvater());
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



}
