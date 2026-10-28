package com.example.readingservice.source;

import com.example.readingservice.entity.Reading;
import java.util.List;

public interface DataSource {
    List<Reading> fetchReadings();
}
