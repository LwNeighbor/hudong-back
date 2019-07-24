package org.jeecg.modules.hudong.xuexi.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.front.lFaserYyTransText;
import org.jeecg.modules.hudong.parent.service.IParentService;
import org.jeecg.modules.hudong.xuexi.entity.XueXi;
import org.jeecg.modules.hudong.xuexi.service.IXueXiService;

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
 * @Description: 学习
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@RestController
@RequestMapping("/xuexi/xueXi")
@Slf4j
public class XueXiController {
	@Autowired
	private IXueXiService xueXiService;
	
	/**
	  * 分页列表查询
	 * @param xueXi
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<XueXi>> queryPageList(XueXi xueXi,
									  @RequestParam(value = "beginTime",required = false,defaultValue = "-1") String beginTime,
									  @RequestParam(value = "endTime",required = false,defaultValue = "-1") String endTime,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {




		Result<IPage<XueXi>> result = new Result<IPage<XueXi>>();
		QueryWrapper<XueXi> queryWrapper = QueryGenerator.initQueryWrapper(xueXi, req.getParameterMap());
		if(!beginTime.equalsIgnoreCase("-1")){
			String begin = beginTime.substring(1,11);
			String start = DateUtils.formatDate(begin);
			queryWrapper.ge("create_time", start);
		}
		if(!endTime.equalsIgnoreCase("-1")){
			String end = endTime.substring(1,11);
			String endtime = DateUtils.formatDate(end);
			queryWrapper.le("create_time",endtime);
		}
		Page<XueXi> page = new Page<XueXi>(pageNo, pageSize);
		IPage<XueXi> pageList = xueXiService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}


	/**
	  *   添加
	 * @param xueXi
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<XueXi> add(@RequestBody XueXi xueXi) {
		Result<XueXi> result = new Result<XueXi>();
		try {
			xueXiService.save(xueXi);
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
	 * @param xueXi
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<XueXi> edit(@RequestBody XueXi xueXi) {
		Result<XueXi> result = new Result<XueXi>();
		XueXi xueXiEntity = xueXiService.getById(xueXi.getId());
		if(xueXiEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xueXiService.updateById(xueXi);
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
	public Result<XueXi> delete(@RequestParam(name="id",required=true) String id) {
		Result<XueXi> result = new Result<XueXi>();
		XueXi xueXi = xueXiService.getById(id);
		if(xueXi==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xueXiService.removeById(id);
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
	public Result<XueXi> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<XueXi> result = new Result<XueXi>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.xueXiService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<XueXi> queryById(@RequestParam(name="id",required=true) String id) {
		Result<XueXi> result = new Result<XueXi>();
		XueXi xueXi = xueXiService.getById(id);
		if(xueXi==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(xueXi);
			result.setSuccess(true);
		}
		return result;
	}


	 /**
	  *   语音转文字
	  * @return
	  */
	 @PostMapping(value = "/transacText")
	 public Result<XueXi> transacText(@RequestBody XueXi olxueXi) {
		 Result<XueXi> result = new Result<XueXi>();
		 String id = olxueXi.getId();
		 String text = "";
		 try {
			 XueXi xueXi = xueXiService.getById(id);
			 Result<JSONObject> lfasrClientImp = lFaserYyTransText.getLfasrClientImp(xueXi.getFilepath());
			 JSONObject json = lfasrClientImp.getResult();

			 if(json.get("ecode").equals("1")){
				 //说明返回是成功过得
				 text = (String)json.get("text");
			 }else {
				 result.error500("操作失败");
				 return result;
			 }

			 xueXi.setYytext(text);
			 xueXiService.updateById(xueXi);
			 result.success("添加成功！");
		 } catch (Exception e) {
			 e.printStackTrace();
			 log.info(e.getMessage());
			 result.error500("操作失败");
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
      QueryWrapper<XueXi> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              XueXi xueXi = JSON.parseObject(deString, XueXi.class);
              queryWrapper = QueryGenerator.initQueryWrapper(xueXi, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<XueXi> pageList = xueXiService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "学习列表");
      mv.addObject(NormalExcelConstants.CLASS, XueXi.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("学习列表数据", "导出人:Jeecg", "导出信息"));
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
              List<XueXi> listXueXis = ExcelImportUtil.importExcel(file.getInputStream(), XueXi.class, params);
              for (XueXi xueXiExcel : listXueXis) {
                  xueXiService.save(xueXiExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listXueXis.size());
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
