package org.jeecg.modules.hudong.parent.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hudong.parent.entity.Parent;
import org.jeecg.modules.hudong.parent.entity.ParentModal;
import org.jeecg.modules.hudong.parent.mapper.ParentMapper;
import org.jeecg.modules.hudong.parent.service.IParentService;

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
 * @Description: 父母管理
 * @author： jeecg-boot
 * @date：   2019-06-20
 * @version： V1.0
 */
@RestController
@RequestMapping("/parent/parent")
@Slf4j
public class ParentController {
	@Autowired
	private IParentService parentService;
	
	/**
	  * 分页列表查询
	 * @param parent
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Parent>> queryPageList(Parent parent,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Parent>> result = new Result<IPage<Parent>>();
		QueryWrapper<Parent> queryWrapper = QueryGenerator.initQueryWrapper(parent, req.getParameterMap());
		Page<Parent> page = new Page<Parent>(pageNo, pageSize);
		IPage<Parent> pageList = parentService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}


	 /**
	  * 查询数据 查出所有部门,并以树结构数据格式响应给前端
	  *
	  * @return
	  */
	 @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	 public Result<List<ParentModal>> queryTreeList() {
		 Result<List<ParentModal>> result = new Result<>();
		 try {
		 	List<ParentModal> listm = new ArrayList<>();
			 List<Parent> list = parentService.list();


			 list.stream().forEach(parent -> {
				 ParentModal parentModal = new ParentModal(parent);
				 listm.add(parentModal);
			 });

			 result.setResult(listm);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return result;
	 }


	 /**
	  * 查询数据 查出所有部门,并以树结构数据格式响应给前端
	  *
	  * @return
	  */
	 @RequestMapping(value = "/queryParent", method = RequestMethod.GET)
	 public Result<List<ParentModal>> queryParent(@RequestParam("ptName")String name,
												  @RequestParam("ptPhone") String phone) {
		 Result<List<ParentModal>> result = new Result<>();
		 try {
			 List<ParentModal> listm = new ArrayList<>();
			 List<Parent> list = parentService.list(new QueryWrapper<Parent>().
					 eq("pt_name", name).or().
					 eq("pt_phone", phone));


			 list.stream().forEach(parent1 -> {
			 	ParentModal parentModal = new ParentModal(parent1);
			 	listm.add(parentModal);
			 });

			 result.setResult(listm);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return result;
	 }

	 /**
	  * 模式描述中的下拉框需要显示的
	  * @param
	  * @param
	  * @return
	  */
	 @GetMapping(value = "/valueList")
	 public Result<List<Map<String,String>>> valueList(HttpServletRequest req) {
		 Result<List<Map<String,String>>> result = new Result<List<Map<String,String>>>();
		 List<Parent> list = parentService.list();
		 List<Map<String,String>> list1 = new ArrayList<>();
		 if(list.size() > 0){
			 list.stream().forEach(parent->{
				 Map map = new HashMap();
				 map.put("value",parent.getId());
				 map.put("text",parent.getPtName());
				 list1.add(map);
			 });
		 }
		 result.setSuccess(true);
		 result.setResult(list1);
		 return result;
	 }

	/**
	  *   添加
	 * @param parent
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Parent> add(@RequestBody Parent parent) {
		Result<Parent> result = new Result<Parent>();
		try {
			parentService.save(parent);
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
	 * @param parent
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Parent> edit(@RequestBody Parent parent) {
		Result<Parent> result = new Result<Parent>();
		Parent parentEntity = parentService.getById(parent.getId());
		if(parentEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = parentService.updateById(parent);
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
	public Result<Parent> delete(@RequestParam(name="id",required=true) String id) {
		Result<Parent> result = new Result<Parent>();
		Parent parent = parentService.getById(id);
		if(parent==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = parentService.removeById(id);
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
	public Result<Parent> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Parent> result = new Result<Parent>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.parentService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Parent> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Parent> result = new Result<Parent>();
		Parent parent = parentService.getById(id);
		if(parent==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(parent);
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
      QueryWrapper<Parent> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Parent parent = JSON.parseObject(deString, Parent.class);
              queryWrapper = QueryGenerator.initQueryWrapper(parent, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Parent> pageList = parentService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "父母管理列表");
      mv.addObject(NormalExcelConstants.CLASS, Parent.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("父母管理列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Parent> listParents = ExcelImportUtil.importExcel(file.getInputStream(), Parent.class, params);
              for (Parent parentExcel : listParents) {
                  parentService.save(parentExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listParents.size());
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
	  *   冻结/解冻某用户
	  * @param id
	  * @return
	  */
	 @PostMapping(value = "/frozen")
	 public Result<Parent> frozen(@RequestBody Parent parent) {
		 Result<Parent> result = new Result<Parent>();
		 if(parent==null) {
			 result.error500("未找到对应实体");
		 }else {
			 boolean ok = parentService.updateById(parent);
			 if(ok) {
				 result.success("操作成功!");
			 }
		 }
		 return result;
	 }

}
