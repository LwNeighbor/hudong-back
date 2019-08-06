package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
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
import org.jeecg.modules.hudong.xthf.entity.Xthf;
import org.jeecg.modules.hudong.xthf.service.IXthfService;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/front/child")
public class FrontChildController extends BaseController {


    @Autowired
    private IChildService childService;
    @Autowired
    private IXueXiService xueXiService;
    @Autowired
    private ILiaoTianService liaoTianService;
    @Autowired
    private IParentService parentService;
    @Autowired
    private IXthfService xthfService;


    /**
     * 登陆
     * @param phone
     * @param password
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation("登录接口")
    public Result<JSONObject> login(@RequestParam("phone") String phone,
                                    @RequestParam("password") String password) {
        Result<JSONObject> result = new Result<JSONObject>();

        Child child = childService.getOne(new QueryWrapper<Child>().eq("cd_phone", phone));
        if(child == null){
            //用户不存在
            result.error500("未找到该用户");
            return result;
        }else {
            //密码验证
            String userpassword = SecureUtil.md5(password);
            String syspassword = child.getCdPassword();
            if(!syspassword.equals(userpassword)) {
                result.error500("用户名或密码错误");
                return result;
            }
            JSONObject obj = new JSONObject();
            obj.put("id", child.getId());
            result.setResult(obj);
            result.success("登录成功");
        }
        return result;
    }


    /**
     * 该接口就是父母或者孩子学习界面定时或者进去时刷新的数据,按照时间排序
     *
     * @return
     */
    @PostMapping(value = "/xuexi")
    @ApiOperation("学习记录")
    public Result<JSONObject> home(HttpServletRequest request,@RequestParam("childid") String childid) {
        Result<JSONObject> result = new Result<JSONObject>();
        String xxOpion = "-1";
        try {
            Child child = childService.getById(childid);
            Parent user = parentService.getById(child.getPtId());
            List<XueXi> list2 = xueXiService.list(new QueryWrapper<XueXi>().
                    //select("id","xx_vtype","xx_ytype","xx_content","xx_read","xx_opion").
                            eq("xx_child_id", child.getId()).
                            like("create_time", DateUtils.formatDate(new Date())).
                            orderByAsc("create_time")
            );

            if(list2.size() > 0){
                xxOpion = list2.get(list2.size()-1).getXxOpion()+"="+list2.get(list2.size()-1).getXxKemu();
            }

            xueXiService.updateCread(childid);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", list2);
            jsonObject.put("xxOpion",xxOpion);
            jsonObject.put("cavater",child.getChildAvater());
            jsonObject.put("pavater",user.getPtAvater());
            jsonObject.put("favater","/plateform.png");
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;

        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 学习孩子发送信息,  如果是文件,应该也要有file:xx,都在request中,这里没有主动接收
     *
     * @param content //文本内容
     * @param type    //如果是文本,不要该条件 WZ/文字,YY/语音,IMG/图片
     * @param xxOpion //消息分类标记,说明该消息是这一天中的哪个时间段的
     * @param childId //孩子ID
     * @return
     */
    @PostMapping(value = "/childMsg")
    @ApiOperation("学习孩子发送信息")
    public Result<JSONObject> childMsg(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "content", required = false) String content,
                                        @RequestParam(value = "type", defaultValue = "WZ", required = false) String type,
                                        @RequestParam("xxOpion") String xxOpion,
                                        @RequestParam("childid") String childId,
                                        @RequestParam(value = "time",required = false) String time) {

        XueXi xue = new XueXi();
        XueXi xueXi = null;
        String realContent = "-1";
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            String[] xxOpions = xxOpion.split("=");
            Child child = childService.getById(childId);
            Parent user = parentService.getById(child.getPtId());
            xue.setXxParentId(child.getPtId());
            xue.setXxChildId(childId);
            xue.setXxYtype(type); //发的是文字
            xue.setXxOpion(xxOpions[0]);
            xue.setXxKemu(xxOpions[1]);
            xue.setXxVtype("HZ");
            xue.setPtName(user.getPtName());
            xue.setPtPhone(user.getPtPhone());
            xue.setChName(child.getCdName());
            xue.setChPhone(child.getCdPhone());
            xue.setCread("Y");
            if (type.equalsIgnoreCase("WZ")) {
                //说明上传的是文字信息
                //XT/系统,HZ/孩子,JZ/家长
                xue.setXxContent(content);
                realContent = content;
                //孩子发送OK消息,需要系统回复一个消息
                long day = DateUtil.betweenDay(new Date(), child.getCreateTime(), true);    //孩子注册的第几天

                List<Xthf> list = xthfService.list(new QueryWrapper<Xthf>().
                        eq("ps_time", day).
                        eq("kemu", xxOpions[1]).
                        eq("grade", child.getFlId())
                );

                if(list.size() > 0){

                    Xthf xthf = list.get(0);

                    xueXi = new XueXi();
                    xueXi.setTxType("0");   //提醒方式
                    xueXi.setChPhone(child.getCdPhone());
                    xueXi.setChName(child.getCdName());
                    xueXi.setPtPhone(user.getPtPhone());
                    xueXi.setPtName(user.getPtName());
                    xueXi.setXxKemu(xxOpions[1]);
                    xueXi.setXxOpion(xxOpions[0]);
                    xueXi.setXxContent(String.valueOf(xthf.getContent()));
                    xueXi.setXxYtype("WZ");
                    xueXi.setXxVtype("XT");
                    xueXi.setXxChildId(child.getId());
                    xueXi.setXxParentId(user.getId());

                }

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

            if(xueXiService.save(xue)){
                Thread.sleep(1000);
                xueXiService.save(xueXi);
            }

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("content", realContent);
            jsonObject.put("cavater",child.getChildAvater());
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.error500("网络开小差啦!");
            return result;
        }
    }


    @PostMapping(value = "/firstLt")
    @ApiOperation("聊天首页/定时刷新")
    public Result<JSONObject> firstLt(HttpServletRequest request,@RequestParam("childid") String id) {
        String realContent = "";
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Child child = childService.getById(id);
            Parent user = parentService.getById(child.getPtId());
            List<LiaoTian> list = liaoTianService.list(new QueryWrapper<LiaoTian>().
                    eq("LT_PT_ID", child.getPtId()).
                    eq("LT_CH_ID", id).
                    like("create_time",DateUtils.formatDate(new Date())).
                    orderByAsc("create_time"));

            //把这些消息都更新为孩子已读
            liaoTianService.updateCread(id);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", list);
            jsonObject.put("pavater",user.getPtAvater());
            jsonObject.put("cavater",child.getChildAvater());
            jsonObject.put("favater","/plateform.png");
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;

        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 孩子聊天发送信息,  如果是文件,应该也要有file:xx,都在request中,这里没有主动接收
     * @param content //文本内容
     * @param type    //如果是文本,不要该条件 WZ/文字,YY/语音,IMG/图片
     * @param childId //孩子ID
     * @return
     */
    @PostMapping(value = "/childLtMsg")
    @ApiOperation("孩子聊天发送信息")
    public Result<JSONObject> childLtMsg(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "content", required = false) String content,
                                        @RequestParam(value = "type",defaultValue = "WZ",required = false) String type,
                                        @RequestParam("childid") String childId,
                                         @RequestParam(value = "time",required = false)String time) {

        LiaoTian liaoTian = new LiaoTian();
        Result<JSONObject> result = new Result<JSONObject>();
        String realContent = "";
        try {
            Child child = childService.getById(childId);
            Parent user = parentService.getById(child.getPtId());
            liaoTian.setLtRead("N");
            liaoTian.setCread("Y");
            liaoTian.setLtVtype("HZ");
            liaoTian.setLtYtype(type);
            liaoTian.setLtChId(childId);
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

            liaoTian.setLtContent(realContent);
            liaoTianService.save(liaoTian);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", realContent);
            result.setResult(jsonObject);
            jsonObject.put("cavater",child.getChildAvater());
            result.success("操作成功");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.error500("网络开小差啦!");
            return result;
        }
    }


    /**
     * 回顾
     *
     * @return
     */
    @PostMapping(value = "/huigu")
    @ApiOperation("回顾")
    public Result<JSONObject> huigu(HttpServletRequest request,@RequestParam("childid") String childid) {
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Child child = childService.getById(childid);

            List<List<Map>> zlist = new ArrayList<>();
            List<Map> slist = new ArrayList<>();

            JSONObject jsonObject = new JSONObject();

            Map map = new HashMap();
            String nowDate = DateUtils.formatDate(new Date());
            String first = DateUtils.formatDate(DateUtil.offsetDay(new Date(),-1));
            String second = DateUtils.formatDate(DateUtil.offsetDay(new Date(),-2));
            String third = DateUtils.formatDate(DateUtil.offsetDay(new Date(),-3));
            String fourth = DateUtils.formatDate(DateUtil.offsetDay(new Date(),-4));

            List<String> timeList = new ArrayList<>();
            timeList.add(0,fourth);
            timeList.add(1,third);
            timeList.add(2,second);
            timeList.add(3,first);
            timeList.add(4,nowDate);

            for(String date : timeList) {
                List<XueXi> list1 = xueXiService.list(new QueryWrapper<XueXi>().
                        select("xx_opion","xx_kemu").
                        eq("xx_child_id", child.getId()).
                        eq("xx_vtype","HZ").
                        like("create_time", date+"%").
                        groupBy("XX_OPION","xx_kemu")
                );

                for(XueXi xueXi : list1){
                    Map map1 = new HashMap();
                    List<XueXi> list2 = xueXiService.list(new QueryWrapper<XueXi>().
                            //select("id","xx_vtype","xx_ytype","xx_content","xx_read","xx_opion").
                                    eq("xx_child_id", child.getId()).
                                    eq("xx_vtype","HZ").
                                    eq("xx_opion", xueXi.getXxOpion()).
                                    like("create_time", date+"%").
                                    orderByAsc("create_time")
                    );

                    map1.put("key", date + " "+ xueXi.getXxOpion()+" "+xueXi.getXxKemu());
                    map1.put("value", list2);
                    slist.add(map1);
                }
            }
            zlist.add(slist);
            jsonObject.put("data", zlist);
            jsonObject.put("avater",child.getChildAvater());
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;

        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    /**
     * 查询未读消息
     * @param childid    孩子ID
     */
    @PostMapping(value = "/readCount")
    @ApiOperation("查询未读消息")
    public Result<JSONObject> readCount(@RequestParam("childid") String childid){


        Result<JSONObject> result = new Result<JSONObject>();

        try {

            List<XueXi> list = xueXiService.list(new QueryWrapper<XueXi>().
                    eq("cread", 'N').
                    eq("XX_CHILD_ID", childid).
                    orderByDesc("create_time")
            );

            List<LiaoTian> list1 = liaoTianService.list(new QueryWrapper<LiaoTian>().
                    eq("cread", 'N').
                    eq("LT_CH_ID", childid).
                    orderByDesc("create_time")
            );

            String type = "-1";
            XueXi xi = new XueXi();

            for(XueXi xueXi : list){
                if(xueXi.getXxVtype().equalsIgnoreCase("XT")){
                    type = xueXi.getTxType();
                    if(!type.equalsIgnoreCase("0")){
                        xi = xueXi;
                        break;
                    }
                }
            }

            xi.setTxType("0");
            xueXiService.updateById(xi);

            String xType = "-1";
            String lType = "-1";
            String xContent = "-1";
            String lContent = "-1";


            if(list.size() > 0){
                XueXi xueXi = list.get(0);
                xType = xueXi.getXxYtype();
                xContent = xueXi.getXxContent();

            }

            if(list1.size() > 0){
                LiaoTian liaoTian = list1.get(0);
                lType = liaoTian.getLtYtype();
                lContent = liaoTian.getLtContent();
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("xCount",list.size());
            jsonObject.put("lCount",list1.size());
            jsonObject.put("xType",xType);
            jsonObject.put("lType",lType);
            jsonObject.put("xContent",xContent);
            jsonObject.put("lContent",lContent);
            jsonObject.put("txType",type);

            result.setResult(jsonObject);
            result.success("操作成功");
            return result;

        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }


    }

}
