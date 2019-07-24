package org.jeecg.modules.hudong.customer.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hudong.customer.entity.Cusomter;
import org.jeecg.modules.hudong.customer.service.ICusomterService;

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
 * @Description: 客服管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@RestController
@RequestMapping("/customer/cusomter")
@Slf4j
public class CusomterController {
	@Autowired
	private ICusomterService cusomterService;
	
	/**
	  * 分页列表查询
	 * @param cusomter
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Cusomter>> queryPageList(Cusomter cusomter,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Cusomter>> result = new Result<IPage<Cusomter>>();
		QueryWrapper<Cusomter> queryWrapper = QueryGenerator.initQueryWrapper(cusomter, req.getParameterMap());
		Page<Cusomter> page = new Page<Cusomter>(pageNo, pageSize);
		IPage<Cusomter> pageList = cusomterService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param cusomter
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Cusomter> add(@RequestBody Cusomter cusomter) {
		Result<Cusomter> result = new Result<Cusomter>();
		try {
			cusomterService.save(cusomter);
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
	 * @param cusomter
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Cusomter> edit(@RequestBody Cusomter cusomter) {
		Result<Cusomter> result = new Result<Cusomter>();
		Cusomter cusomterEntity = cusomterService.getById(cusomter.getId());
		if(cusomterEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = cusomterService.updateById(cusomter);
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
	public Result<Cusomter> delete(@RequestParam(name="id",required=true) String id) {
		Result<Cusomter> result = new Result<Cusomter>();
		Cusomter cusomter = cusomterService.getById(id);
		if(cusomter==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = cusomterService.removeById(id);
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
	public Result<Cusomter> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Cusomter> result = new Result<Cusomter>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.cusomterService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Cusomter> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Cusomter> result = new Result<Cusomter>();
		Cusomter cusomter = cusomterService.getById(id);
		if(cusomter==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(cusomter);
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
      QueryWrapper<Cusomter> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Cusomter cusomter = JSON.parseObject(deString, Cusomter.class);
              queryWrapper = QueryGenerator.initQueryWrapper(cusomter, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Cusomter> pageList = cusomterService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "客服管理列表");
      mv.addObject(NormalExcelConstants.CLASS, Cusomter.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("客服管理列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Cusomter> listCusomters = ExcelImportUtil.importExcel(file.getInputStream(), Cusomter.class, params);
              for (Cusomter cusomterExcel : listCusomters) {
                  cusomterService.save(cusomterExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listCusomters.size());
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

}
