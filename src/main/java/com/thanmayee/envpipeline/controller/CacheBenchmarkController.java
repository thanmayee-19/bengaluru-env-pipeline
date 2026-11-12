package com.thanmayee.envpipeline.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thanmayee.envpipeline.service.CacheBenchmarkService;

@RestController
@RequestMapping("/api/cache")
public class CacheBenchmarkController {

    private final CacheBenchmarkService cacheBenchmarkService;

    public CacheBenchmarkController(
            CacheBenchmarkService cacheBenchmarkService) {

        this.cacheBenchmarkService = cacheBenchmarkService;
    }

    @GetMapping("/benchmark")
    public Map<String, Object> benchmark(
            @RequestParam(defaultValue = "KORAMANGALA")
            String area,
            @RequestParam(defaultValue = "1000")
            int iterations) {

        return cacheBenchmarkService.benchmark(
                area, iterations);
    }
}
