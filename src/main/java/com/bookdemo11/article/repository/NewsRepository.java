package com.bookdemo11.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.article.entity.News;

public interface NewsRepository extends JpaRepository<News, Integer> {
}