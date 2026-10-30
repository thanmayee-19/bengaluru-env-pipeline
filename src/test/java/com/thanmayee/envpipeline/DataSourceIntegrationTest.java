package com.thanmayee.envpipeline;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.model.Area;
import com.thanmayee.envpipeline.source.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataSourceIntegrationTest {

    @Autowired
    private List<DataSource> dataSources;

    @Test
    void testBothLiveDataSources() {

        assertEquals(2, dataSources.size());

        for (DataSource dataSource : dataSources) {

            System.out.println("\nTesting source: " + dataSource.name());

            List<Reading> readings =
                    dataSource.fetch(Area.KORAMANGALA);
            assertFalse(readings.isEmpty(),
                    dataSource.name() + " returned no readings");

            assertNotNull(readings);
            assertFalse(readings.isEmpty(),
                    dataSource.name() + " returned no readings");

            System.out.println("Readings received: " + readings.size());

            Reading first = readings.get(0);

            System.out.println("Area: " + first.getArea());
            System.out.println("Metric: " + first.getMetric());
            System.out.println("Value: " + first.getValue());
            System.out.println("Unit: " + first.getUnit());
            System.out.println("Source: " + first.getSource());
            System.out.println("Timestamp: " + first.getRecordedAt());

            assertEquals("KORAMANGALA", first.getArea());
            assertNotNull(first.getValue());
            assertNotNull(first.getRecordedAt());
        }
    }
}
