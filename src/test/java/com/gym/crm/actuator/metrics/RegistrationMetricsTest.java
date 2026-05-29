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
class RegistrationMetricsTest {

    private MeterRegistry registry;
    private RegistrationMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new RegistrationMetrics(registry);
    }

    @Test
    void incrementTraineeCount_shouldIncrementSuccessCounter_whenSuccess() {
        metrics.incrementTraineeCount(true);

        Counter counter = registry.find("gym.user.registrations")
                .tag("type", "trainee")
                .tag("status", "success")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void incrementTraineeCount_shouldIncrementFailureCounter_whenFailure() {
        metrics.incrementTraineeCount(false);

        Counter counter = registry.find("gym.user.registrations")
                .tag("type", "trainee")
                .tag("status", "failure")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void incrementTrainerCount_shouldIncrementSuccessCounter_whenSuccess() {
        metrics.incrementTrainerCount(true);

        Counter counter = registry.find("gym.user.registrations")
                .tag("type", "trainer")
                .tag("status", "success")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void incrementTrainerCount_shouldIncrementFailureCounter_whenFailure() {
        metrics.incrementTrainerCount(false);

        Counter counter = registry.find("gym.user.registrations")
                .tag("type", "trainer")
                .tag("status", "failure")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }
}
