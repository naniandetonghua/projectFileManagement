package com.projectdelivery.controller;

import com.projectdelivery.dto.ApiResponse;
import com.projectdelivery.dto.DeliverableDTO;
import com.projectdelivery.service.DeliverableService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/deliverable")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliverableController {

    private final DeliverableService deliverableService;

    @GetMapping("/list")
    public ApiResponse<Page<DeliverableDTO>> getDeliverableList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DeliverableDTO> deliverables = deliverableService.findDeliverables(name, projectId, type, status, pageable);
        return ApiResponse.success(deliverables);
    }

    @GetMapping("/{id}")
    public ApiResponse<DeliverableDTO> getDeliverableById(@PathVariable Long id) {
        DeliverableDTO deliverable = deliverableService.findById(id);
        return ApiResponse.success(deliverable);
    }

    @PostMapping("/create")
    public ApiResponse<DeliverableDTO> createDeliverable(@RequestBody DeliverableDTO deliverableDTO) {
        DeliverableDTO createdDeliverable = deliverableService.createDeliverable(deliverableDTO);
        return ApiResponse.success("交付物创建成功", createdDeliverable);
    }

    @PutMapping("/update")
    public ApiResponse<DeliverableDTO> updateDeliverable(@RequestBody DeliverableDTO deliverableDTO) {
        DeliverableDTO updatedDeliverable = deliverableService.updateDeliverable(deliverableDTO);
        return ApiResponse.success("交付物更新成功", updatedDeliverable);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteDeliverable(@PathVariable Long id) {
        deliverableService.deleteDeliverable(id);
        return ApiResponse.success("交付物删除成功", null);
    }

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = deliverableService.uploadFile(file);
            return ApiResponse.success("文件上传成功", filename);
        } catch (Exception e) {
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            Resource resource = deliverableService.downloadFile(id);
            DeliverableDTO deliverable = deliverableService.findById(id);
            
            String filename = URLEncoder.encode(deliverable.getName(), StandardCharsets.UTF_8.toString());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    public ApiResponse<Object> getDeliverableStats() {
        return ApiResponse.success(deliverableService.getDeliverableStats());
    }
} 