package org.jeecg.modules.hudong.xthf.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.json.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hudong.msfenlei.entity.MsFenLi;
import org.jeecg.modules.hudong.msfenlei.service.IMsFenLiService;
import org.jeecg.modules.hudong.xk.entity.XueKe;
import org.jeecg.modules.hudong.xk.service.IXueKeService;
import org.jeecg.modules.hudong.xthf.entity.Xthf;
import org.jeecg.modules.hudong.xthf.service.IXthfService;

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
 * @Description: 学生OK回复的系统回复
 * @author： jeecg-boot
 * @date：   2019-08-02
 * @version： V1.0
 */
@RestController
@RequestMapping("/xthf/xthf")
@Slf4j
public class XthfController {
	@Autowired
	private IXthfService xthfService;
	@Autowired
	private IMsFenLiService msFenLiService;
	@Autowired
	private IXueKeService xueKeService;
	
	/**
	  * 分页列表查询
	 * @param xthf
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Xthf>> queryPageList(Xthf xthf,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Xthf>> result = new Result<IPage<Xthf>>();
		QueryWrapper<Xthf> queryWrapper = QueryGenerator.initQueryWrapper(xthf, req.getParameterMap());
		Page<Xthf> page = new Page<Xthf>(pageNo, pageSize);
		queryWrapper.orderByAsc("ps_time");
		IPage<Xthf> pageList = xthfService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param xthf
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Xthf> add(@RequestBody Xthf xthf) {
		Result<Xthf> result = new Result<Xthf>();
		try {
			String flId = xthf.getGrade();	//年级
			MsFenLi fenLi = msFenLiService.getById(flId);
			xthf.setGradeName(fenLi.getFlName());
			String kemu = xthf.getKemu();
			XueKe xueKe = xueKeService.getById(kemu);
			xthf.setKemu(xueKe.getXkName());
			xthfService.save(xthf);
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
	 * @param xthf
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Xthf> edit(@RequestBody Xthf xthf) {
		Result<Xthf> result = new Result<Xthf>();
		Xthf xthfEntity = xthfService.getById(xthf.getId());
		if(xthfEntity==null) {
			result.error500("未找到对应实体");
		}else {
			String flId = xthf.getGrade();	//年级
			if(!flId.equals("-1")){
				MsFenLi fenLi = msFenLiService.getById(flId);
				xthf.setGrade(fenLi.getId());
				xthf.setGradeName(fenLi.getFlName());
			}else {
				xthf.setGrade(xthfEntity.getGrade());
			}
			String kemu = xthf.getKemu();
			if(!kemu.equals("-1")){
				XueKe xueKe = xueKeService.getById(kemu);
				xthf.setKemu(xueKe.getXkName());
			}else {
				xthf.setKemu(xthfEntity.getKemu());
			}

			boolean ok = xthfService.updateById(xthf);
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
	public Result<Xthf> delete(@RequestParam(name="id",required=true) String id) {
		Result<Xthf> result = new Result<Xthf>();
		Xthf xthf = xthfService.getById(id);
		if(xthf==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = xthfService.removeById(id);
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
	public Result<Xthf> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Xthf> result = new Result<Xthf>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.xthfService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Xthf> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Xthf> result = new Result<Xthf>();
		Xthf xthf = xthfService.getById(id);
		if(xthf==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(xthf);
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
      QueryWrapper<Xthf> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Xthf xthf = JSON.parseObject(deString, Xthf.class);
              queryWrapper = QueryGenerator.initQueryWrapper(xthf, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Xthf> pageList = xthfService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "学生OK回复的系统回复列表");
      mv.addObject(NormalExcelConstants.CLASS, Xthf.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("学生OK回复的系统回复列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Xthf> listXthfs = ExcelImportUtil.importExcel(file.getInputStream(), Xthf.class, params);
              for (Xthf xthfExcel : listXthfs) {



                  xthfService.save(xthfExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listXthfs.size());
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
	 @GetMapping(value = "/nflist")
	 public Result<List<Map<String,String>>> valueList(HttpServletRequest req) {
		 Result<List<Map<String,String>>> result = new Result<List<Map<String,String>>>();
		 List<MsFenLi> list = msFenLiService.list();
		 List<Map<String,String>> list1 = new ArrayList<>();
		 if(list.size() > 0){
		 	for(MsFenLi msfenli : list) {
				Map map = new HashMap();
				map.put("value", msfenli.getId());
				map.put("label", msfenli.getFlName());
				List<XueKe> xklist2 = xueKeService.list(new QueryWrapper<XueKe>().eq("fl_id", msfenli.getId()));
				List<Map<String, String>> list2 = new ArrayList<>();
				if (xklist2.size() > 0) {
					xklist2.stream().forEach(xueKe -> {
						Map map1 = new HashMap();
						map1.put("value", xueKe.getId());
						map1.put("label", xueKe.getXkName());
						list2.add(map1);
					});
					map.put("children", list2);

					list1.add(map);
				}

			}
		 }
		 result.setSuccess(true);
		 result.setResult(list1);
		 return result;
	 }

}
