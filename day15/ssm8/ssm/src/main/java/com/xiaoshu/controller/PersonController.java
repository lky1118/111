package com.xiaoshu.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Bank;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Pvo;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.PersonService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("person")
public class PersonController extends LogController{
	static Logger logger = Logger.getLogger(PersonController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	@Autowired
	private PersonService personService;
	
	
	@RequestMapping("personIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		List<Bank> bList = personService.findAlls();
		request.setAttribute("bList", bList);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "person";
	}
	
	
	@RequestMapping(value="personList",method=RequestMethod.POST)
	public void personList(HttpServletRequest request,Pvo pvo,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Pvo> userList= personService.findPersonPage(pvo,pageNum,pageSize);
			
	
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reservePerson")
	public void reservePerson(HttpServletRequest request,Pvo pvo,HttpServletResponse response){
		Integer userId = pvo.getpId();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
					pvo.setpId(userId);
					personService.updateperson(pvo);
					result.put("success", true);
				
				
			}else {   // 添加
				if(personService.existUserWithUserName(pvo.getpName())==null){  // 没有重复可以添加
					personService.addPersom(pvo);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deletePerson")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				personService.deletePerson(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	//Echarts图表
		@RequestMapping("/echarts")
		public void Echarts(HttpServletResponse response){
			Map<String,Integer> map = personService.echarts();
			String jsonString = JSONObject.toJSONString(map);
			WriterUtil.write(response,jsonString );
		}

}
