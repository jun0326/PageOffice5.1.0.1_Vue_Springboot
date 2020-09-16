package com.test.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zhuozhengsoft.pageoffice.wordwriter.WordDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.zhuozhengsoft.pageoffice.*;

/**
 * @author Administrator
 *
 */
@RestController
public class DemoController {
	
	@Value("${posyspath}") 
	private String poSysPath;
	
//	@Value("${popassword}")
//	private String poPassWord;
	
	@Value("${docpath}") 
	private String docPath;

	@RequestMapping("/hello")
	public String test() {
		System.out.println("hello run");
		return "Hello";
	}
	
	@RequestMapping(value="/word", method=RequestMethod.GET)
	public ModelAndView showWord(HttpServletRequest request, Map<String,Object> map){

		String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		PageOfficeCtrl poCtrl=new PageOfficeCtrl(request);
		poCtrl.setServerPage("/poserver.zz");//设置服务页面
		WordDocument doc = new WordDocument();
		doc.openDataRegion("PO_name").setValue(dateStr + "李四，文档版权归属招商证券所有，不得转载，谢谢！");
		poCtrl.setWriter(doc);
//		//隐藏菜单栏
//		poCtrl.setMenubar(false);
//		//隐藏工具栏
//		poCtrl.setCustomToolbar(false);
		poCtrl.addCustomToolButton("保存","Save",1);//添加自定义保存按钮
		poCtrl.addCustomToolButton("插入批注", "newComment", 2);
//		poCtrl.addCustomToolButton("盖章","AddSeal",2);//添加自定义盖章按钮
		poCtrl.setSaveFilePage("/save");//设置处理文件保存的请求方法

		//打开word
		poCtrl.webOpen("file://" + docPath +"test.docx",OpenModeType.docCommentOnly,"李四");

//		poCtrl.webOpen("file://" + docPath +"test.docx",OpenModeType.docNormalEdit,"李四");


		map.put("pageoffice",poCtrl.getHtmlCode("PageOfficeCtrl1"));
		
		ModelAndView mv = new ModelAndView("Word");
		return mv;
	}
	@RequestMapping(value="/excel", method=RequestMethod.GET)
	public ModelAndView showExcel(HttpServletRequest request, Map<String,Object> map){

		PageOfficeCtrl poCtrl=new PageOfficeCtrl(request);
		poCtrl.setServerPage("/poserver.zz");//设置服务页面
		poCtrl.addCustomToolButton("保存","Save",1);//添加自定义保存按钮
		poCtrl.addCustomToolButton("盖章","AddSeal",2);//添加自定义盖章按钮
		poCtrl.setSaveFilePage("/save");//设置处理文件保存的请求方法
		//打开word
		poCtrl.webOpen("file://"+docPath+"test.xlsx",OpenModeType.xlsNormalEdit,"张三");
		map.put("pageoffice",poCtrl.getHtmlCode("PageOfficeCtrl1"));
		ModelAndView mv = new ModelAndView("Excel");
		return mv;
	}
	
	@RequestMapping("/save")
	public void saveFile(HttpServletRequest request, HttpServletResponse response){
		FileSaver fs = new FileSaver(request, response);
		fs.saveToFile(docPath + fs.getFileName());
		fs.close();
	}
	
	
	/**
	 * 添加PageOffice的服务器端授权程序Servlet（必须）
	 * @return
	 */
	@Bean
    public ServletRegistrationBean servletRegistrationBean() {
		com.zhuozhengsoft.pageoffice.poserver.Server poserver = new com.zhuozhengsoft.pageoffice.poserver.Server();
		poserver.setSysPath(poSysPath);//设置PageOffice注册成功后,license.lic文件存放的目录
		ServletRegistrationBean srb = new ServletRegistrationBean(poserver);
		srb.addUrlMappings("/poserver.zz");
		srb.addUrlMappings("/posetup.exe");
		srb.addUrlMappings("/pageoffice.js");
		srb.addUrlMappings("/jquery.min.js");
		srb.addUrlMappings("/pobstyle.css");
		srb.addUrlMappings("/sealsetup.exe");
        return srb;// 
    }
	
	/**
	 * 添加印章管理程序Servlet（可选）
	 * @return
	 */
	@Bean
    public ServletRegistrationBean servletRegistrationBean2() {
		com.zhuozhengsoft.pageoffice.poserver.AdminSeal adminSeal = new com.zhuozhengsoft.pageoffice.poserver.AdminSeal();
//		adminSeal.setAdminPassword(poPassWord);//设置印章管理员admin的登录密码
		adminSeal.setSysPath(poSysPath);//设置印章数据库文件poseal.db存放的目录
		ServletRegistrationBean srb = new ServletRegistrationBean(adminSeal);
		srb.addUrlMappings("/adminseal.zz");
		srb.addUrlMappings("/sealimage.zz");
		srb.addUrlMappings("/loginseal.zz");
        return srb;// 
    }
}
