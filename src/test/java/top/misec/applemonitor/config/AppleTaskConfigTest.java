package top.misec.applemonitor.config;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppleTaskConfigTest {
    private AppleTaskConfig parseConfig(String extraFields) {
        return JSONObject.parseObject("""
                {
                  "appleTaskConfig": {
                    "country": "CN",
                    "location": "广东 深圳 南山区",
                    "cronExpressions": "*/12 * * * * ?",
                    "deviceCodeList": [{"deviceCode": "MJYD4CH/A", "pushConfigs": []}]
                """ + extraFields + "}}", AppCfg.class).getAppleTaskConfig();
    }

    @Test
    void oldConfigUsesDefaultBackoff() {
        AppleTaskConfig config = parseConfig("");
        assertEquals(12, config.getBackoffMinCycles());
        assertEquals(4, config.getBackoffStepCycles());
        assertEquals(24, config.getBackoffMaxCycles());
        assertTrue(config.valid());
    }

    @Test
    void readsCustomAndPartiallySpecifiedBackoff() {
        AppleTaskConfig config = parseConfig(",\"backoffMinCycles\":3,\"backoffStepCycles\":5,\"backoffMaxCycles\":14");
        assertEquals(3, config.getBackoffMinCycles());
        assertEquals(5, config.getBackoffStepCycles());
        assertEquals(14, config.getBackoffMaxCycles());
        assertTrue(config.valid());
        AppleTaskConfig partial = parseConfig(",\"backoffMaxCycles\":40");
        assertEquals(12, partial.getBackoffMinCycles());
        assertEquals(4, partial.getBackoffStepCycles());
        assertEquals(40, partial.getBackoffMaxCycles());
        assertTrue(partial.valid());
    }

    @Test
    void rejectsInvalidBackoffBeforeStartingMonitoring() {
        for (String fields : new String[]{
                ",\"backoffMinCycles\":0", ",\"backoffMinCycles\":-1",
                ",\"backoffStepCycles\":0", ",\"backoffStepCycles\":-1",
                ",\"backoffMaxCycles\":11"}) {
            assertFalse(parseConfig(fields).valid(), fields);
        }
    }
}
