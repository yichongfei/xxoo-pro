package com.xxss.controller;

import java.util.Random;

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
import com.xxss.config.S3Config;
import com.xxss.dao.AccountService;
import com.xxss.dao.CardService;
import com.xxss.dao.VideoService;
import com.xxss.entity.Video;

@Controller
public class VideoController {
	@Autowired
	private VideoService videoService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CardService cardService;
	
	
	/**
	 * 获取要播放的PRE-URL
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/video/getVideo")
	@ResponseBody
	public Video getVideoUrl(String id) {
		if(id.endsWith("iframeload")) {
			id=id.split("#")[0];
		}
		Video video = videoService.findById(id);
		String preSignedURL = AmazonS3Object.getPreSignedURL(S3Config.VIDEOBUCKET, video.getMp4Key());
		video.setPreUrl(preSignedURL);
		return video;
	}
	
	
	
	
	
}
