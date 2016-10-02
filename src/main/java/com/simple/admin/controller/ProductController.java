package com.simple.admin.controller;

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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.admin.util.ProductTokenUtil;
import com.simple.common.config.EnvPropertiesConfiger;
import com.simple.common.util.DoubleUtil;
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
import com.simple.weixin.auth.JsConfigInfo;
import com.simple.weixin.auth.WeiXinAuth;

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
					if (owner.equals(seller.getUserPhone())) {
						continue;
					}
					User user = userService.queryByPhone(owner,true);
					if ( null == user ) {
						continue;
					}
					Map map = getAgentSellerPercent(user,seller);
					if (!isAllow(map)) {
						continue;
					}
					ShopList sl = new ShopList();
					sl.setOwner(owner);
					//sl.setOwnerName(user.getWeChatNo());
					sl.setOwnerName(user.getUserNick());
					sl.setZhuying(user.getCategory());
					sl.setHeadimg(user.getHeadimg());
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
			User user = userService.queryByPhone(owner,true);
			if ( null == user){
				return  AjaxWebUtil.sendAjaxResponse(request, response, false,"该代理帐号无效", null);
			}
			Map map = getAgentSellerPercent(user,seller);
			if (!isAllow(map)) {
				return  AjaxWebUtil.sendAjaxResponse(request, response, false,"该代理禁止代销啦", null);
			}
			List<String> ownerlist = new ArrayList<String>();
			ownerlist.add(owner);
			List<ShopProduct> splist = queryShopProduct(ownerlist,pageIndex,pageSize,getPercent(map),getSysCharge());
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
		List<Product> products = productService.queryList(null, ownerlist, Constant.PRODUCT_STATUS_ONLINE, pageIndex, pageSize,true);
		if ( null != products) {
			List<ShopProduct>  shopProducts = new ArrayList<ShopProduct>();
			for ( int j = 0 ; j < products.size() ; j ++) {
				Product p = products.get(j);
				ShopProduct sp = new ShopProduct();
				sp.setProductName(p.getName());
				sp.setPrice(String.valueOf(DoubleUtil.formatDouble((p.getPrice()))));
				sp.setImage(p.getFirstImg());
				double chargedouble = p.getPrice()*(percent-syscharge)/100.00;
				if (chargedouble<0) {
					chargedouble = 0d;
				}
				sp.setCharge(String.valueOf(DoubleUtil.formatPrice(chargedouble)));
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
			PageResult products= productService.query(null, owners ,Constant.PRODUCT_STATUS_ONLINE, pageIndex, pageSize,false);
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
			if (product.getProductStatus()==Constant.PRODUCT_STATUS_OFFLINE) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"产品已经下架", null);
			}
			if (product.getProductStatus()==Constant.PRODUCT_STATUS_DELETE) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"产品已经被删除", null);
			}
			List<ProductImage> pi = productService.getImage(id);
			if ( null != pi && pi.size() > 0 ) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0 ; i < pi.size() ; i ++) {
					ProductImage pii = pi.get(i);
					if (i==0) {
						sb.append("[");
					}
					if (i==pi.size()-1) {
						sb.append("\"").append(pii.getImagePath(pii.getImage())).append("\"").append("]");
					}else {
						sb.append("\"").append(pii.getImagePath(pii.getImage())).append("\"").append(",");
					}
				}
				product.setThumbnail(sb.toString());
			}
			User owner = userService.queryByPhone(product.getOwner(),true);
			if ( null == owner) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"该产品代理无效", null);
			}
			double percent = owner.getChargePrecent()-agentSellerService.queryCharge();
			if (percent<0d) {
				percent = 0d;
			}
			String token = request.getParameter("token");
			if (!StringUtils.isEmpty(token)) {
				String sellerPhone = ProductTokenUtil.validToken(id, token);
				if (StringUtils.isEmpty(sellerPhone)) {
					return AjaxWebUtil.sendAjaxResponse(request, response, false,"token无效", null);
				}
				User seller = userService.queryByPhone(sellerPhone,true);
				if (null == seller) {
					return AjaxWebUtil.sendAjaxResponse(request, response, false,"代销账户无效", null);
				}
				product.setSellerWeChatNo(seller.getWeChatNo());
				product.setSellerNickName(seller.getUserNick());
				product.setHeadimg(seller.getHeadimg());
			}
			
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"查询产品成功", product);
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"查询产品失败:"+e.getLocalizedMessage(), null);
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
			p.setFirstImg(firstImg);
			productService.insert(p, imges);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"添加成功", null);
		}catch(Exception e) {
			log.error("添加失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"添加失败:"+e.getLocalizedMessage(), null);
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
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"修改失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	@RequestMapping(value = "delete",method=RequestMethod.GET)
	@ResponseBody
	public String delete(Integer id,HttpServletRequest request, HttpServletResponse response) {
		try {
			productService.updateProductStatus(id,Constant.PRODUCT_STATUS_OFFLINE);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"更新成功", null);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"更新失败:"+e.getLocalizedMessage(), e.getMessage());
		}
	}
	
	@RequestMapping(value = "shareConfgi",method=RequestMethod.GET)
	@ResponseBody
	public String shareConfgi(int id,String from,String owen,String ran,HttpServletRequest request, HttpServletResponse response) {
		try {
//			StringBuffer url = request.getRequestURL();
//			String queryString =request.getQueryString();
//			if(!StringUtils.isEmpty(queryString)){
//				url.append("?").append(queryString);
//			}
			JsConfigInfo config = WeiXinAuth.getJsConfigInfo(String.format(EnvPropertiesConfiger.getValue("weixin_js_ticket_url"),id,from,owen,ran));
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"获取配置成功", config);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"获取配置失败:"+e.getLocalizedMessage(), e.getMessage());
		}
	}
	
	@RequestMapping(value = "share",method=RequestMethod.GET)
	@ResponseBody
	public String share(Integer id,HttpServletRequest request, HttpServletResponse response) {
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			String token = ProductTokenUtil.getToken(id, user.getUserPhone());
			//更新用户分享次数
			//Product product = productService.getById(id, false);
			//if (product.getProductStatus()==Constant.PRODUCT_STATUS_OFFLINE) {
			//	return AjaxWebUtil.sendAjaxResponse(request, response, false,"产品已经下架，不能分享", null);
			//}
			//if ( null != product) {
			//	agentSellerService.increaseWatchCount(product.getOwner(), user.getUserPhone());
			//}
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"分享成功", token);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"分享失败:"+e.getLocalizedMessage(), e.getMessage());
		}
	}
	
	@RequestMapping(value = "sharePage",method=RequestMethod.GET)
	@ResponseBody
	public String sharePage(Integer id,String token,HttpServletRequest request, HttpServletResponse response) {
		try {
			String sellerPhone = ProductTokenUtil.validToken(id, token);
			if (StringUtils.isEmpty(sellerPhone)) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"token无效", null);
			}
			//更新用户分享次数
			Product product = productService.getById(id, false);
			if ( null != product) {
				if (product.getProductStatus()!=Constant.PRODUCT_STATUS_OFFLINE) {
					agentSellerService.increaseWatchCount(product.getOwner(), sellerPhone);
				}
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
			if (StringUtils.isEmpty(sellerPhone)) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"token无效", null);
			}
			User seller = userService.queryByPhone(sellerPhone,true);
			if (null == seller) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"代销账户无效", null);
			}
			Product product = productService.getById(id,false);
			if (product.getProductStatus()==Constant.PRODUCT_STATUS_OFFLINE) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"产品已经下架，不能购买", null);
			}
			if (product.getProductStatus()==Constant.PRODUCT_STATUS_DELETE) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"产品已经被删除", null);
			}
			List<ProductImage> pi = productService.getImage(id);
			if ( null != pi && pi.size() > 0 ) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0 ; i < pi.size() ; i ++) {
					if (i==0) {
						sb.append("[");
					}
					if (i==pi.size()-1) {
						sb.append("\"").append(pi.get(i).getImagePath(pi.get(i).getImage())).append("\"").append("]");
					}else {
						sb.append("\"").append(pi.get(i).getImagePath(pi.get(i).getImage())).append("\"").append(",");
					}
				}
				product.setThumbnail(sb.toString());
			}
			product.setSellerPhone(seller.getUserPhone());
			product.setSellerWeChatNo(seller.getWeChatNo());
			product.setSellerNickName(seller.getUserNick());
			product.setHeadimg(seller.getHeadimg());
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"查询产品成功", product);
		}
		catch (Exception e){
			e.printStackTrace();
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"查询产品失败"+e.getLocalizedMessage(), null);
		}
	}
	
}
