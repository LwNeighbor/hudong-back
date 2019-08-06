package org.jeecg.modules.hudong.child.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.crypto.SecureUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hudong.child.entity.Child;
import org.jeecg.modules.hudong.child.service.IChildService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.hudong.msfenlei.entity.MsFenLi;
import org.jeecg.modules.hudong.msfenlei.service.IMsFenLiService;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.service.IParentService;
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
 * @Description: 儿童管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@RestController
@RequestMapping("/child/child")
@Slf4j
public class ChildController {
	@Autowired
	private IChildService childService;
	@Autowired
	private IParentService parentService;
	@Autowired
	private IMsFenLiService fenLiService;
	
	/**
	  * 分页列表查询
	 * @param child
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Child>> queryPageList(Child child,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Child>> result = new Result<IPage<Child>>();
		QueryWrapper<Child> queryWrapper = QueryGenerator.initQueryWrapper(child, req.getParameterMap());
		Page<Child> page = new Page<Child>(pageNo, pageSize);
		IPage<Child> pageList = childService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param child
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Child> add(@RequestBody Child child) {
		Result<Child> result = new Result<Child>();
		try {
			childService.save(child);
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
	 * @param child
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Child> edit(@RequestBody Child child) {
		Result<Child> result = new Result<Child>();
		String cdPassword = child.getCdPassword();
		String nowPassword = SecureUtil.md5(cdPassword);
		child.setCdPassword(nowPassword);
		Child childEntity = childService.getById(child.getId());
		if(childEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = childService.updateById(child);
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
	public Result<Child> delete(@RequestParam(name="id",required=true) String id) {
		Result<Child> result = new Result<Child>();
		Child child = childService.getById(id);
		if(child==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = childService.removeById(id);
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
	public Result<Child> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Child> result = new Result<Child>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.childService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Child> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Child> result = new Result<Child>();
		Child child = childService.getById(id);
		if(child==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(child);
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
      QueryWrapper<Child> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Child child = JSON.parseObject(deString, Child.class);
              queryWrapper = QueryGenerator.initQueryWrapper(child, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Child> pageList = childService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "儿童管理列表");
      mv.addObject(NormalExcelConstants.CLASS, Child.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("儿童管理列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Child> listChilds = ExcelImportUtil.importExcel(file.getInputStream(), Child.class, params);
              for (Child childExcel : listChilds) {
                  childService.save(childExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listChilds.size());
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
	  * 父母下面对应的孩子
	  * @param
	  * @param req
	  * @return
	  */
	 @GetMapping(value = "/inlineList")
	 public Result<List<Child>> inlineList(Child child, HttpServletRequest req) {
		 Result<List<Child>> result = new Result<List<Child>>();
		 QueryWrapper<Child> queryWrapper = QueryGenerator.initQueryWrapper(child, req.getParameterMap());
		 List<Child> pageList = childService.list(queryWrapper);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }

	 /**
	  * 关联父母
	  * @param
	  * @param req
	  * @return
	  */
	 @PostMapping(value = "/relateParent")
	 public Result<Child> relateParent(@RequestBody Child child, HttpServletRequest req) {
		 Result<Child> result = new Result<Child>();
	 	//child对象中的name与phone应该是父母的信息
		 String cdName = child.getCdName();	//父母的姓名
		 String cdPhone = child.getCdPhone(); //父母的手机号
		 QueryWrapper<Parent> queryWrapper = new QueryWrapper<Parent>().eq("pt_name", cdName.trim()).or().
				 eq("pt_phone", cdPhone.trim());
		 List<Parent> list = parentService.list(queryWrapper);
		 if(list.size() > 0){
		 	//说明找到了父母
			 String id = list.get(0).getId();
			 Child child1 = new Child();
			 child1.setId(child.getId());
			 child1.setPtId(id);
			 boolean ok = childService.updateById(child1);
			 if(ok) {
				 result.success("修改成功!");
			 }
		 }else {
		 	result.error500("未找到该家长信息");
		 }
		 return result;
	 }


	 /**
	  * 关联模版
	  * @param
	  * @param req
	  * @return
	  */
	 @PostMapping(value = "/saveMsChild")
	 public Result<Child> saveMsChild(@RequestBody Child child, HttpServletRequest req) {
		 Result<Child> result = new Result<Child>();

		 String flId = child.getFlId();
		 MsFenLi fenLi = fenLiService.getById(flId);
		 String flName = fenLi.getFlName();
		 child.setFlName(flName);
		 Child childEntity = childService.getById(child.getId());
		 if(childEntity==null) {
			 result.error500("未找到对应实体");
		 }else {
			 boolean ok = childService.updateById(child);
			 //TODO 返回false说明什么？
			 if(ok) {
				 result.success("修改成功!");
			 }
		 }
		 return result;
	 }
}
