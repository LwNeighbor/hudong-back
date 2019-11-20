package org.jeecg.modules.hudong.mqx.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hudong.mqx.entity.MqXQing;
import org.jeecg.modules.hudong.mqx.service.IMqXQingService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.hudong.xk.entity.XueKe;
import org.jeecg.modules.hudong.xk.service.IXueKeService;
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
 * @Description: 模式详情
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@RestController
@RequestMapping("/mqx/mqXQing")
@Slf4j
public class MqXQingController {
	@Autowired
	private IMqXQingService mqXQingService;
	@Autowired
	private IXueKeService xueKeService;
	
	/**
	  * 分页列表查询
	 * @param mqXQing
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<MqXQing>> queryPageList(MqXQing mqXQing,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<MqXQing>> result = new Result<IPage<MqXQing>>();
		QueryWrapper<MqXQing> queryWrapper = QueryGenerator.initQueryWrapper(mqXQing, req.getParameterMap());
		Page<MqXQing> page = new Page<MqXQing>(pageNo, pageSize);
		queryWrapper.orderByAsc("register_day");
		IPage<MqXQing> pageList = mqXQingService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	 /**
	  * 分页列表查询
	  * @param
	  * @param req
	  * @return
	  */
	 @GetMapping(value = "/inlineList")
	 public Result<List<MqXQing>> inlineList(MqXQing mqXQing,HttpServletRequest req) {
		 Result<List<MqXQing>> result = new Result<List<MqXQing>>();
		 QueryWrapper<MqXQing> queryWrapper = QueryGenerator.initQueryWrapper(mqXQing, req.getParameterMap());
		 queryWrapper.orderByAsc("register_day");
		 List<MqXQing> pageList = mqXQingService.list(queryWrapper);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }

	
	/**
	  *   添加
	 * @param mqXQing
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<MqXQing> add(@RequestBody MqXQing mqXQing) {
		Result<MqXQing> result = new Result<MqXQing>();
		try {
			try{
				XueKe xu = xueKeService.getById(mqXQing.getMqKemu());
				mqXQing.setMqKemu(xu.getXkName());
			}catch (Exception e){
				e.printStackTrace();
			}

			mqXQingService.save(mqXQing);
			result.success("添加成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param mqXQing
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<MqXQing> edit(@RequestBody MqXQing mqXQing) {
		Result<MqXQing> result = new Result<MqXQing>();
		MqXQing mqXQingEntity = mqXQingService.getById(mqXQing.getId());
		if(mqXQingEntity==null) {
			result.error500("未找到对应实体");
		}else {
			try{
				XueKe xu = xueKeService.getById(mqXQing.getMqKemu());
				mqXQing.setMqKemu(xu.getXkName());
			}catch (Exception e){
				e.printStackTrace();
			}
			boolean ok = mqXQingService.updateById(mqXQing);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<MqXQing> delete(@RequestParam(name="id",required=true) String id) {
		Result<MqXQing> result = new Result<MqXQing>();
		MqXQing mqXQing = mqXQingService.getById(id);
		if(mqXQing==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = mqXQingService.removeById(id);
			if(ok) {
				result.success("删除成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<MqXQing> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<MqXQing> result = new Result<MqXQing>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.mqXQingService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<MqXQing> queryById(@RequestParam(name="id",required=true) String id) {
		Result<MqXQing> result = new Result<MqXQing>();
		MqXQing mqXQing = mqXQingService.getById(id);
		if(mqXQing==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(mqXQing);
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
	  QueryWrapper<MqXQing> queryWrapper = null;
	  try {
		  String paramsStr = request.getParameter("paramsStr");
		  if (oConvertUtils.isNotEmpty(paramsStr)) {
			  String deString = URLDecoder.decode(paramsStr, "UTF-8");
			  MqXQing mq = JSON.parseObject(deString, MqXQing.class);
		  }
	  } catch (UnsupportedEncodingException e) {
		  e.printStackTrace();
	  }
      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "模式详情列表");
      mv.addObject(NormalExcelConstants.CLASS, MqXQing.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("模式详情列表数据", "提醒类型:0.静音. 1.响铃 2.震动 3.响铃加震动 导出人:雁飞留影", "导出信息"));
      //mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
  }





}
