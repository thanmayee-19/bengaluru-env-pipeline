package com.thanmayee.envpipeline.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "readings",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_readings_area_metric_time",
        columnNames = {"area", "metric", "recorded_at"}
    )
)
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private Double value;

    private String unit;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private String metric;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    public Reading() {
    }

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }
}
