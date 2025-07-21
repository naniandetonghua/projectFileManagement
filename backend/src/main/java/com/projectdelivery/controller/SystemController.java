package com.projectdelivery.controller;

import com.projectdelivery.dto.ApiResponse;
import com.projectdelivery.entity.System;
import com.projectdelivery.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/list")
    public ApiResponse<List<System>> getSystemList() {
        List<System> systems = systemService.getAllSystems();
        return ApiResponse.success(systems);
    }

    @GetMapping("/{id}")
    public ApiResponse<System> getSystemById(@PathVariable Long id) {
        System system = systemService.getSystemById(id);
        return ApiResponse.success(system);
    }

    @PostMapping("/create")
    public ApiResponse<System> createSystem(@RequestBody System system) {
        System createdSystem = systemService.createSystem(system);
        return ApiResponse.success("系统创建成功", createdSystem);
    }

    @PutMapping("/update")
    public ApiResponse<System> updateSystem(@RequestBody System system) {
        System updatedSystem = systemService.updateSystem(system);
        return ApiResponse.success("系统更新成功", updatedSystem);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteSystem(@PathVariable Long id) {
        systemService.deleteSystem(id);
        return ApiResponse.success("系统删除成功", null);
    }
} 