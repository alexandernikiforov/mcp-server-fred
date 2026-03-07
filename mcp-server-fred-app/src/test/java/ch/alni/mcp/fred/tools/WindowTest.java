package ch.alni.mcp.fred.tools;

import ch.alni.mcp.fred.service.LookbackPeriod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WindowTest {
    @Test
    public void testWindowInitialization() {
        assertNotNull(LookbackPeriod.LAST_1Y);
    }
}
