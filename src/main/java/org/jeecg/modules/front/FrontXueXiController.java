package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.fd.entity.Fd;
import org.jeecg.modules.hudong.fd.service.IFdService;
import org.jeecg.modules.hudong.mqx.service.IMqXQingService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/front/xuexi")
public class FrontXueXiController extends BaseController {


    @Autowired
    private IXueXiService xueXiService;
    @Autowired
    private IMqXQingService mqXQingService;
    @Autowired
    private IChildService childService;
    @Autowired
    private IFdService fdService;


    /**
     * 指定孩子聊天记录,
     *
     * @return
     */
    @PostMapping(value = "/childRd")
    @ApiOperation("指定孩子聊天记录")
    public Result<JSONObject> childRd(@RequestHeader("token") String token,@RequestParam("childid") String childid) {
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            if (user != null) {
                Child child = childService.getById(childid);
                List<Map> slist = new ArrayList<>();
                DateUtils.formatDate(new Date());
                String xxOpion = "-1";

                Map map = new HashMap();
                List<XueXi> list1 = xueXiService.list(new QueryWrapper<XueXi>().
                        select("xx_opion","xx_kemu").
                        eq("xx_child_id", child.getId()).
                        like("create_time", DateUtils.formatDate(new Date())+"%").
                        orderByAsc("create_time").
                        groupBy("xx_opion","xx_kemu"));
                if(list1.size() > 0){
                    xxOpion = list1.get(list1.size()-1).getXxOpion()+"="+list1.get(list1.size()-1).getXxKemu();
                }

                for (XueXi xueXi : list1) {
                    Map map1 = new HashMap();
                    List<XueXi> list2 = xueXiService.list(new QueryWrapper<XueXi>().
                            //select("id","xx_vtype","xx_ytype","xx_content","xx_read","xx_opion").
                                    eq("xx_child_id", child.getId()).
                                    eq("xx_opion", xueXi.getXxOpion()).
                                    like("create_time", DateUtils.formatDate(new Date())+"%").
                                    orderByAsc("create_time")
                    );

                    map1.put("key", xueXi.getXxOpion()+" ");
                    map1.put("value", list2);
                    slist.add(map1);
                }


                xueXiService.updateParentRead(childid);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", slist);
                jsonObject.put("pavater",user.getPtAvater());
                jsonObject.put("cavater",child.getChildAvater());
                jsonObject.put("xxOpion",xxOpion);      //包含xxopion和科目信息
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
     * 定时请求发送消息
     *
     * @return
     */
    @PostMapping(value = "/timerMsg")
    @ApiOperation("定时请求发送消息")
    public Result<JSONObject> timerMsg(@RequestHeader("token") String token,
                                       @RequestParam("childid") String childid) {
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            Child child = childService.getById(childid);
            if (user != null) {

                List<Map> slist = new ArrayList<>();
                DateUtils.formatDate(new Date());
                String xxOpion = "-1";

                Map map = new HashMap();
                String date = DateUtils.formatDate(new Date());
                List<XueXi> list1 = xueXiService.list(new QueryWrapper<XueXi>().
                        select("xx_opion","xx_kemu").
                        eq("xx_child_id", child.getId()).
                        like("create_time", date+"%").
                        orderByAsc("create_time").
                        groupBy("xx_opion","xx_kemu"));
                if(list1.size() > 0){
                    xxOpion = list1.get(list1.size()-1).getXxOpion()+"="+list1.get(list1.size()-1).getXxKemu();
                }

                for (XueXi xueXi : list1) {
                    Map map1 = new HashMap();
                    List<XueXi> list2 = xueXiService.list(new QueryWrapper<XueXi>().
                            //select("id","xx_vtype","xx_ytype","xx_content","xx_read","xx_opion").
                                    eq("xx_child_id", child.getId()).
                                    eq("xx_opion", xueXi.getXxOpion()).
                                    like("create_time", date+"%").
                                    orderByAsc("create_time")
                    );

                    map1.put("key", xueXi.getXxOpion()+" ");
                    map1.put("value", list2);
                    slist.add(map1);
                }

                xueXiService.updateParentRead(childid);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", slist);
                jsonObject.put("pavater",user.getPtAvater());
                jsonObject.put("cavater",child.getChildAvater());
                jsonObject.put("xxOpion",xxOpion);      //包含xxopion和科目信息
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
     * @param xxOpion //消息分类标记,说明该消息是这一天中的哪个时间段的
     * @param childId //孩子ID
     * @return
     */
    @PostMapping(value = "/parentMsg")
    @ApiOperation("父母发送信息")
    public Result<JSONObject> parentMsg(HttpServletRequest request, HttpServletResponse response,
                                        @RequestHeader("token") String token,
                                        @RequestParam(value = "content", required = false) String content,
                                        @RequestParam(value = "type", defaultValue = "WZ", required = false) String type,
                                        @RequestParam("xxOpion") String xxOpion,
                                        @RequestParam("childid") String childId,
                                        @RequestParam(value = "time",required = false) String time) {

        XueXi xue = new XueXi();
        String realContent = "-1";
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            Child child = childService.getById(childId);
            String[] xxOpions = xxOpion.split("=");
            if (user != null) {
                xue.setXxParentId(user.getId());
                xue.setXxChildId(childId);
                xue.setXxYtype(type); //发的是文字
                xue.setXxOpion(xxOpions[0]);
                xue.setXxKemu(xxOpions[1]);
                xue.setXxVtype("JZ");
                xue.setPtName(user.getPtName());
                xue.setPtPhone(user.getPtPhone());
                xue.setChName(child.getCdName());
                xue.setChPhone(child.getCdPhone());
                xue.setXxRead("Y");
                if (type.equalsIgnoreCase("WZ")) {
                    //说明上传的是文字信息
                    //XT/系统,HZ/孩子,JZ/家长
                    xue.setXxContent(content);
                    realContent = content;
                } else if (type.equalsIgnoreCase("YY")) {
                    String upload = uploadYY(request, response);
                    xue.setXxContent("sys/common/view/"+ upload);
                    xue.setFilepath(upload);
                    if(type.equalsIgnoreCase("YY")){
                        xue.setYyTime(time);
                    }
                    realContent = "sys/common/view/"+ upload;
                }else if(type.equalsIgnoreCase("IMG")){
                    String upload = uploadImg(request, response);
                    xue.setXxContent("sys/common/view/"+ upload);
                    xue.setFilepath(upload);
                    if(type.equalsIgnoreCase("YY")){
                        xue.setYyTime(time);
                    }
                    realContent = "sys/common/view/"+ upload;
                }

                xueXiService.save(xue);
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
        XueXi xueXi = new XueXi();
        xueXi.setId(id);
        xueXi.setXxRead("Y");
        xueXiService.updateById(xueXi);
    }

    /**
     * 父母端离开时
     * @Param chid  孩子id
     */
    @PostMapping(value = "/leave")
    @ApiOperation("父母端离开时")
    public Result<JSONObject> leave(@RequestParam("childid")String childid,@RequestHeader("token") String token){
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            if (user != null) {
                int count = 0;
                int count1 = 0;
                count = xueXiService.count(new QueryWrapper<XueXi>().
                        eq("feedback", "N").
                        eq("xx_parent_id", user.getId()).
                        eq("xx_child_id",childid).
                        eq("xx_vtype", "XT").
                        like("create_time", DateUtil.format(new Date(), "yyyy-MM-dd")+"%"));

                if(count == 0){

                    count1 = fdService.count(new QueryWrapper<Fd>().
                            eq("ch_id", childid).
                            eq("is_fd", "N").
                            like("create_time", DateUtil.format(new Date(), "yyyy-MM-dd")+"%")
                    );
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("count", count+count1);
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
     * 确认将反馈
     * @param childid    孩子ID
     */
    @PostMapping(value = "/confirmFd")
    @ApiOperation("确认将反馈")
    public Result<JSONObject> confirmFd(@RequestParam("childid") String childid, @RequestHeader("token") String token){
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            if (user != null) {
                String time = DateUtil.format(new Date(), "yyyy-MM-dd");
                List<XueXi> list1 = xueXiService.list(new QueryWrapper<XueXi>().
                        eq("feedback", "N").
                        eq("xx_parent_id", user.getId()).
                        eq("xx_child_id", childid).
                        ne("xx_vtype","JZ").
                        like("create_time",time + "%").
                        orderByAsc("create_time")
                );

                XueXi xueXi = null;
                XueXi xueXi1 = null;
                for(int i=0;i<list1.size();i++){
                    xueXi = list1.get(i);
                    if(xueXi.getXxVtype().equals("XT")){
                        if((i+1) < list1.size()){
                            if(list1.get(i+1).getXxVtype().equals("HZ")){
                                xueXi1 = list1.get(i+1);
                            }
                        }
                        fdService.saveFdAndUpdateXuexi(xueXi,xueXi1,user,childid);
                        xueXi = null;
                        xueXi1 = null;
                    }else {
                        continue;
                    }
                }
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
     * 反馈页面
     * @param childid    孩子ID
     */
    @PostMapping(value = "/fdList")
    @ApiOperation("反馈页面")
    public Result<JSONObject> fdList(@RequestParam("childid") String childid,@RequestHeader("token") String token){
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            if (user != null) {

                List<Fd> list = fdService.list(new QueryWrapper<Fd>().
                        eq("fd_pt_id", user.getId()).
                        eq("ch_id",childid).
                        like("create_time", DateUtil.format(new Date(), "yyyy-MM-dd")+"%"));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fd",list);
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
     * 提交反馈内容
     * @param id           记录id
     * @param fdcontent    反馈内容
     * @param fdtime       反馈时间
     */
    @PostMapping(value = "/saveFd")
    @ApiOperation("提交反馈内容")
    public Result<JSONObject> saveFd(@RequestParam("id") String id,
                                     @RequestParam("fdcontent") String fdcontent,
                                     @RequestParam("fdtime") String fdtime,
                                     @RequestHeader("token") String token){
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            if (user != null) {
                Fd fd = new Fd();
                fd.setId(id);
                fd.setFdContent(fdcontent);
                fd.setFdTime(fdtime);
                fd.setIsFd("Y");
                boolean b = fdService.updateById(fd);
                if(b){
                    result.success("操作成功");
                }
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
