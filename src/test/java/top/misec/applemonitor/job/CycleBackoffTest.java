package top.misec.applemonitor.job;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CycleBackoffTest {
    @Test
    void increasesSkippedCyclesByFourAndCapsAtTwentyFour() {
        CycleBackoff backoff = new CycleBackoff(12, 4, 24);
        assertFalse(backoff.skipCycle());
        for (int delay : new int[]{12, 16, 20, 24, 24, 24}) {
            assertEquals(delay, backoff.rateLimited());
            for (int cycle = 0; cycle < delay; cycle++) {
                assertTrue(backoff.skipCycle());
            }
            assertFalse(backoff.skipCycle());
        }
    }

    @Test
    void successfulResponseResetsEscalationAndPendingDelay() {
        CycleBackoff backoff = new CycleBackoff(12, 4, 24);
        backoff.rateLimited();
        backoff.rateLimited();
        backoff.rateLimited();
        backoff.rateLimited();
        backoff.rateLimited();
        backoff.reset();
        assertFalse(backoff.skipCycle());
        assertEquals(12, backoff.rateLimited());
        for (int cycle = 0; cycle < 12; cycle++) {
            assertTrue(backoff.skipCycle());
        }
        assertFalse(backoff.skipCycle());
    }
    @Test
    void usesCustomMinimumStepAndMaximum() {
        CycleBackoff backoff = new CycleBackoff(3, 5, 14);
        for (int delay : new int[]{3, 8, 13, 14, 14}) {
            assertEquals(delay, backoff.rateLimited());
            for (int cycle = 0; cycle < delay; cycle++) {
                assertTrue(backoff.skipCycle());
            }
            assertFalse(backoff.skipCycle());
        }
        backoff.reset();
        assertEquals(3, backoff.rateLimited());
    }

    @Test
    void supportsFixedDelayAndAvoidsIntegerOverflow() {
        CycleBackoff fixed = new CycleBackoff(5, 3, 5);
        assertEquals(5, fixed.rateLimited());
        assertEquals(5, fixed.rateLimited());
        CycleBackoff large = new CycleBackoff(12, Integer.MAX_VALUE, 24);
        assertEquals(12, large.rateLimited());
        assertEquals(24, large.rateLimited());
    }

    @Test
    void rejectsInvalidSettings() {
        assertThrows(IllegalArgumentException.class, () -> new CycleBackoff(0, 4, 24));
        assertThrows(IllegalArgumentException.class, () -> new CycleBackoff(12, 0, 24));
        assertThrows(IllegalArgumentException.class, () -> new CycleBackoff(12, -1, 24));
        assertThrows(IllegalArgumentException.class, () -> new CycleBackoff(12, 4, 11));
    }

}
