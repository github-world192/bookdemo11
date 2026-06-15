package com.bookdemo11.employee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookdemo11.employee.dto.EmployeeForm;
import com.bookdemo11.employee.entity.Employee;
import com.bookdemo11.employee.entity.Gender;
import com.bookdemo11.employee.service.DepartmentService;
import com.bookdemo11.employee.service.EmployeeService;
import com.bookdemo11.employee.service.JobTitleService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/employees")
@PreAuthorize("@adminSecurity.hasPermission('EMPLOYEE_MANAGE')")
public class EmployeeManageController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final JobTitleService jobTitleService;

    public EmployeeManageController(EmployeeService employeeService,
                                  DepartmentService departmentService,
                                  JobTitleService jobTitleService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.jobTitleService = jobTitleService;
    }

    @ModelAttribute("genders")
    public Gender[] genders() {
        return Gender.values();
    }

    @GetMapping
    public String list(Model model) {
        populateFormModel(model, new EmployeeForm(), false);
        model.addAttribute("employees", employeeService.findAll());
        return "admin/employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("員工不存在"));
        EmployeeForm form = new EmployeeForm();
        form.setEmployeeId(employee.getEmployeeId());
        form.setEmployeeName(employee.getEmployeeName());
        form.setEmployeeMail(employee.getEmployeeMail());
        form.setPhone(employee.getPhone());
        form.setAddress(employee.getAddress());
        form.setHireDate(employee.getHireDate());
        form.setGender(employee.getGender());
        form.setStatus(employee.getStatus());
        if (employee.getDepartment() != null) {
            form.setDepartmentId(employee.getDepartment().getDepartmentId());
        }
        if (employee.getJobTitle() != null) {
            form.setJobTitleId(employee.getJobTitle().getJobTitleId());
        }

        populateFormModel(model, form, true);
        model.addAttribute("employees", employeeService.findAll());
        return "admin/employees";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("employeeForm") EmployeeForm form,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormModel(model, form, form.getEmployeeId() != null);
            model.addAttribute("employees", employeeService.findAll());
            return "admin/employees";
        }
        try {
            if (form.getEmployeeId() == null) {
                employeeService.create(form);
                redirectAttributes.addFlashAttribute("successMessage", "員工已新增");
            } else {
                employeeService.update(form.getEmployeeId(), form);
                redirectAttributes.addFlashAttribute("successMessage", "員工已更新");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/employees";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "員工狀態已更新");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "員工已刪除");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/employees";
    }

    private void populateFormModel(Model model, EmployeeForm form, boolean editing) {
        model.addAttribute("employeeForm", form);
        model.addAttribute("editing", editing);
        model.addAttribute("departments", departmentService.findActive());
        model.addAttribute("jobTitles", jobTitleService.findActive());
    }
}