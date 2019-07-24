package org.jeecg.modules.hudong.xtxi.controller;

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
import org.jeecg.modules.hudong.xtxi.entity.Xtxi;
import org.jeecg.modules.hudong.xtxi.service.IXtxiService;

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
 * @Description: 系统消息管理
 * @author： jeecg-boot
 * @date：   2019-06-29
 * @version： V1.0
 */
@RestController
@RequestMapping("/xtxi/xtxi")
@Slf4j
public class XtxiController {
	@Autowired
	private IXtxiService xtxiService;
	
	/**
	  * 分页列表查询
	 * @param xtxi
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Xtxi>> queryPageList(Xtxi xtxi,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Xtxi>> result = new Result<IPage<Xtxi>>();
		QueryWrapper<Xtxi> queryWrapper = QueryGenerator.initQueryWrapper(xtxi, req.getParameterMap());
		Page<Xtxi> page = new Page<Xtxi>(pageNo, pageSize);
		IPage<Xtxi> pageList = xtxiService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param xtxi
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Xtxi> add(@RequestBody Xtxi xtxi) {
		Result<Xtxi> result = new Result<Xtxi>();
		try {
			xtxiService.save(xtxi);
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
	 * @param xtxi
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Xtxi> edit(@RequestBody Xtxi xtxi) {
		Result<Xtxi> result = new Result<Xtxi>();
		Xtxi xtxiEntity = xtxiService.getById(xtxi.getId());
		if(xtxiEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xtxiService.updateById(xtxi);
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
	public Result<Xtxi> delete(@RequestParam(name="id",required=true) String id) {
		Result<Xtxi> result = new Result<Xtxi>();
		Xtxi xtxi = xtxiService.getById(id);
		if(xtxi==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xtxiService.removeById(id);
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
	public Result<Xtxi> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Xtxi> result = new Result<Xtxi>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.xtxiService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Xtxi> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Xtxi> result = new Result<Xtxi>();
		Xtxi xtxi = xtxiService.getById(id);
		if(xtxi==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(xtxi);
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
      QueryWrapper<Xtxi> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Xtxi xtxi = JSON.parseObject(deString, Xtxi.class);
              queryWrapper = QueryGenerator.initQueryWrapper(xtxi, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Xtxi> pageList = xtxiService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "系统消息管理列表");
      mv.addObject(NormalExcelConstants.CLASS, Xtxi.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("系统消息管理列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Xtxi> listXtxis = ExcelImportUtil.importExcel(file.getInputStream(), Xtxi.class, params);
              for (Xtxi xtxiExcel : listXtxis) {
                  xtxiService.save(xtxiExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listXtxis.size());
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
