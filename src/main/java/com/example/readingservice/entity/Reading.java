package com.example.readingservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "readings")

public class Reading {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String source;

	    @Column(nullable = false)
	    private Double value;

	    private String unit;

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

	    public Instant getRecordedAt() {
	        return recordedAt;
	    }

	    public void setRecordedAt(Instant recordedAt) {
	        this.recordedAt = recordedAt;
	    }
	}
