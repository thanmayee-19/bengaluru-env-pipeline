package com.thanmayee.envpipeline.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanmayee.envpipeline.model.Area;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    @GetMapping
    public List<Area> getAreas() {
        return Arrays.asList(Area.values());
    }
}
