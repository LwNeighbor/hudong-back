package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.xtxi.entity.Xtxi;
import org.jeecg.modules.hudong.xtxi.service.IXtxiService;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/front/ketang")
public class ParentKeTangShouHuoController extends BaseController {

    @Autowired
    private IChildService childService;
    @Autowired
    private IXueXiService xueXiService;
    @Autowired
    private IXtxiService xtxiService;

    /**
     * 该接口就是父母或者孩子学习界面定时或者进去时刷新的数据,按照时间排序
     *
     * @return
     */
    @PostMapping(value = "")
    @ApiOperation("学习聊天记录")
    public Result<JSONObject> home(@RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            String childAvater = "";
            if (user != null) {

                List<Child> list = childService.list(new QueryWrapper<Child>().eq("pt_id", user.getId()).
                        orderByAsc("create_time"));

                List<List<Map>> zlist = new ArrayList<>();
                List<Map> flist = new ArrayList<>();
                List<Map> slist = new ArrayList<>();
                list.stream().forEach(child -> {
                    Map cmap = new HashMap();
                    cmap.put("id", child.getId());
                    cmap.put("name", child.getCdName());
                    flist.add(cmap);
                });
                if (list.size() > 0) {
                    childAvater = list.get(0).getChildAvater();
                }
                zlist.add(flist);
                JSONObject jsonObject = new JSONObject();
                if (list.size() > 0) {
                    Map map = new HashMap();
                    Child child = list.get(0);

                    List<Xtxi> xtxiList = xtxiService.list(new QueryWrapper<Xtxi>().
                            select("id", "title", "ps_time", "introduce").
                            like("ps_time", DateUtils.formatDate(new Date()))
                    );

                    jsonObject.put("xtxi", xtxiList);

                    String maxCreateTime = xueXiService.selectMaxCreateTime(child.getId());
                    if (maxCreateTime != null) {
                        maxCreateTime = maxCreateTime.substring(0, 10);

                        List<XueXi> list1 = xueXiService.list(new QueryWrapper<XueXi>().
                                select("xx_opion", "xx_kemu").
                                eq("xx_child_id", child.getId()).
                                eq("xx_vtype", "HZ").
                                like("create_time", maxCreateTime).
                                groupBy("XX_OPION")
                        );

                        for (XueXi xueXi : list1) {
                            Map map1 = new HashMap();
                            List<XueXi> list2 = xueXiService.list(new QueryWrapper<XueXi>().
                                    //select("id","xx_vtype","xx_ytype","xx_content","xx_read","xx_opion").
                                            eq("xx_child_id", child.getId()).
                                            eq("xx_vtype", "HZ").
                                            eq("xx_opion", xueXi.getXxOpion()).
                                            like("create_time", maxCreateTime).
                                            orderByAsc("create_time")
                            );

                            map1.put("key", xueXi.getXxOpion());
                            map1.put("value", list2);
                            slist.add(map1);
                        }

                        jsonObject.put("time", maxCreateTime);
                    } else {
                        jsonObject.put("time", "-1");
                    }
                }
                zlist.add(slist);
                jsonObject.put("data", zlist);
                jsonObject.put("avater", childAvater);
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
     * 系统消息详情
     *
     * @return
     */
    @PostMapping(value = "/xtxq")
    @ApiOperation("系统消息详情")
    public Result<JSONObject> xtxq(@RequestParam("id") String id) {
        Result<JSONObject> result = new Result<JSONObject>();
        Xtxi byId = xtxiService.getById(id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("xq", byId);
        result.setResult(jsonObject);
        result.success("操作成功");
        return result;
    }

    /**
     * 按时间查询孩子的记录
     *
     * @return
     */
    @PostMapping(value = "/cRecord")
    @ApiOperation("系统消息详情")
    public Result<JSONObject> cRecord(@RequestParam("time") String time,
                                      @RequestParam("childid") String childid,
                                      @RequestHeader("token") String token) {

        Result<JSONObject> result = new Result<JSONObject>();
        try {
            Parent user = verify(token);
            List<Map> slist = new ArrayList<>();
            if (user != null) {

                Child child = childService.getById(childid);

                List<XueXi> list1 = xueXiService.list(new QueryWrapper<XueXi>().
                        eq("xx_child_id", child.getId()).
                        eq("xx_vtype", "HZ").
                        like("create_time", time).
                        groupBy("XX_OPION")
                );

                for (XueXi xueXi : list1) {
                    Map map1 = new HashMap();
                    List<XueXi> list2 = xueXiService.list(new QueryWrapper<XueXi>().
                            //select("id","xx_vtype","xx_ytype","xx_content","xx_read","xx_opion").
                                    eq("xx_child_id", child.getId()).
                                    eq("xx_vtype", "HZ").
                                    eq("xx_opion", xueXi.getXxOpion()).
                                    like("create_time", time).
                                    orderByAsc("create_time")
                    );

                    map1.put("key", xueXi.getXxOpion());
                    map1.put("value", list2);
                    slist.add(map1);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", slist);
                jsonObject.put("avater", child.getChildAvater());
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
     * 该接口就是父母或者孩子学习界面定时或者进去时刷新的数据,按照时间排序
     *
     * @return
     */
    @PostMapping(value = "/detail")
    @ApiOperation("学习聊天记录")
    public Result<JSONObject> detail(@RequestHeader("token") String token,
                                     @RequestParam("childid") String childid) {
        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            String childAvater = "";
            if (user != null) {

                List<Map> slist = new ArrayList<>();

                JSONObject jsonObject = new JSONObject();

                Map map = new HashMap();
                Child child = childService.getById(childid);

                List<Xtxi> xtxiList = xtxiService.list(new QueryWrapper<Xtxi>().
                        select("id", "title", "ps_time", "introduce").
                        like("ps_time", DateUtils.formatDate(new Date()))
                );

                jsonObject.put("xtxi", xtxiList);
                String maxCreateTime = xueXiService.selectMaxCreateTime(child.getId());
                if (maxCreateTime != null) {
                    maxCreateTime = maxCreateTime.substring(0, 10);

                    List<XueXi> list1 = xueXiService.list(new QueryWrapper<XueXi>().
                            select("xx_opion", "xx_kemu").
                            eq("xx_child_id", child.getId()).
                            eq("xx_vtype", "HZ").
                            like("create_time", maxCreateTime).
                            groupBy("XX_OPION")
                    );

                    for (XueXi xueXi : list1) {
                        Map map1 = new HashMap();
                        List<XueXi> list2 = xueXiService.list(new QueryWrapper<XueXi>().
                                //select("id","xx_vtype","xx_ytype","xx_content","xx_read","xx_opion").
                                        eq("xx_child_id", child.getId()).
                                        eq("xx_vtype", "HZ").
                                        eq("xx_opion", xueXi.getXxOpion()).
                                        like("create_time", maxCreateTime).
                                        orderByAsc("create_time")
                        );

                        map1.put("key", xueXi.getXxOpion());
                        map1.put("value", list2);
                        slist.add(map1);
                    }

                    jsonObject.put("time", maxCreateTime);
                } else {
                    jsonObject.put("time", DateUtil.formatDate(new Date()));
                }

                jsonObject.put("data", slist);
                jsonObject.put("avater", childAvater);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (
                Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

}
