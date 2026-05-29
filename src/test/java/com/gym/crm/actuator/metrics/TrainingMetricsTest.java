package com.gym.crm.actuator.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TrainingMetricsTest {

    private MeterRegistry registry;
    private TrainingMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new TrainingMetrics(registry);
    }

    @Test
    void incrementCounter_shouldCreateAndIncrementCounter_whenNewType() {
        metrics.incrementCounter("Yoga");

        Counter counter = registry.find("gym.training.creations")
                .tag("type", "Yoga")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void incrementCounter_shouldIncrementExistingCounter_whenSameType() {
        metrics.incrementCounter("Yoga");
        metrics.incrementCounter("Yoga");

        Counter counter = registry.find("gym.training.creations")
                .tag("type", "Yoga")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void incrementCounter_shouldCreateSeparateCounters_whenDifferentTypes() {
        metrics.incrementCounter("Yoga");
        metrics.incrementCounter("Pilates");

        double countYoga = registry.counter("gym.training.creations", "type", "Yoga").count();
        double countPilates = registry.counter("gym.training.creations", "type", "Pilates").count();

        assertThat(countYoga).isEqualTo(1.0);
        assertThat(countPilates).isEqualTo(1.0);
    }
}
