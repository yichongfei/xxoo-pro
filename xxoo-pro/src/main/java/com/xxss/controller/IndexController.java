package com.xxss.controller;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxss.dao.AccountService;
import com.xxss.dao.CardService;
import com.xxss.dao.VideoService;
import com.xxss.entity.Account;
import com.xxss.entity.Card;
import com.xxss.entity.Result;
import com.xxss.entity.Video;



@Controller
public class IndexController {
	@Autowired
	private VideoService videoService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CardService cardService;
	
	
	//每播放一次记录IP
	private static ConcurrentHashMap<String, Integer> playTimes = new ConcurrentHashMap<String, Integer>();
	
	
	public static long dayEnd = getEndTime();
	
	@RequestMapping("/")
	public String index(Model model, HttpServletRequest request) {
		return "index";
	}
	
	/**
	 * 根据分类查询
	 * @param category
	 * @param page
	 * @param model
	 * @return
	 */
	@RequestMapping("/listVideo/{category}/{page}")
	public String listVideo(@PathVariable(value = "category") String category,
			@PathVariable(value = "page") Integer page, Model model) {
		if (category == null) {
			model.addAttribute("name", "china");
		} else {
			model.addAttribute("name", category);
		}

		Sort sort = new Sort(Direction.DESC, "uploadTime");
		Pageable pageable = new PageRequest(page, 24, sort);
		List<Video> list = videoService.findBycategory(pageable, category);
		model.addAttribute("videos", list);
		model.addAttribute("count", videoService.getCountByCategory(category));
		model.addAttribute("curpage", page);
		return "listmovies";
	}
	
	
	/**
	 * 分页查询最新的视频
	 * @param page
	 * @param model
	 * @return
	 */
	@RequestMapping("/listNewVideo/{page}")
	public String listNewVideo(@PathVariable(value = "page") Integer page, Model model) {
		Sort sort = new Sort(Direction.DESC, "uploadTime");
		Pageable pageable = new PageRequest(page, 24, sort);
		Page<Video> list = videoService.findAll(pageable);
		model.addAttribute("videos", list);
		model.addAttribute("countpage", list.getTotalPages());
		model.addAttribute("curpage", page);
		return "listNewVideo";
	}
	/**
	 * 分页查询播放次数最多的视频
	 * @param page
	 * @param model
	 * @return
	 */
	@RequestMapping("/listHotVideo/{page}")
	public String listHotVideo(@PathVariable(value = "page") Integer page, Model model) {
		Sort sort = new Sort(Direction.DESC, "playTimes");
		Pageable pageable = new PageRequest(page, 24, sort);
		Page<Video> list = videoService.findAll(pageable);
		model.addAttribute("videos", list);
		model.addAttribute("countpage", list.getTotalPages());
		model.addAttribute("curpage", page);
		return "listHotVideo";
	}
	/**
	 * 跳转到观看视频页面
	 * 
	 * @return
	 */
	@RequestMapping("/goVideoPlay")
	public String goVideoPlay(HttpServletRequest request, Model model,
			@RequestParam(value = "id", required = false, defaultValue = "") String id) {
		model.addAttribute("id", id);
		Sort sort = new Sort(Direction.DESC, "playTimes");
		Random ra =new Random();
		Pageable pageable = new PageRequest(ra.nextInt(10)+1, 8, sort);
		Page<Video> list = videoService.findAll(pageable);
		model.addAttribute("videos", list);
		
		HttpSession session = request.getSession();
		Account account = (Account) session.getAttribute("account");
		if (account != null && account.getVipDeadline() > System.currentTimeMillis()) {
			return "videoPlay";// 会员观看次数不限制
		}

		if (System.currentTimeMillis() > dayEnd) {
			resetPlayTimes();// 每天重置播放次数记录数据
		}

		String addr = getIpAddr(request);
		if (playTimes.containsKey(addr)) {
			if (playTimes.get(addr) > 10) {
				return "videoPlayNon";
			}

			Integer times = playTimes.get(addr) + 1;
			playTimes.put(addr, times);
		} else {
			playTimes.put(addr, 1);
		}
		
		
		return "videoPlay";

	}
	
	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		return ip;
	}

	private static void resetPlayTimes() {
		dayEnd = getEndTime();
		playTimes.clear();
	}
	
	private static long getStartTime() {
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTimeInMillis();
	}

	private static long getEndTime() {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTimeInMillis();
	}
	
	
	
	
	
	@RequestMapping("/information")
	public String information(HttpServletRequest request) {
		// 判断有没有登录,如果没有登录,则返回到登录界面
		HttpSession session = request.getSession();
		Account account = (Account) session.getAttribute("account");
		if (account.getEmail().equals("游客")) {
			return "pages-sign-in";
		}

		return "informationnew";
	}
	/**
	 * 跳转到充值页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/chongzhi")
	public String chongzhi(HttpServletRequest request) {

		return "chongzhi";

	}

	@RequestMapping("/chongzhivip")
	@ResponseBody
	public Result chongzhivip(String email, String key, String secret, HttpServletRequest request) {
		Result result = new Result();
		Account account = accountService.findByemail(email);
		if (account == null) {
			result.setSuccess(false);
			result.setInformation("账号不存在");
			return result;
		}

		if (!key.equals("") && !secret.equals("")) {
			Card card = cardService.findBykeyWords(key);
			if (card != null && card.getSecret().equals(secret) && card.isAvailable() == true) {
				account.updateVip(card);
				accountService.saveAndFlush(account);
				card.setAvailable(false);
				cardService.saveAndFlush(card);
				result.setSuccess(true);
				result.setObject(account);
				result.setInformation("充值VIP " + card.getMonths() + "个月成功");
				HttpSession session = request.getSession();
				session.setAttribute("account", account);// 更新session中账户信息
				return result;
			}
		}
		result.setSuccess(false);
		result.setInformation("充值失败,请查看卡密是否准确,如有疑问,请联系客服QQ");

		return result;

	}
}
