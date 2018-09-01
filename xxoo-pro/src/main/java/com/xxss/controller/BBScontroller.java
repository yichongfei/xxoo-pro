package com.xxss.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxss.aws.s3.AmazonS3Object;
import com.xxss.config.AccountConfig;
import com.xxss.config.BBSconfig;
import com.xxss.config.S3Config;
import com.xxss.dao.AccountService;
import com.xxss.dao.ArticleService;
import com.xxss.dao.CardService;
import com.xxss.dao.VideoService;
import com.xxss.entity.Account;
import com.xxss.entity.Article;
import com.xxss.util.AccountCache;
import com.xxss.util.ImgUtil;
@Controller
public class BBScontroller {
	
	public static List<Article> stickList = new ArrayList<Article>();
	
	
	@Autowired
	private VideoService videoService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private ArticleService articleservice;
	
	/**
	 * 跳转到论坛
	 *  
	 * @param request
	 * @return
	 */
	@RequestMapping("/bbs/{page}")
	public String bbs(@PathVariable(value = "page") Integer page,Model model) {
		
		
		
		
		
		
		if(stickList.size() == 0) {
			stickList = articleservice.findByisStickLike("1");
		}
		Sort sort = new Sort(Direction.DESC, "publishTime");
		Pageable pageable = new PageRequest(page, 10, sort);
		Page<Article> articleList = articleservice.findByisStickLike("0",pageable);
		
		getArticleName(articleList);
		getArticleName(stickList);
		
		model.addAttribute("stickList", stickList);
		model.addAttribute("articleList",articleList);
		model.addAttribute("countpage", articleList.getTotalPages());
		model.addAttribute("curpage", page);
		
		return "bbs";
	}
	
	/**
	 * 跳转到论坛发布文章界面
	 *  
	 * @param request
	 * @return
	 */
	@RequestMapping("/bbs/goPublishArticle")
	public String goPublishArticle(HttpServletRequest request) {
		return "publishArticle";
	}
	
	
	/**
	 * 发布文章:性息
	 *  
	 * @param request
	 * @return
	 */
	@RequestMapping("/bbs/publishArticleInformation")
	@ResponseBody
	public void publishArticle(HttpServletRequest request,String context,String accountid,String category,String city,String title) {
		
		Document html = Jsoup.parse(context);
		Elements imgs = html.getElementsByTag("img");
		if(imgs.size()>0) {
			for (Element element : imgs) {
				String base64 = element.attr("src");
				String generateImage = ImgUtil.GenerateImage(base64);
				String keyname = AmazonS3Object.uploadFile1(new File(generateImage), "talent-xinjiapo", BBSconfig.S3BBSPHOTO_PATH);
				keyname =AccountConfig.S3PATH+keyname;
				element.attr("src", keyname);
			}
		}
		
		Article article = new Article();
		article.setAccountId(accountid);
		article.setCity(city);
		article.setCategory(category);
		article.setContext(html.toString());
		article.setId(UUID.randomUUID().toString());
		article.setPublishTime(System.currentTimeMillis());
		article.setTitle(title);
		article.setIsStick("0");
		articleservice.save(article);
	}
	
	/**
	 * 发布文章 :非性息类
	 *  
	 * @param request
	 * @return
	 */
	@RequestMapping("/bbs/publishArticle")
	@ResponseBody
	public void publishArticle(HttpServletRequest request,String context,String accountid,String category,String title) {

		Document html = Jsoup.parse(context);
		Elements imgs = html.getElementsByTag("img");
		if(imgs.size()>0) {
			for (Element element : imgs) {
				String base64 = element.attr("src");
				String generateImage = ImgUtil.GenerateImage(base64);
				String keyname = AmazonS3Object.uploadFile1(new File(generateImage), "talent-xinjiapo", BBSconfig.S3BBSPHOTO_PATH);
				keyname =AccountConfig.S3PATH+keyname;
				element.attr("src", keyname);
			}
		}
		
		
		Article article = new Article();
		article.setAccountId(accountid);
		article.setCategory(category);
		article.setContext(context);
		article.setId(UUID.randomUUID().toString());
		article.setPublishTime(System.currentTimeMillis());
		article.setTitle(title);
		article.setIsStick("0");
		articleservice.save(article);
	}
	
	
	public static List<Article> getArticleName(List<Article> list){
		 for (Article article : list) {
			 article.setAccountName(AccountCache.accountMap.get(article.getAccountId()).getName());
		 }
		 return list;
	}
	
	public static List<Article> getArticleName(Page<Article> list){
		 List<Article> resultlist = list.getContent();
		 for (Article article : resultlist) {
			 article.setAccountName(AccountCache.accountMap.get(article.getAccountId()).getName());
		 }
		 return resultlist;
	}
	
	
}
