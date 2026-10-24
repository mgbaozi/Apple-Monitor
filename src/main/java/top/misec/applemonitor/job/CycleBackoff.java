package top.misec.applemonitor.job;

/** State is accessed under AppleMonitor's shared monitor lock. */
final class CycleBackoff {
    private final int minCycles;
    private final int stepCycles;
    private final int maxCycles;
    private int nextDelay;
    private int remainingCycles;

    CycleBackoff(int minCycles, int stepCycles, int maxCycles) {
        if (minCycles < 1 || stepCycles < 1 || maxCycles < minCycles) {
            throw new IllegalArgumentException("Backoff requires min >= 1, step >= 1 and max >= min");
        }
        this.minCycles = minCycles;
        this.stepCycles = stepCycles;
        this.maxCycles = maxCycles;
        reset();
    }

    int rateLimited() {
        remainingCycles = nextDelay;
        nextDelay = (int) Math.min((long) nextDelay + stepCycles, maxCycles);
        return remainingCycles;
    }

    boolean skipCycle() {
        if (remainingCycles == 0) {
            return false;
        }
        remainingCycles--;
        return true;
    }

    void reset() {
        nextDelay = minCycles;
        remainingCycles = 0;
    }
}
