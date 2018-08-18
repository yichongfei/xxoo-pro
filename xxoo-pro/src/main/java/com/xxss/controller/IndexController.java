package com.xxss.controller;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.xxss.dao.AccountService;
import com.xxss.dao.CardService;
import com.xxss.dao.VideoService;
import com.xxss.entity.Video;



@Controller
public class IndexController {
	@Autowired
	private VideoService videoService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CardService cardService;
	
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
	
	
}
