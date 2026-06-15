package com.bookdemo11.article.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bookdemo11.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ARTICLE")
@Getter
@Setter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ARTICLE_ID")
    private Integer articleId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;

    @NotBlank
    @Column(name = "CATEGORY", nullable = false, length = 100)
    private String category;

    @Column(name = "VIEW_COUNT", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "STATUS", nullable = false)
    private Byte status = 1;

    @NotBlank
    @Lob
    @Column(name = "CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Lob
    @Column(name = "COVER_IMAGE", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] coverImage = new byte[0];

    @NotBlank
    @Column(name = "TITLE", nullable = false)
    private String title;

    @CreationTimestamp
    @Column(name = "CREATE_AT", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "UPDATE_AT", nullable = false)
    private LocalDateTime updateAt;
}