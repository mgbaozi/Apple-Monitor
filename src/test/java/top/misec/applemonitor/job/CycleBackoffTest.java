package top.misec.applemonitor.job;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CycleBackoffTest {
    @Test
    void increasesSkippedCyclesByFourAndCapsAtTwentyFour() {
        CycleBackoff backoff = new CycleBackoff();
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
        CycleBackoff backoff = new CycleBackoff();
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
}
