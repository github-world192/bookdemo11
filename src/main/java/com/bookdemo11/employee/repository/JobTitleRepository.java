package com.bookdemo11.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.employee.entity.JobTitle;

public interface JobTitleRepository extends JpaRepository<JobTitle, Integer> {
    List<JobTitle> findByStatus(Byte status);
}