package org.jeecg.modules.hudong.xk.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hudong.msfenlei.entity.MsFenLi;
import org.jeecg.modules.hudong.xk.entity.XueKe;
import org.jeecg.modules.hudong.xk.service.IXueKeService;

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
 * @Description: 学科设置
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@RestController
@RequestMapping("/xk/xueKe")
@Slf4j
public class XueKeController {
	@Autowired
	private IXueKeService xueKeService;
	
	/**
	  * 分页列表查询
	 * @param xueKe
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<XueKe>> queryPageList(XueKe xueKe,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<XueKe>> result = new Result<IPage<XueKe>>();
		QueryWrapper<XueKe> queryWrapper = QueryGenerator.initQueryWrapper(xueKe, req.getParameterMap());
		Page<XueKe> page = new Page<XueKe>(pageNo, pageSize);
		IPage<XueKe> pageList = xueKeService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	 /**
	  * 模式描述中的下拉框需要显示的
	  * @param
	  * @param req
	  * @return
	  */
	 @PostMapping(value = "/valueList")
	 public Result<List<Map<String,String>>> valueList(HttpServletRequest req,@RequestBody XueKe xueKe) {
		 Result<List<Map<String,String>>> result = new Result<List<Map<String,String>>>();
		 List<XueKe> xueKeList = xueKeService.list(new QueryWrapper<XueKe>().eq("fl_id", xueKe.getFlId()));
		 List<Map<String,String>> list1 = new ArrayList<>();
		 if(xueKeList.size() > 0){
			 xueKeList.stream().forEach(xueKe1->{
				 Map map = new HashMap();
				 map.put("value",xueKe1.getId());
				 map.put("text",xueKe1.getXkName());
				 list1.add(map);
			 });
		 }
		 result.setSuccess(true);
		 result.setResult(list1);
		 return result;
	 }
	
	/**
	  *   添加
	 * @param xueKe
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<XueKe> add(@RequestBody XueKe xueKe) {
		Result<XueKe> result = new Result<XueKe>();
		try {




			xueKeService.save(xueKe);
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
	 * @param xueKe
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<XueKe> edit(@RequestBody XueKe xueKe) {
		Result<XueKe> result = new Result<XueKe>();
		XueKe xueKeEntity = xueKeService.getById(xueKe.getId());
		if(xueKeEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xueKeService.updateById(xueKe);
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
	public Result<XueKe> delete(@RequestParam(name="id",required=true) String id) {
		Result<XueKe> result = new Result<XueKe>();
		XueKe xueKe = xueKeService.getById(id);
		if(xueKe==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xueKeService.removeById(id);
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
	public Result<XueKe> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<XueKe> result = new Result<XueKe>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.xueKeService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<XueKe> queryById(@RequestParam(name="id",required=true) String id) {
		Result<XueKe> result = new Result<XueKe>();
		XueKe xueKe = xueKeService.getById(id);
		if(xueKe==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(xueKe);
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
      QueryWrapper<XueKe> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              XueKe xueKe = JSON.parseObject(deString, XueKe.class);
              queryWrapper = QueryGenerator.initQueryWrapper(xueKe, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<XueKe> pageList = xueKeService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "学科设置列表");
      mv.addObject(NormalExcelConstants.CLASS, XueKe.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("学科设置列表数据", "导出人:Jeecg", "导出信息"));
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
              List<XueKe> listXueKes = ExcelImportUtil.importExcel(file.getInputStream(), XueKe.class, params);
              for (XueKe xueKeExcel : listXueKes) {
                  xueKeService.save(xueKeExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listXueKes.size());
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
