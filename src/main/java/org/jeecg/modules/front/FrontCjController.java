package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;
import org.jeecg.modules.hudong.cj.entity.Cj;
import org.jeecg.modules.hudong.cj.service.ICjService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.service.IParentService;
import org.jeecg.modules.hudong.xk.entity.XueKe;
import org.jeecg.modules.hudong.xk.service.IXueKeService;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/front/cj")
public class FrontCjController extends BaseController {


    @Autowired
    private ICjService cjService;
    @Autowired
    private IChildService childService;
    @Autowired
    private IXueKeService xueKeService;
    @Autowired
    private IParentService parentService;
    @Autowired
    private IXueXiService xueXiService;


    @RequestMapping(value = "/cName", method = RequestMethod.POST)
    @ApiOperation("孩子姓名")
    public Result<JSONObject> cName(@RequestHeader("token") String token) {

        Result<JSONObject> result = new Result<JSONObject>();

        try {
            Parent user = verify(token);
            if (user != null) {

                List<Child> pt_id = childService.list(new QueryWrapper<Child>().eq("pt_id", user.getId()));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", pt_id);
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


    @RequestMapping(value = "/xueke", method = RequestMethod.POST)
    @ApiOperation("学科")
    public Result<JSONObject> xueke(@RequestParam("childid") String childid) {

        Result<JSONObject> result = new Result<JSONObject>();

        try {

            Child child = childService.getById(childid);


            List<XueKe> list = xueKeService.list(new QueryWrapper<XueKe>().eq("fl_id",child.getFlId()));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", list);
            result.setResult(jsonObject);
            result.success("操作成功");
            return result;

        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    @RequestMapping(value = "/lrcj", method = RequestMethod.POST)
    @ApiOperation("录入成绩")
    public Result<JSONObject> lrcj(@RequestHeader("token") String token,
                                   @RequestParam("childid") String childid,
                                   @RequestBody List<Cj> list) {

        Result<JSONObject> result = new Result<JSONObject>();
        try {

            Parent user = verify(token);
            if (user != null) {
                Child child = childService.getById(childid);


                for (Cj cj : list) {
                    cj.setCjParentId(user.getId());
                    cj.setCjCdId(childid);
                    cj.setCjCdName(child.getCdName());
                    cj.setCjPhone(child.getCdPhone());

                    Cj one = cjService.getOne(new QueryWrapper<Cj>().
                            eq("cj_cd_id", cj.getCjCdId()).
                            eq("cj_xk_id", cj.getCjXkId()).
                            eq("cj_time", cj.getCjTime())
                    );
                    if(one != null) {
                        cj.setId(one.getId());
                        cjService.updateById(cj);
                    }else {
                        cjService.save(cj);
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


    @RequestMapping(value = "/cjqx", method = RequestMethod.POST)
    @ApiOperation("查看成绩曲线")
    public Result<JSONObject> cjqx(@RequestHeader("token") String token) {

        Result<JSONObject> result = new Result<JSONObject>();
        try {

            List zlist = new ArrayList();

            Parent user = verify(token);
            if (user != null) {

                List<Child> childList = childService.list(new QueryWrapper<Child>().eq("pt_id", user.getId()));

                List<XueKe> xueKeList = xueKeService.list();

                List<Cj> list = new ArrayList<>();

                //默认就是周
                if (childList.size() > 0 && xueKeList.size() > 0) {
                    //说明查询的是周
                    String begin = DateUtils.formatDate(DateUtil.offsetDay(new Date(), -7));
                    String now = DateUtils.formatDate(new Date());

                    list = cjService.list(new QueryWrapper<Cj>().eq("cj_cd_id", childList.get(0).getId()).
                            eq("cj_xk_id", xueKeList.get(0).getId()).
                            orderByAsc("cj_time").
                            between("cj_time", begin, now)
                    );
                }


                list.stream().forEach(cj -> {

                    List<String> list1 = new ArrayList();
                    Map map1 = new HashMap();
                    map1.put("time", cj.getCjTime());
                    map1.put("value", cj.getCjNumber());
                    zlist.add(map1);
                });


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", zlist);
                jsonObject.put("child", childList);
                jsonObject.put("xueke", xueKeList);
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
     * 曲线查询
     *
     * @param token
     * @param childid
     * @param xuekeId
     * @param type    w/周  m/月        默认周
     * @return
     */
    @RequestMapping(value = "/cxqx", method = RequestMethod.POST)
    @ApiOperation("曲线查询")
    public Result<JSONObject> cxqx(@RequestHeader("token") String token,
                                   @RequestParam("childid") String childid,
                                   @RequestParam("xuekeId") String xuekeId,
                                   @RequestParam(value = "type", required = false, defaultValue = "w") String type) {

        Result<JSONObject> result = new Result<JSONObject>();
        try {


            List<Cj> list = new ArrayList<>();
            List<Map<String, String>> n_list = new LinkedList();

            Parent user = verify(token);
            if (user != null) {

                String begin = "";
                String now = "";

                if (type.equalsIgnoreCase("w")) {
                    //说明查询的是周
                    begin = DateUtils.formatDate(DateUtil.offsetDay(new Date(), -7));
                    now = DateUtils.formatDate(new Date());
                }else if(type.equalsIgnoreCase("m")){
                    begin = DateUtils.formatDate(DateUtil.offsetDay(new Date(), -30));
                    now = DateUtils.formatDate(new Date());
                }

                list = cjService.list(new QueryWrapper<Cj>().eq("cj_cd_id", childid).
                        eq("cj_xk_id", xuekeId).
                        orderByAsc("cj_time").
                        between("cj_time", begin, now)
                );


                list.stream().forEach(cj -> {

                    List<String> list1 = new ArrayList();
                    Map map1 = new HashMap();
                    map1.put("time", cj.getCjTime());
                    map1.put("value", cj.getCjNumber());
                    n_list.add(map1);
                });


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", n_list);
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
     * 第一次进入平台综合成绩
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/ptcj", method = RequestMethod.POST)
    @ApiOperation("第一次进入平台综合成绩")
    public Result<JSONObject> ptcj(@RequestHeader("token") String token) {

        Result<JSONObject> result = new Result<JSONObject>();
        try {

            List<Map<String, String>> n_list = new LinkedList();
            List<Child> childList = new ArrayList<>();

            Parent user = verify(token);
            if (user != null) {

                int hc = 0;    //这个月孩子的发言总数
                int zc = 0;    //这个月总的发言总数
                double zdiv = 0;
                childList = childService.list(new QueryWrapper<Child>().eq("pt_id", user.getId()));
                if (childList.size() > 0) {

                    for (int i = -30; i < 0; i++) {

                        Map map = new HashMap();
                        //默认查询的是周
                        String time = DateUtils.formatDate(DateUtil.offsetDay(new Date(), i));

                        map.put("time", time);
                        List<Map<String, String>> xueXiList = xueXiService.selectGroupByType(childList.get(0).getId(), time + "%");


                        int hcount = 0;     //孩子
                        int zcount = 0;     //家长
                        double div = 0;

                        for (Map map1 : xueXiList) {

                            String type = (String) map1.get("type");
                            if (type.equalsIgnoreCase("HZ")) {
                                hcount = Integer.parseInt(String.valueOf(map1.get("count")));
                                zcount += Integer.parseInt(String.valueOf(map1.get("count")));
                                hc += Integer.parseInt(String.valueOf(map1.get("count")));
                                zc += Integer.parseInt(String.valueOf(map1.get("count")));
                            } else {
                                zcount += Integer.parseInt(String.valueOf(map1.get("count")));
                                zc += Integer.parseInt(String.valueOf(map1.get("count")));
                            }

                        }

                        if (zcount != 0) {
                            div = NumberUtil.div(hcount, zcount, 2) * 100;
                        }

                        map.put("value", div);
                        n_list.add(map);

                    }

                }

                if (zc != 0) {
                    zdiv = NumberUtil.div(hc, zc, 2) * 100;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("child", childList);
                jsonObject.put("data", n_list);
                jsonObject.put("pg", zdiv);
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
     * 平台综合成绩切换孩子
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/ptcqh", method = RequestMethod.POST)
    @ApiOperation("平台综合成绩切换孩子")
    public Result<JSONObject> ptcqh(@RequestHeader("token") String token,
                                    @RequestParam(value = "type", required = false, defaultValue = "m") String types,
                                    @RequestParam("childid") String childid) {

        Result<JSONObject> result = new Result<JSONObject>();
        try {

            List<Map<String, String>> n_list = new LinkedList();

            Parent user = verify(token);
            if (user != null) {
                int hcount = 0;     //孩子
                int zcount = 0;     //家长
                double div = 0;
                int mz = 0;

                if (types.equalsIgnoreCase("m")) {
                    mz = -30;
                } else if (types.equalsIgnoreCase("w")) {
                    mz = -7;
                }

                for (int i = mz; i < 0; i++) {

                    Map map = new HashMap();
                    //默认查询的是月
                    String time = DateUtils.formatDate(DateUtil.offsetDay(new Date(), i));

                    map.put("time", time);
                    List<Map<String, String>> xueXiList = xueXiService.selectGroupByType(childid, time + "%");


                    for (Map map1 : xueXiList) {

                        String type = (String) map1.get("type");
                        if (type.equalsIgnoreCase("HZ")) {
                            hcount = Integer.parseInt(String.valueOf(map1.get("count")));
                            zcount += Integer.parseInt(String.valueOf(map1.get("count")));
                        } else {
                            zcount += Integer.parseInt(String.valueOf(map1.get("count")));
                        }
                    }

                    if (zcount != 0) {
                        div = NumberUtil.div(hcount, zcount, 2) * 100;
                    }

                    map.put("value", div);
                    n_list.add(map);
                }


                String begin = DateUtil.format(DateUtil.beginOfMonth(DateUtil.offsetMonth(new Date(), -1)), "yyyy-MM-dd");
                String end = DateUtil.format(DateUtil.beginOfMonth(new Date()), "yyyy-MM-dd");
                List<Map<String, String>> xueXiList = xueXiService.selectCountByMonth(childid, begin, end);
                int c = 0;
                int z = 0;
                double czdiv = 0;
                if (xueXiList.size() > 0) {
                    for (Map stringStringMap : xueXiList) {
                        if (stringStringMap.get("type").equals("HZ")) {
                            c += Integer.parseInt(String.valueOf(stringStringMap.get("count")));
                        }
                        z += Integer.parseInt(String.valueOf(stringStringMap.get("count")));

                    }

                    if (z != 0) {
                        czdiv = NumberUtil.div(c, z, 2) * 100;
                    }
                }


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", n_list);
                jsonObject.put("pg", czdiv);
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
