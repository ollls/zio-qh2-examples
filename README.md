# ZIO Quartz H2 Examples

This repository contains example HTTP/2 server implementations using the ZIO Quartz H2 library. Both examples demonstrate how to create a high-performance HTTP/2 server with ZIO.

This implementation leverages Linux's IO-Uring interface for significantly improved I/O performance by reducing context switches and copy operations. It requires:
- Linux kernel 5.1 or newer for basic IO-Uring support
- Linux kernel 5.5+ recommended for better performance features

You can check your kernel version with `uname -r`.

### 3. Running the Root Project Example

To run the example in the root project:

```bash
sbt run
```

This runs a minimal HTTP/2 server that demonstrates a simple route function with just a GET / endpoint that returns an empty OK response. It's ideal for understanding the basic structure of a ZIO Quartz H2 server without additional complexity.

## Switching Between NIO and IO-Uring

By default, the server runs using Java NIO. To enable the high-performance Linux IO-Uring implementation:

1. Open `src/main/scala/Run.scala`
2. Comment out the NIO line:
```scala
// exitCode <- new QuartzH2Server("localhost", 8443, 16000, ctx).startIO(R, filter, sync = false)
```
3. Uncomment the IO-Uring line:
```scala
exitCode <- new QuartzH2Server("localhost", 8443, 16000, ctx).startIO_linuxOnly(1, R, filter)
```

Note that IO-Uring is only available on Linux systems with kernel version 5.1 or newer, with 5.5+ recommended for optimal performance.

## Key Concepts

### WebFilter with Either.cond

All examples demonstrate the use of a powerful WebFilter pattern using Scala's `Either.cond` mechanism:

```scala
val filter: WebFilter[Any] = (r: Request) =>
  ZIO.succeed(
    Either.cond(condition, r, Response.Error(StatusCode.Forbidden))
  )
```

This pattern allows for elegant request filtering where:
- If the condition evaluates to `true`, the request passes through to your routes (`Right(r)`)  
- If the condition evaluates to `false`, the request is rejected with the specified error response (`Left(response)`)

This functional approach enables clean implementation of access control, rate limiting, authentication, or any other request filtering logic.

## SSL Configuration

All examples use SSL and require a keystore file named `keystore.jks` with password "password" in the appropriate directory.
