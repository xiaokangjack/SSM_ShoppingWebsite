package com.enjoyshop.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enjoyshop.web.bean.Cart;
import com.enjoyshop.web.bean.Item;
import com.enjoyshop.web.bean.Order;
import com.enjoyshop.web.bean.User;
import com.enjoyshop.web.service.CartService;
import com.enjoyshop.web.service.ItemService;
import com.enjoyshop.web.service.OrderService;
import com.enjoyshop.web.threadlocal.UserThreadLocal;

@RequestMapping("order")
@Controller
public class OrderController {
	@Autowired
	private ItemService itemService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CartService cartService;

	@RequestMapping(value = "{itemId}", method = RequestMethod.GET)
	public ModelAndView toOrder(@PathVariable("itemId") Long itemId) {
		ModelAndView mv = new ModelAndView("order");
		Item item = this.itemService.queryItemById(itemId);
		mv.addObject("item", item);
		return mv;
	}

	/**
	 * 基于购物车下单
	 * 
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.GET)
	public ModelAndView toCartOrder() {
		ModelAndView mv = new ModelAndView("order-cart");
		User user = UserThreadLocal.get();
		List<Cart> carts = this.cartService.queryCartListByUserId(user.getId());
		mv.addObject("carts", carts);
		return mv;
	}

	@RequestMapping(value = "submit", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> submitOrder(Order order) {
		Map<String, Object> result = new HashMap<String, Object>();
		String orderId = this.orderService.submitOrder(order);
		if (StringUtils.isEmpty(orderId)) {
			result.put("status", 300);
		} else {
			result.put("status", 200);
			result.put("data", orderId);
		}

		return result;
	}

	@RequestMapping("success")
	public ModelAndView success(@RequestParam("id") String orderId) {
		ModelAndView mv = new ModelAndView("success");
		Order order = this.orderService.queryOrderById(orderId);
		mv.addObject("order", order);
		// 当前时间推后2天，格式化：xx月xx日
		mv.addObject("date", new DateTime().plusDays(2).toString("MM月dd日"));
		return mv;
	}

}
