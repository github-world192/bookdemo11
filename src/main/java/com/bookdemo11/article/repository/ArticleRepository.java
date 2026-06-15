package com.bookdemo11.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
}