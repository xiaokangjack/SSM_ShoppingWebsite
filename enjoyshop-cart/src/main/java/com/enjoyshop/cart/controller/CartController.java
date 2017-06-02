package com.enjoyshop.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.enjoyshop.cart.bean.User;
import com.enjoyshop.cart.pojo.Cart;
import com.enjoyshop.cart.service.CartCookieService;
import com.enjoyshop.cart.service.CartService;
import com.enjoyshop.cart.threadlocal.UserThreadLocal;

@RequestMapping("cart")
@Controller
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartCookieService cartCookieService;

	/**
	 * 加入商品到购物车
	 * 
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value = "{itemId}", method = RequestMethod.GET)
	public String addItemToCart(@PathVariable("itemId") Long itemId, HttpServletRequest request,
			HttpServletResponse response) {
		User user = UserThreadLocal.get();
		if (null == user) {
			// 未登录状态
			this.cartCookieService.addItemToCart(itemId, request, response);
		} else {
			// 登录状态
			this.cartService.addItemToCart(itemId);
		}
		// 重定向到购物车列表页面
		return "redirect:/cart/list.html";
	}

	/**
	 * 对外提交接口，根据用户id查询购物车列表（数据库查询）
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, params = "userId")
	public ResponseEntity<List<Cart>> queryCartListByUserId(@RequestParam("userId") Long userId) {
		try {
			List<Cart> carts = this.cartService.queryCartList(userId);
			if (null == carts || carts.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(carts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	/**
	 * 显示购物车列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public ModelAndView showCartList(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("cart");
		List<Cart> cartList = null;
		User user = UserThreadLocal.get();
		if (null == user) {
			// 未登录状态
			cartList = this.cartCookieService.queryCartList(request);
		} else {
			// 登录状态
			cartList = this.cartService.queryCartList();
		}
		mv.addObject("cartList", cartList);
		return mv;
	}

	/**
	 * 修改购买商品的数量
	 * 
	 * @param itemId
	 * @param num 最终购买的数量
	 * @return
	 */
	@RequestMapping(value = "update/num/{itemId}/{num}", method = RequestMethod.POST)
	public ResponseEntity<Void> updateNum(@PathVariable("itemId") Long itemId, @PathVariable("num") Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		User user = UserThreadLocal.get();
		if (null == user) {
			// 未登录状态
			this.cartCookieService.updateNum(itemId, num, request, response);
		} else {
			// 登录状态
			this.cartService.updateNum(itemId, num);
		}
		// 204
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 删除购物车中的商品
	 * 
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value = "delete/{itemId}", method = RequestMethod.GET)
	public String deleteItem(@PathVariable("itemId") Long itemId, HttpServletRequest request,
			HttpServletResponse response) {
		User user = UserThreadLocal.get();
		if (null == user) {
			// 未登录状态
			this.cartCookieService.deleteItem(itemId, request, response);
		} else {
			// 登录状态
			this.cartService.deleteItem(itemId);
		}
		// 重定向到购物车列表页面
		return "redirect:/cart/list.html";
	}
}
