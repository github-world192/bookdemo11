package com.bookdemo11.employee.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.employee.entity.JobTitle;
import com.bookdemo11.employee.repository.JobTitleRepository;

@Service
public class JobTitleService {

    private final JobTitleRepository jobTitleRepository;

    public JobTitleService(JobTitleRepository jobTitleRepository) {
        this.jobTitleRepository = jobTitleRepository;
    }

    public List<JobTitle> findAll() {
        return jobTitleRepository.findAll();
    }

    public List<JobTitle> findActive() {
        return jobTitleRepository.findByStatus((byte) 1);
    }

    public Optional<JobTitle> findById(Integer id) {
        return jobTitleRepository.findById(id);
    }

    @Transactional
    public JobTitle save(String name, Byte status) {
        JobTitle title = new JobTitle();
        title.setJobTitleName(name);
        title.setStatus(status != null ? status : (byte) 1);
        return jobTitleRepository.save(title);
    }

    @Transactional
    public JobTitle update(Integer id, String name, Byte status) {
        JobTitle title = jobTitleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("職稱不存在"));
        title.setJobTitleName(name);
        title.setStatus(status != null ? status : (byte) 1);
        return jobTitleRepository.save(title);
    }
}