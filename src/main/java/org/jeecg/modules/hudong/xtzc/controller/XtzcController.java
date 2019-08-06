package org.jeecg.modules.hudong.xtzc.controller;

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
import org.jeecg.modules.hudong.xtzc.entity.Xtzc;
import org.jeecg.modules.hudong.xtzc.service.IXtzcService;

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
 * @Description: 注册后第几天的固定内容发送
 * @author： jeecg-boot
 * @date：   2019-08-02
 * @version： V1.0
 */
@RestController
@RequestMapping("/xtzc/xtzc")
@Slf4j
public class XtzcController {
	@Autowired
	private IXtzcService xtzcService;
	
	/**
	  * 分页列表查询
	 * @param xtzc
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Xtzc>> queryPageList(Xtzc xtzc,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Xtzc>> result = new Result<IPage<Xtzc>>();
		QueryWrapper<Xtzc> queryWrapper = QueryGenerator.initQueryWrapper(xtzc, req.getParameterMap());
		Page<Xtzc> page = new Page<Xtzc>(pageNo, pageSize);
		IPage<Xtzc> pageList = xtzcService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param xtzc
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Xtzc> add(@RequestBody Xtzc xtzc) {
		Result<Xtzc> result = new Result<Xtzc>();
		try {
			xtzcService.save(xtzc);
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
	 * @param xtzc
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Xtzc> edit(@RequestBody Xtzc xtzc) {
		Result<Xtzc> result = new Result<Xtzc>();
		Xtzc xtzcEntity = xtzcService.getById(xtzc.getId());
		if(xtzcEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xtzcService.updateById(xtzc);
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
	public Result<Xtzc> delete(@RequestParam(name="id",required=true) String id) {
		Result<Xtzc> result = new Result<Xtzc>();
		Xtzc xtzc = xtzcService.getById(id);
		if(xtzc==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xtzcService.removeById(id);
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
	public Result<Xtzc> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Xtzc> result = new Result<Xtzc>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.xtzcService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Xtzc> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Xtzc> result = new Result<Xtzc>();
		Xtzc xtzc = xtzcService.getById(id);
		if(xtzc==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(xtzc);
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
      QueryWrapper<Xtzc> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Xtzc xtzc = JSON.parseObject(deString, Xtzc.class);
              queryWrapper = QueryGenerator.initQueryWrapper(xtzc, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Xtzc> pageList = xtzcService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "注册后第几天的固定内容发送列表");
      mv.addObject(NormalExcelConstants.CLASS, Xtzc.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("注册后第几天的固定内容发送列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Xtzc> listXtzcs = ExcelImportUtil.importExcel(file.getInputStream(), Xtzc.class, params);
              for (Xtzc xtzcExcel : listXtzcs) {
                  xtzcService.save(xtzcExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listXtzcs.size());
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
