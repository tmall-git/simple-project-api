package com.simple.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.model.Product;
import com.simple.service.ProductService;

@Controller
@RequestMapping(value = "/product")
public class ProductController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	ProductService productService;
	
	
	@RequestMapping(value="info",method=RequestMethod.GET)
	@ResponseBody
	public String info(Integer id,HttpServletRequest request, HttpServletResponse response){
		try {
			Product product = productService.getById(id);
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"查询产品成功", product);
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"查询产品成功", null);
		}
	}
	
	@RequestMapping(value="buy",method=RequestMethod.POST)
	@ResponseBody
	public String buy(Integer id,HttpServletRequest request, HttpServletResponse response){
		try {
			int stock = productService.reduceStock(id);
			if (stock<=0) {
				return  AjaxWebUtil.sendAjaxResponse(request, response, false,"购买失败!商品没有库存.", null);
			}
			//TODO 后续购买流程
			return null;
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"购买失败!", e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "add",method=RequestMethod.POST)
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response) {
		try {
			String name = AjaxWebUtil.getRequestParameter(request,"name");
			String stock = AjaxWebUtil.getRequestParameter(request,"stock");
			String price = AjaxWebUtil.getRequestParameter(request,"price");
			String description = AjaxWebUtil.getRequestParameter(request,"description");
			String tip = AjaxWebUtil.getRequestParameter(request,"tip");
			String productStatus = AjaxWebUtil.getRequestParameter(request,"productStatus");
			String[] imges = request.getParameterValues("images");
			Product p = new Product();
			p.setName(name);
			p.setStock(Integer.parseInt(stock));
			p.setPrice(Double.parseDouble(price));
			p.setDescription(description);
			p.setTip(tip);
			p.setProductStatus(Integer.parseInt(productStatus));
			p.setOwner(LoginUserUtil.getCurrentUser(request).getUserPhone());
			productService.insert(p, imges);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"添加成功", null);
		}catch(Exception e) {
			log.error("添加失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"添加失败", null);
		}
	}
	
	@RequestMapping(value = "update",method=RequestMethod.POST)
	@ResponseBody
	public String update(int id,HttpServletRequest request, HttpServletResponse response) {
		try {
			String name = AjaxWebUtil.getRequestParameter(request,"name");
			String stock = AjaxWebUtil.getRequestParameter(request,"stock");
			String price = AjaxWebUtil.getRequestParameter(request,"price");
			String description = AjaxWebUtil.getRequestParameter(request,"description");
			String tip = AjaxWebUtil.getRequestParameter(request,"tip");
			String productStatus = AjaxWebUtil.getRequestParameter(request,"productStatus");
			String[] imges = request.getParameterValues("images");
			String[] imagearrays = null;
			Product p = productService.getById(id);
			p.setName(name);
			p.setStock(Integer.parseInt(stock));
			p.setPrice(Double.parseDouble(price));
			p.setDescription(description);
			p.setTip(tip);
			p.setProductStatus(Integer.parseInt(productStatus));
			productService.insert(p, imges);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"修改成功", null);
		}catch(Exception e) {
			log.error("修改失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"修改失败", null);
		}
	}
	
	@RequestMapping(value = "delete",method=RequestMethod.GET)
	@ResponseBody
	public String delete(Integer id,int status,HttpServletRequest request, HttpServletResponse response) {
		try {
			productService.updateProductStatus(id,status);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"更新成功", null);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"更新失败", e.getMessage());
		}
	}
}
