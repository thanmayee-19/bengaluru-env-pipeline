package com.thanmayee.envpipeline.source;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.model.Area;

import java.util.List;

public interface DataSource {

    String name();

    List<Reading> fetch(Area area);
}
