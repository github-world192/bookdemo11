package com.bookdemo11.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.article.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}