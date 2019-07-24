package org.jeecg.modules.hudong.msm.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hudong.mqx.service.IMqXQingService;
import org.jeecg.modules.hudong.msfenlei.entity.MsFenLi;
import org.jeecg.modules.hudong.msfenlei.service.IMsFenLiService;
import org.jeecg.modules.hudong.msm.entity.Msm;
import org.jeecg.modules.hudong.msm.service.IMsmService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

/**
 * @Title: Controller
 * @Description: 模式描述
 * @author： jeecg-boot
 * @date： 2019-06-20
 * @version： V1.0
 */
@RestController
@RequestMapping("/msm/msm")
@Slf4j
public class MsmController {

    @Autowired
    private IMsFenLiService msFenLiService;
    @Autowired
    private IMsmService msmService;
    @Autowired
    private IMqXQingService mqXQingService;

    /**
     * 分页列表查询
     *
     * @param msm
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/list")
    public Result<IPage<Msm>> queryPageList(Msm msm,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                            HttpServletRequest req) {
        Result<IPage<Msm>> result = new Result<IPage<Msm>>();
        QueryWrapper<Msm> queryWrapper = QueryGenerator.initQueryWrapper(msm, req.getParameterMap());
        Page<Msm> page = new Page<Msm>(pageNo, pageSize);
        IPage<Msm> pageList = msmService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param msm
     * @return
     */
    @PostMapping(value = "/add")
    public Result<Msm> add(@RequestBody Msm msm) {
        Result<Msm> result = new Result<Msm>();
        try {
            msmService.save(msm);
            result.success("添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param msm
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<Msm> edit(@RequestBody Msm msm) {
        Result<Msm> result = new Result<Msm>();
        Msm msmEntity = msmService.getById(msm.getId());
        if (msmEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = msmService.updateById(msm);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Result<Msm> delete(@RequestParam(name = "id", required = true) String id) {
        Result<Msm> result = new Result<Msm>();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("ms_id", id);
        int count = mqXQingService.count(queryWrapper);
        if (count > 0) {
            result.error500("该分类下还有详情，无法删除");
        }
        Msm msm = msmService.getById(id);
        if (msm == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = msmService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        }

        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<Msm> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Msm> result = new Result<Msm>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.msmService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryById")
    public Result<Msm> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<Msm> result = new Result<Msm>();
        Msm msm = msmService.getById(id);
        if (msm == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(msm);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<Msm> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                Msm msm = JSON.parseObject(deString, Msm.class);
                queryWrapper = QueryGenerator.initQueryWrapper(msm, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<Msm> pageList = msmService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "模式描述列表");
        mv.addObject(NormalExcelConstants.CLASS, Msm.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("模式描述列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
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
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Msm> listMsms = ExcelImportUtil.importExcel(file.getInputStream(), Msm.class, params);
                for (Msm msmExcel : listMsms) {
                    msmService.save(msmExcel);
                }
                return Result.ok("文件导入成功！数据行数：" + listMsms.size());
            } catch (Exception e) {
                log.error(e.getMessage());
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
     * 孩子选择模式,级联选择
     *
     * @param
     * @param
     * @return
     */
    @GetMapping(value = "/msListUrl")
    public Result<Object> msListUrl() {
        Result<Object> result = new Result<Object>();
        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> zlist = new ArrayList<>();
        List<MsFenLi> list = msFenLiService.list();
        for (MsFenLi msFenLi : list) {
            Map map = new HashMap();
            map.put("value", msFenLi.getId());
            map.put("label", msFenLi.getFlName());

            List<Msm> msms = msmService.list(new QueryWrapper<Msm>().eq("fl_id", msFenLi.getId()));
            List list2 = new ArrayList();
            msms.stream().forEach(msm -> {
                Map chmap = new HashMap();
                chmap.put("value",msm.getId());
                chmap.put("label",msm.getMsName());
                list2.add(chmap);
            });
            map.put("children", list2);
            zlist.add(map);
        }
        jsonObject.put("options",zlist);
        result.setSuccess(true);
        result.setResult(jsonObject);
        return result;
    }


    /**
     * 模式描述中的下拉框需要显示的
     * @param
     * @param
     * @return
     */
    @GetMapping(value = "/valueList")
    public Result<List<Map<String,Object>>> valueList(HttpServletRequest req) {
        Result<List<Map<String,Object>>> result = new Result<List<Map<String,Object>>>();
        List<MsFenLi> flist = msFenLiService.list();

        List<Map<String,Object>> zlist = new ArrayList<>();
        flist.stream().forEach(fenLi -> {
            List<Map<String,String>> list1 = new ArrayList<>();
            List<Msm> list = msmService.list(new QueryWrapper<Msm>().eq("fl_id",fenLi.getId()));
            Map map = new HashMap();
            map.put("value",fenLi.getId());
            map.put("text",fenLi.getFlName());

            list.stream().forEach(msfenli->{
                Map map1 = new HashMap();
                map1.put("value",msfenli.getId());
                map1.put("text",msfenli.getMsName());
                list1.add(map1);
            });
            map.put("children",list1);
            zlist.add(map);
        });
        result.setSuccess(true);
        result.setResult(zlist);
        return result;
    }

}
