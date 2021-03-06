package org.jeecg.modules.hudong.kc.controller;

import java.util.ArrayList;
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
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.kc.entity.Kc;
import org.jeecg.modules.hudong.kc.service.IKcService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.hudong.parent.entity.Parent;
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
 * @Description: 课程表管理
 * @author： jeecg-boot
 * @date：   2019-07-31
 * @version： V1.0
 */
@RestController
@RequestMapping("/kc/kc")
@Slf4j
public class KcController {
	@Autowired
	private IKcService kcService;
	
	/**
	  * 分页列表查询
	 * @param kc
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Kc>> queryPageList(Kc kc,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Kc>> result = new Result<IPage<Kc>>();
		QueryWrapper<Kc> queryWrapper = QueryGenerator.initQueryWrapper(kc, req.getParameterMap());
		queryWrapper.orderByAsc("weekday");
		Page<Kc> page = new Page<Kc>(pageNo, pageSize);
		IPage<Kc> pageList = kcService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param kc
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Kc> add(@RequestBody Kc kc) {
		Result<Kc> result = new Result<Kc>();
		try {
			kcService.save(kc);
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
	 * @param kc
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Kc> edit(@RequestBody Kc kc) {
		Result<Kc> result = new Result<Kc>();
		Kc kcEntity = kcService.getById(kc.getId());
		if(kcEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = kcService.updateById(kc);
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
	public Result<Kc> delete(@RequestParam(name="id",required=true) String id) {
		Result<Kc> result = new Result<Kc>();
		Kc kc = kcService.getById(id);
		if(kc==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = kcService.removeById(id);
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
	public Result<Kc> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Kc> result = new Result<Kc>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.kcService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Kc> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Kc> result = new Result<Kc>();
		Kc kc = kcService.getById(id);
		if(kc==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(kc);
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
      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "课程表管理列表");
      mv.addObject(NormalExcelConstants.CLASS, Kc.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("课程表管理列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, new ArrayList<>());
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
              List<Kc> listKcs = ExcelImportUtil.importExcel(file.getInputStream(), Kc.class, params);
              for (Kc kcExcel : listKcs) {
                  kcService.save(kcExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listKcs.size());
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
