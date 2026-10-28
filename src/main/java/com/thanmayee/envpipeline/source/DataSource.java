package com.thanmayee.envpipeline.source;

import java.util.List;

import com.thanmayee.envpipeline.entity.Reading;

public interface DataSource {
    List<Reading> fetchReadings();
}
