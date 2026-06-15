package com.bookdemo11.employee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookdemo11.employee.service.DepartmentService;
import com.bookdemo11.employee.service.JobTitleService;

@Controller
@RequestMapping("/admin/org")
@PreAuthorize("@adminSecurity.hasPermission('ROLE_MANAGE')")
public class OrgManageController {

    private final DepartmentService departmentService;
    private final JobTitleService jobTitleService;

    public OrgManageController(DepartmentService departmentService, JobTitleService jobTitleService) {
        this.departmentService = departmentService;
        this.jobTitleService = jobTitleService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("jobTitles", jobTitleService.findAll());
        return "admin/org";
    }

    @PostMapping("/departments")
    public String saveDepartment(@RequestParam(required = false) Integer departmentId,
                                 @RequestParam String departmentName,
                                 @RequestParam(defaultValue = "1") Byte status,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (departmentId == null) {
                departmentService.save(departmentName, status);
                redirectAttributes.addFlashAttribute("successMessage", "部門已新增");
            } else {
                departmentService.update(departmentId, departmentName, status);
                redirectAttributes.addFlashAttribute("successMessage", "部門已更新");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/org";
    }

    @PostMapping("/job-titles")
    public String saveJobTitle(@RequestParam(required = false) Integer jobTitleId,
                               @RequestParam String jobTitleName,
                               @RequestParam(defaultValue = "1") Byte status,
                               RedirectAttributes redirectAttributes) {
        try {
            if (jobTitleId == null) {
                jobTitleService.save(jobTitleName, status);
                redirectAttributes.addFlashAttribute("successMessage", "職稱已新增");
            } else {
                jobTitleService.update(jobTitleId, jobTitleName, status);
                redirectAttributes.addFlashAttribute("successMessage", "職稱已更新");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/org";
    }

    @GetMapping("/departments/{id}/edit")
    public String editDepartment(@PathVariable Integer id, Model model) {
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("jobTitles", jobTitleService.findAll());
        model.addAttribute("editingDepartment", departmentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("部門不存在")));
        return "admin/org";
    }

    @GetMapping("/job-titles/{id}/edit")
    public String editJobTitle(@PathVariable Integer id, Model model) {
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("jobTitles", jobTitleService.findAll());
        model.addAttribute("editingJobTitle", jobTitleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("職稱不存在")));
        return "admin/org";
    }
}