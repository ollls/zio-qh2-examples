# ZIO Quartz H2 Examples

This repository contains two example HTTP/2 server implementations using the ZIO Quartz H2 library. Both examples demonstrate how to create a high-performance HTTP/2 server with ZIO, but they use different I/O subsystems.

## Project Structure

The project consists of two main subprojects:

1. **NIO** - Uses standard Java NIO (Non-blocking I/O)
2. **IOURING** - Uses Linux IO-Uring for enhanced I/O performance

## Why Two Different Implementations?

The two implementations showcase different approaches to handling I/O operations:

- **NIO Implementation**: Uses Java's standard NIO API for cross-platform compatibility. This implementation works on any platform that supports Java.

- **IOURING Implementation**: Uses Linux's IO-Uring interface, which provides higher performance I/O operations through a more efficient kernel interface. This implementation only works on Linux systems with IO-Uring support (Linux kernel 5.1+).

## HTTP Route Function

Both implementations use the same HTTP route function (`HttpRouteIO`) which defines how HTTP requests are handled. The route function is a pattern-matching function that maps HTTP requests to responses.

Key features demonstrated in the route function:

- Path matching with static and variable segments
- Query parameter extraction
- Custom headers and cookies
- File uploads and downloads
- Streaming responses
- Environment access with ZIO

## Running the NIO Example

To run the standard NIO-based HTTP/2 server:

```bash
sbt "nio/run"
```

This will start an HTTP/2 server on localhost port 8443 using standard Java NIO.

## Running the IOURING Example

To run the IO-Uring-based HTTP/2 server (Linux only):

```bash
sbt "iouring/run"
```

Optional command-line arguments for logging level:

```bash
sbt "iouring/run --debug"   # Debug level logging
sbt "iouring/run --error"   # Error level logging
sbt "iouring/run --trace"   # Trace level logging
sbt "iouring/run --off"     # Turn off logging
```

## SSL Configuration

Both servers use SSL and require a keystore file named `keystore.jks` with password "password" in the project root directory.

## Changing Debug Level

To change the default debug level, modify the logback configuration file at:
`src/main/resources/logback-test.xml`
