package top.misec.applemonitor.job;

/** State is accessed under AppleMonitor's shared monitor lock. */
final class CycleBackoff {
    private int nextDelay = 12;
    private int remainingCycles;

    int rateLimited() {
        remainingCycles = nextDelay;
        nextDelay = Math.min(nextDelay + 4, 24);
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
        nextDelay = 12;
        remainingCycles = 0;
    }
}
