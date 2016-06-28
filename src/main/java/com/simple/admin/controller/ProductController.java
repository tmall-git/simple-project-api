package com.simple.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.constant.Constant;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.model.PageResult;
import com.simple.model.Product;
import com.simple.model.ProductImage;
import com.simple.service.ProductService;

@Controller
@RequestMapping(value = "/product")
public class ProductController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	ProductService productService;
	
	@RequestMapping(value="list",method=RequestMethod.GET)
	@ResponseBody
	public String list(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			List<String> owners = new ArrayList<String>();
			owners.add(LoginUserUtil.getCurrentUser(request).getUserPhone());
			PageResult products= productService.query(null, owners ,Constant.PRODUCT_STATUS_ONLINE, pageIndex, pageSize);
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"查询产品成功", products);
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"查询产品成功", null);
		}
	}
	
	
	@RequestMapping(value="info",method=RequestMethod.GET)
	@ResponseBody
	public String info(Integer id,HttpServletRequest request, HttpServletResponse response){
		try {
			Product product = productService.getById(id);
			ProductImage pi = productService.getImage(id);
			if ( null != pi ) {
				product.setThumbnail(pi.getImage());
			}
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
			int stock = productService.updateStock(id);
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
			String name = AjaxWebUtil.getRequestParameter(request,"name",true);
			String stock = AjaxWebUtil.getRequestParameter(request,"stock");
			String price = AjaxWebUtil.getRequestParameter(request,"price");
			String description = AjaxWebUtil.getRequestParameter(request,"description",true);
			String tip = AjaxWebUtil.getRequestParameter(request,"tip",true);
			String firstImg = AjaxWebUtil.getRequestParameter(request,"firstImg");
			int productStatus = Constant.PRODUCT_STATUS_ONLINE;//AjaxWebUtil.getRequestParameter(request,"productStatus");
			String imges = AjaxWebUtil.getRequestParameter(request,"images");
//			List<String> images = JSON.parseArray(imges, String.class);
//			for (String im : images) {
//				ImageHandleUtil.uploadFile(im, desFile);
//			}
			Product p = new Product();
			p.setName(name);
			p.setStock(Integer.parseInt(stock));
			p.setPrice(Double.parseDouble(price));
			p.setDescription(description);
			p.setTip(tip);
			p.setProductStatus(productStatus);
			p.setOwner(LoginUserUtil.getCurrentUser(request).getUserPhone());
			p.setThumbnail(firstImg);
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
			String name = AjaxWebUtil.getRequestParameter(request,"name",true);
			String stock = AjaxWebUtil.getRequestParameter(request,"stock");
			String price = AjaxWebUtil.getRequestParameter(request,"price");
			String description = AjaxWebUtil.getRequestParameter(request,"description",true);
			String tip = AjaxWebUtil.getRequestParameter(request,"tip",true);
			//String productStatus = AjaxWebUtil.getRequestParameter(request,"productStatus");
			String imges = AjaxWebUtil.getRequestParameter(request,"images");
			String firstImg = AjaxWebUtil.getRequestParameter(request,"firstImg");
			Product p = productService.getById(id);
			p.setName(name);
			p.setStock(Integer.parseInt(stock));
			p.setPrice(Double.parseDouble(price));
			p.setDescription(description);
			p.setTip(tip);
			p.setFirstImg(firstImg);
			//p.setProductStatus(Integer.parseInt(productStatus));
			productService.update(p,imges);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"修改成功", null);
		}catch(Exception e) {
			log.error("修改失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"修改失败", null);
		}
	}
	
	@RequestMapping(value = "delete",method=RequestMethod.GET)
	@ResponseBody
	public String delete(Integer id,HttpServletRequest request, HttpServletResponse response) {
		try {
			productService.updateProductStatus(id,Constant.PRODUCT_STATUS_DELETE);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"更新成功", null);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"更新失败", e.getMessage());
		}
	}
}
