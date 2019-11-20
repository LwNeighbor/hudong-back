package org.jeecg.modules.hudong.msfenlei.controller;

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
 * @Description: 模式分类
 * @author： jeecg-boot
 * @date：   2019-06-21
 * @version： V1.0
 */
@RestController
@RequestMapping("/msfenlei/msFenLi")
@Slf4j
public class MsFenLiController {
	@Autowired
	private IMsFenLiService msFenLiService;
	@Autowired
	private IMqXQingService mqXQingService;
	
	/**
	  * 分页列表查询
	 * @param msFenLi
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<MsFenLi>> queryPageList(MsFenLi msFenLi,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<MsFenLi>> result = new Result<IPage<MsFenLi>>();
		QueryWrapper<MsFenLi> queryWrapper = QueryGenerator.initQueryWrapper(msFenLi, req.getParameterMap());
		Page<MsFenLi> page = new Page<MsFenLi>(pageNo, pageSize);
		IPage<MsFenLi> pageList = msFenLiService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param msFenLi
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<MsFenLi> add(@RequestBody MsFenLi msFenLi) {
		Result<MsFenLi> result = new Result<MsFenLi>();
		try {
			msFenLiService.save(msFenLi);
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
	 * @param msFenLi
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<MsFenLi> edit(@RequestBody MsFenLi msFenLi) {

		//需要修改孩子
		//修改学科
		//课程
		//系统回复OK


		Result<MsFenLi> result = new Result<MsFenLi>();
		MsFenLi msFenLiEntity = msFenLiService.getById(msFenLi.getId());
		if(msFenLiEntity==null) {
			result.error500("未找到对应实体");
		}else {

			int i = msFenLiService.updateFenLiById(msFenLi);

			//boolean ok = msFenLiService.updateById(msFenLi);
			//TODO 返回false说明什么？
			if(i > 0) {
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
	public Result<MsFenLi> delete(@RequestParam(name="id",required=true) String id) {
		Result<MsFenLi> result = new Result<MsFenLi>();

		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("fl_id",id);
		int count = mqXQingService.count(queryWrapper);
		if(count > 0){
			result.error500("该分类下还有模式描述，无法删除");
		}
		MsFenLi msFenLi = msFenLiService.getById(id);
		if(msFenLi==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = msFenLiService.removeById(id);
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
	public Result<MsFenLi> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<MsFenLi> result = new Result<MsFenLi>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.msFenLiService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<MsFenLi> queryById(@RequestParam(name="id",required=true) String id) {
		Result<MsFenLi> result = new Result<MsFenLi>();
		MsFenLi msFenLi = msFenLiService.getById(id);
		if(msFenLi==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(msFenLi);
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
      QueryWrapper<MsFenLi> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              MsFenLi msFenLi = JSON.parseObject(deString, MsFenLi.class);
              queryWrapper = QueryGenerator.initQueryWrapper(msFenLi, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<MsFenLi> pageList = msFenLiService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "模式分类列表");
      mv.addObject(NormalExcelConstants.CLASS, MsFenLi.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("模式分类列表数据", "导出人:Jeecg", "导出信息"));
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
              List<MsFenLi> listMsFenLis = ExcelImportUtil.importExcel(file.getInputStream(), MsFenLi.class, params);
              for (MsFenLi msFenLiExcel : listMsFenLis) {
                  msFenLiService.save(msFenLiExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listMsFenLis.size());
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
	  * 模式描述中的下拉框需要显示的
	  * @param
	  * @param req
	  * @return
	  */
	 @GetMapping(value = "/valueList")
	 public Result<List<Map<String,String>>> valueList(HttpServletRequest req) {
		 Result<List<Map<String,String>>> result = new Result<List<Map<String,String>>>();
		 List<MsFenLi> list = msFenLiService.list();
		 List<Map<String,String>> list1 = new ArrayList<>();
		 if(list.size() > 0){
		 	list.stream().forEach(msfenli->{
		 		Map map = new HashMap();
		 		map.put("value",msfenli.getId());
		 		map.put("text",msfenli.getFlName());
		 		list1.add(map);
			});
		 }
		 result.setSuccess(true);
		 result.setResult(list1);
		 return result;
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
			 zlist.add(map);
		 }
		 jsonObject.put("options",zlist);
		 result.setSuccess(true);
		 result.setResult(jsonObject);
		 return result;
	 }

}
