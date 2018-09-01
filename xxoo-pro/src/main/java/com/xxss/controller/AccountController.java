package com.xxss.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxss.aws.s3.AmazonS3Object;
import com.xxss.config.AccountConfig;
import com.xxss.dao.AccountService;
import com.xxss.dao.CardService;
import com.xxss.dao.VideoService;
import com.xxss.entity.Account;
import com.xxss.entity.Result;
import com.xxss.util.ImgUtil;

@Controller
public class AccountController {


	@Autowired
	private VideoService videoService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CardService cardService;
	/**
	 * 注册账号
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	@RequestMapping("/account/zhuce")
	@ResponseBody
	public Result register(String email, String password) {
		Account findByemail = accountService.findByemail(email);
		Result result = new Result();
		if (findByemail == null) {
			accountService.save(new Account(email, password));
			result.setSuccess(true);
			result.setInformation("注册成功,欢迎来到星辰社区");
			return result;
		} else {
			result.setSuccess(false);
			result.setInformation("账号已存在,请您重新注册");
			return result;
		}

	}
	/**
	 * 登录到XXSS
	 * 
	 * @param email
	 *            账号
	 * @param password
	 *            密码
	 * @return
	 */
	@RequestMapping("/account/login")
	@ResponseBody
	public Result loginXXSS(String email, String password, HttpServletRequest request) {
		HttpSession session = request.getSession();
		Account account = accountService.findByemail(email);
		Result result = new Result();
		if (account == null) {
			result.setSuccess(false);
			result.setInformation("登录失败,账号不存在");
			return result;
		} else if (account.getPassword().equals(password)) {
			result.setSuccess(true);
			result.setInformation("登录成功,欢迎回来,祝您愉快,星辰社区永远陪伴您");
			result.setObject(account);
			session.setAttribute("account", account);
			return result;
		} else if (!account.getPassword().equals(password)) {
			result.setSuccess(false);
			result.setInformation("登录失败,您的密码错误");
			return result;
		} else {
			return null;
		}

	}
	
	
	/**
	 * 退出登录
	 * 
	 * @param email
	 * @param request
	 * @return
	 */
	@RequestMapping("/account/exit")
	public String exit(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("account");
		Account account = new Account();
		account.setEmail("游客");
		session.setAttribute("account", account);
		return "index";

	}
	
	/**
	 * 更新头像
	 * 
	 * @param email
	 * @param request
	 * @return
	 */
	@RequestMapping("/account/updatePic")
	@ResponseBody
	public void updatePic(HttpServletRequest request,String base64url) {
		HttpSession session = request.getSession();
		Account account = (Account) session.getAttribute("account");
		String generateImage = ImgUtil.GenerateImage(base64url);
		String keyname = AmazonS3Object.uploadFile1(new File(generateImage), "talent-xinjiapo", "bbsphoto/");
		account.setPicPath(AccountConfig.S3PATH+keyname);
		accountService.save(account);
	}
	
	

	/**
	 * 更新账号
	 * 
	 * @param email
	 * @param request
	 * @return
	 */
	@RequestMapping("/account/update")
	@ResponseBody
	public void updateAccount(HttpServletRequest request,String password,String name,String description) {
		HttpSession session = request.getSession();
		Account account = (Account) session.getAttribute("account");
		account.setPassword(password);
		account.setName(name);
		account.setDescription(description);
		accountService.save(account);
		session.setAttribute("account", account);
	}
	
	
	
	
	
	
	
	
	
}
