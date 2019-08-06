package org.jeecg.modules.hudong.hdExcel.controller;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.FileUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hudong.hdExcel.entity.HdExcel;
import org.jeecg.modules.hudong.hdExcel.service.IHdExcelService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.hudong.kc.entity.Kc;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

 /**
 * @Title: Controller
 * @Description: Excel模版管理
 * @author： jeecg-boot
 * @date：   2019-07-31
 * @version： V1.0
 */
@RestController
@RequestMapping("/hdExcel/hdExcel")
@Slf4j
public class HdExcelController {
	 @Value(value = "${jeecg.path.upload}")
	 private String uploadpath;
	@Autowired
	private IHdExcelService hdExcelService;
	
	/**
	  * 分页列表查询
	 * @param hdExcel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<HdExcel>> queryPageList(HdExcel hdExcel,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<HdExcel>> result = new Result<IPage<HdExcel>>();
		QueryWrapper<HdExcel> queryWrapper = QueryGenerator.initQueryWrapper(hdExcel, req.getParameterMap());
		Page<HdExcel> page = new Page<HdExcel>(pageNo, pageSize);
		IPage<HdExcel> pageList = hdExcelService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param hdExcel
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<HdExcel> add(@RequestBody HdExcel hdExcel) {
		Result<HdExcel> result = new Result<HdExcel>();
		try {
			hdExcelService.save(hdExcel);
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
	 * @param hdExcel
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<HdExcel> edit(@RequestBody HdExcel hdExcel) {
		Result<HdExcel> result = new Result<HdExcel>();
		HdExcel hdExcelEntity = hdExcelService.getById(hdExcel.getId());
		if(hdExcelEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = hdExcelService.updateById(hdExcel);
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
	public Result<HdExcel> delete(@RequestParam(name="id",required=true) String id) {
		Result<HdExcel> result = new Result<HdExcel>();
		HdExcel hdExcel = hdExcelService.getById(id);
		if(hdExcel==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = hdExcelService.removeById(id);
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
	public Result<HdExcel> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<HdExcel> result = new Result<HdExcel>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.hdExcelService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<HdExcel> queryById(@RequestParam(name="id",required=true) String id) {
		Result<HdExcel> result = new Result<HdExcel>();
		HdExcel hdExcel = hdExcelService.getById(id);
		if(hdExcel==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(hdExcel);
			result.setSuccess(true);
		}
		return result;
	}

  /**
   * 导出课程表模版
   *
   * @param request
   * @param response
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 组装查询条件
      QueryWrapper<HdExcel> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              HdExcel hdExcel = JSON.parseObject(deString, HdExcel.class);
              queryWrapper = QueryGenerator.initQueryWrapper(hdExcel, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<HdExcel> pageList = hdExcelService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "Excel模版管理列表");
      mv.addObject(NormalExcelConstants.CLASS, Kc.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("Excel模版管理列表数据", "导出人:雁飞留影", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, new ArrayList<>());
      return mv;
  }

	 /**
	  * 上传模版文件
	  *
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping("/uploadExcel")
	 public Result<JSONObject> uploadExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {

	 	Result<JSONObject> result = new Result<>();
		 String ctxPath = uploadpath;
		 String fileName = null;
		 String bizPath = "user";
		 String nowday = new SimpleDateFormat("yyyyMMdd").format(new Date());
		 File file = new File(ctxPath + File.separator + bizPath + File.separator + nowday);
		 if (!file.exists()) {
			 file.mkdirs();// 创建文件根目录
		 }
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
		 String orgName = mf.getOriginalFilename();// 获取文件名
		 fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
		 String savePath = file.getPath() + File.separator + fileName;
		 File savefile = new File(savePath);
		 FileCopyUtils.copy(mf.getBytes(), savefile);
		 String dbpath = bizPath + File.separator + nowday + File.separator + fileName;
		 if (dbpath.contains("\\")) {
			 dbpath = dbpath.replace("\\", "/");
		 }
		 JSONObject jsonObject=  new JSONObject();
		 jsonObject.put("path",dbpath);
		 result.setResult(jsonObject);
		 result.setSuccess(true);
		 return result;
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
              List<HdExcel> listHdExcels = ExcelImportUtil.importExcel(file.getInputStream(), HdExcel.class, params);
              for (HdExcel hdExcelExcel : listHdExcels) {
                  hdExcelService.save(hdExcelExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listHdExcels.size());
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
