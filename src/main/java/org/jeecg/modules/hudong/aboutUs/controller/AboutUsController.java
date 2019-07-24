package org.jeecg.modules.hudong.aboutUs.controller;

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
import org.jeecg.modules.hudong.aboutUs.entity.AboutUs;
import org.jeecg.modules.hudong.aboutUs.service.IAboutUsService;

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
 * @Description: 关于我们
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@RestController
@RequestMapping("/aboutUs/aboutUs")
@Slf4j
public class AboutUsController {
	@Autowired
	private IAboutUsService aboutUsService;
	
	/**
	  * 分页列表查询
	 * @param aboutUs
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<AboutUs>> queryPageList(AboutUs aboutUs,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<AboutUs>> result = new Result<IPage<AboutUs>>();
		QueryWrapper<AboutUs> queryWrapper = QueryGenerator.initQueryWrapper(aboutUs, req.getParameterMap());
		Page<AboutUs> page = new Page<AboutUs>(pageNo, pageSize);
		IPage<AboutUs> pageList = aboutUsService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param aboutUs
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<AboutUs> add(@RequestBody AboutUs aboutUs) {
		Result<AboutUs> result = new Result<AboutUs>();
		try {
			aboutUsService.save(aboutUs);
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
	 * @param aboutUs
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<AboutUs> edit(@RequestBody AboutUs aboutUs) {
		Result<AboutUs> result = new Result<AboutUs>();
		AboutUs aboutUsEntity = aboutUsService.getById(aboutUs.getId());
		if(aboutUsEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = aboutUsService.updateById(aboutUs);
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
	public Result<AboutUs> delete(@RequestParam(name="id",required=true) String id) {
		Result<AboutUs> result = new Result<AboutUs>();
		AboutUs aboutUs = aboutUsService.getById(id);
		if(aboutUs==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = aboutUsService.removeById(id);
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
	public Result<AboutUs> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<AboutUs> result = new Result<AboutUs>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.aboutUsService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<AboutUs> queryById(@RequestParam(name="id",required=true) String id) {
		Result<AboutUs> result = new Result<AboutUs>();
		AboutUs aboutUs = aboutUsService.getById(id);
		if(aboutUs==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(aboutUs);
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
      QueryWrapper<AboutUs> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              AboutUs aboutUs = JSON.parseObject(deString, AboutUs.class);
              queryWrapper = QueryGenerator.initQueryWrapper(aboutUs, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<AboutUs> pageList = aboutUsService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "关于我们列表");
      mv.addObject(NormalExcelConstants.CLASS, AboutUs.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("关于我们列表数据", "导出人:Jeecg", "导出信息"));
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
              List<AboutUs> listAboutUss = ExcelImportUtil.importExcel(file.getInputStream(), AboutUs.class, params);
              for (AboutUs aboutUsExcel : listAboutUss) {
                  aboutUsService.save(aboutUsExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listAboutUss.size());
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
