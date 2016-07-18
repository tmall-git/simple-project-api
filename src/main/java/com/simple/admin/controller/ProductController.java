package com.simple.admin.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import com.simple.model.AgentSeller;
import com.simple.model.PageResult;
import com.simple.model.Product;
import com.simple.model.ProductImage;
import com.simple.model.ShopList;
import com.simple.model.ShopProduct;
import com.simple.model.User;
import com.simple.service.AgentSellerService;
import com.simple.service.ProductService;
import com.simple.service.UserService;

@Controller
@RequestMapping(value = "/product")
public class ProductController {
	
	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	ProductService productService;
	@Autowired
	UserService userService;
	@Autowired
	AgentSellerService agentSellerService;
	
	@RequestMapping(value="productOwners",method=RequestMethod.GET)
	@ResponseBody
	public String productPhonelist(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User seller = LoginUserUtil.getCurrentUser(request);
			List<String> owners = productService.queryProductOwners(pageIndex, pageSize);
			List<ShopList> shoplist = null;
			DecimalFormat df=new DecimalFormat(".##");
			if ( null != owners ) {
				shoplist = new ArrayList<ShopList>();
				for ( int i = 0 ; i < owners.size() ; i ++) {
					String owner = owners.get(i);
					User user = userService.queryByPhone(owner);
					ShopList sl = new ShopList();
					sl.setOwner(owner);
					sl.setOwnerName(user.getWeChatNo());
					sl.setZhuying(user.getCategory());
					List<String> ownerlist = new ArrayList<String>();
					ownerlist.add(owner);
					int productCount = productService.queryCount(null, ownerlist, Constant.PRODUCT_STATUS_ONLINE);
					sl.setProductCount(productCount);
					List<Product> products = productService.queryList(null, ownerlist, Constant.PRODUCT_STATUS_ONLINE, 1, 2);
					boolean isJoin = false;
					if ( null != products) {
						List<ShopProduct>  shopProducts = new ArrayList<ShopProduct>();
						for ( int j = 0 ; j < products.size() ; j ++) {
							Product p = products.get(j);
							ShopProduct sp = new ShopProduct();
							sp.setProductName(p.getName());
							sp.setPrice(df.format(p.getPrice()));
							sp.setImage(p.getFirstImg());
							//查询设置的提成
							List<AgentSeller> ass = agentSellerService.queryListByPhone(owner, seller.getUserPhone(), 1, 1);
							if ( null != ass && ass.size() > 0) {
								isJoin  = true;
							}else {
								ass = agentSellerService.queryListByPhone(owner, null, 1, 1);
							}
							double syscharge = agentSellerService.queryCharge();
							double percent = syscharge;
							if (null != ass && ass.size() > 0) {
								AgentSeller as = ass.get(0);
								percent = as.getChargePercent();
							}
							double chargedouble = p.getPrice()*(percent-syscharge)/100.00;
							if (chargedouble<0) {
								chargedouble = 0d;
							}
							sp.setCharge(df.format(chargedouble));
							shopProducts.add(sp);
						}
						sl.setProducts(shopProducts);
					}
					sl.setJoin(isJoin);
					shoplist.add(sl);
				}
			}
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"查询产品成功", shoplist);
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"查询产品错误:"+e.getLocalizedMessage(), null);
		}
	}
	
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
	
	// TODO 添加是否有seller验证
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
