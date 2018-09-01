package com.xxss.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xxss.entity.Article;

public interface ArticleService  extends JpaRepository<Article, String>{
	Article findByid(String id);
	
	List<Article> findByisStickLike(String isStick);
	
	Page<Article> findByisStickLike(String isStick,Pageable pageable);
}
