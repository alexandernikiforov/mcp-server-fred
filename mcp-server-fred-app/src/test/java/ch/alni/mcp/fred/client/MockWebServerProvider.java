package ch.alni.mcp.fred.client;

import mockwebserver3.MockWebServer;

final class MockWebServerProvider {

    static final MockWebServer WEB_SERVER = new MockWebServer();

    private MockWebServerProvider() {
    }
}
