# FRED MCP Server

An [MCP (Model Context Protocol)](https://modelcontextprotocol.io/) server that provides access to economic data from the [Federal Reserve Economic Data (FRED)](https://fred.stlouisfed.org/) website.

## Overview

This server allows AI agents and other MCP clients to query economic series, releases, and other financial data directly from FRED.

## Features

- **FRED Data Access**: Integration with the FRED API to fetch economic time-series data.
- **Spring Boot Powered**: Built using Spring Boot for robust performance and easy configuration.
- **MCP Compatible**: Implements the Model Context Protocol for seamless integration with AI tools like Claude.

## Prerequisites

- **Java 25**: The project is configured to use Java 25.
- **FRED API Key**: You will need an API key from [FRED](https://fred.stlouisfed.org/docs/api/api_key.html).

## Getting Started

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/mcp-server-fred.git
   cd mcp-server-fred
   ```

2. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

### Configuration

The server requires a FRED API key to function. You can provide it via environment variables or Spring Boot configuration properties.

Example `application.properties`:
```properties
fred.api.key=your_api_key_here
```

### Running the Server

Run the application using the Gradle wrapper:
```bash
./gradlew :mcp-server-fred-app:bootRun
```

## Project Structure

- `mcp-server-fred-app`: The main Spring Boot application.
- `mcp-server-fred-platform`: Dependency management (BOM).
- `mcp-server-fred-conventions`: Shared Gradle build logic.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
