package com.simple.admin.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.simple.admin.util.ProductTokenUtil;
import com.simple.common.util.Base64;
import com.simple.constant.Constant;
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
	
	/**
	 * 所有代理（有商品的用户）
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="productOwners",method=RequestMethod.GET)
	@ResponseBody
	public String productPhonelist(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User seller = LoginUserUtil.getCurrentUser(request);
			List<String> owners = productService.queryProductOwners(pageIndex, pageSize);
			List<ShopList> shoplist = null;
			
			if ( null != owners ) {
				shoplist = new ArrayList<ShopList>();
				double syscharge = getSysCharge();
				for ( int i = 0 ; i < owners.size() ; i ++) {
					String owner = owners.get(i);
					User user = userService.queryByPhone(owner);
					Map map = getAgentSellerPercent(user,seller);
					if (!isAllow(map)) {
						continue;
					}
					ShopList sl = new ShopList();
					sl.setOwner(owner);
					sl.setOwnerName(user.getWeChatNo());
					sl.setZhuying(user.getCategory());
					List<String> ownerlist = new ArrayList<String>();
					ownerlist.add(owner);
					int productCount = productService.queryCount(null, ownerlist, Constant.PRODUCT_STATUS_ONLINE);
					sl.setProductCount(productCount);
					List<ShopProduct> splist = queryShopProduct(ownerlist,1,2,getPercent(map),syscharge);
					sl.setProducts(splist);
					sl.setJoin(isJoin(map));
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
	
	/**
	 * 代销------》代理商品列表
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="agentProductList",method=RequestMethod.GET)
	@ResponseBody
	public String productPhonelist(String owner,int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User seller = LoginUserUtil.getCurrentUser(request);
			User user = userService.queryByPhone(owner);
			Map map = getAgentSellerPercent(user,seller);
			if (!isAllow(map)) {
				return  AjaxWebUtil.sendAjaxResponse(request, response, false,"该代理禁止代销啦", null);
			}
			List<String> ownerlist = new ArrayList<String>();
			ownerlist.add(owner);
			List<ShopProduct> splist = queryShopProduct(ownerlist,1,2,getPercent(map),getSysCharge());
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"查询产品成功", splist);
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"查询产品错误:"+e.getLocalizedMessage(), null);
		}
	}
	
	private double getSysCharge()  {
		return agentSellerService.queryCharge();
	}
	
	private boolean isAllow(Map agentSellerMap) {
		return (boolean)agentSellerMap.get("isAllow");
	}
	
	private boolean isJoin(Map agentSellerMap) {
		return (boolean)agentSellerMap.get("isJoin");
	}
	
	private double getPercent(Map agentSellerMap) {
		return (double)agentSellerMap.get("percent");
	}
	
	private Map getAgentSellerPercent(User owner,User seller) {
		Map map = new HashMap();
		//如果该用户不允许代销，则忽略
		if (!owner.isAllow()) {
			map.put("isAllow", false);
		}else {
			//查询设置的提成
			boolean isJoin = false;
			double percent = owner.getChargePrecent();
			List<AgentSeller> ass = agentSellerService.queryListByPhone(owner.getUserPhone(), seller.getUserPhone(), 1, 1);
			if ( null != ass && ass.size() > 0) {
				//如果设置了不允许代销，则不显示
				AgentSeller as0 = ass.get(0);
				if (!as0.isAllow()) {
					map.put("isAllow", false);
				}else {
					map.put("isAllow", true);
				}
				isJoin  = true;
				percent = as0.getChargePercent();
			}else {
				map.put("isAllow", true);
			}
			map.put("isJoin", isJoin);
			map.put("percent", percent);
		}
		return map;
	}
	
	private List<ShopProduct> queryShopProduct(List<String> ownerlist,int pageIndex,int pageSize,double percent,double syscharge) {
		List<Product> products = productService.queryList(null, ownerlist, Constant.PRODUCT_STATUS_ONLINE, pageIndex, pageSize);
		if ( null != products) {
			DecimalFormat df=new DecimalFormat(".##");
			List<ShopProduct>  shopProducts = new ArrayList<ShopProduct>();
			for ( int j = 0 ; j < products.size() ; j ++) {
				Product p = products.get(j);
				ShopProduct sp = new ShopProduct();
				sp.setProductName(p.getName());
				sp.setPrice(df.format(p.getPrice()));
				sp.setImage(p.getFirstImg());
				double chargedouble = p.getPrice()*(percent-syscharge)/100.00;
				if (chargedouble<0) {
					chargedouble = 0d;
				}
				sp.setCharge(df.format(chargedouble));
				sp.setStock(p.getStock());
				sp.setId(p.getId());
				shopProducts.add(sp);
			}
			return shopProducts;
		}
		return null;
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
			Product product = productService.getById(id,false);
			ProductImage pi = productService.getImage(id);
			if ( null != pi ) {
				product.setThumbnail(pi.getImage());
			}
			User owner = userService.queryByPhone(product.getOwner());
			product.setCharge(product.getPrice()*owner.getChargePrecent()/100.00);
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"查询产品成功", product);
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"查询产品成功", null);
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
			Product p = productService.getById(id,false);
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
	
	@RequestMapping(value = "share",method=RequestMethod.GET)
	@ResponseBody
	public String share(Integer id,HttpServletRequest request, HttpServletResponse response) {
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			String token = ProductTokenUtil.getToken(id, user.getUserPhone());
			//更新用户分享次数
			Product product = productService.getById(id, false);
			if ( null != product) {
				agentSellerService.increaseWatchCount(product.getOwner(), user.getUserPhone());
			}
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"分享成功", token);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"分享失败:"+e.getLocalizedMessage(), e.getMessage());
		}
	}
	
	@RequestMapping(value="infoToBuy",method=RequestMethod.GET)
	@ResponseBody
	public String info(Integer id,String token,HttpServletRequest request, HttpServletResponse response){
		try {
			String sellerPhone = ProductTokenUtil.validToken(id, token);
			User seller = userService.queryByPhone(sellerPhone);
			if (null == seller) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"token无效", null);
			}
			Product product = productService.getById(id,false);
			ProductImage pi = productService.getImage(id);
			if ( null != pi ) {
				product.setThumbnail(pi.getImage());
			}
			product.setSellerPhone(seller.getUserPhone());
			product.setSellerWeChatNo(seller.getWeChatNo());
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"查询产品成功", product);
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"查询产品成功", null);
		}
	}
	
}
