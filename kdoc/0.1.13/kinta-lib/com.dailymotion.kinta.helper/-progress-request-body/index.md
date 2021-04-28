[kinta-lib](../../index.md) / [com.dailymotion.kinta.helper](../index.md) / [ProgressRequestBody](./index.md)

# ProgressRequestBody

`open class ProgressRequestBody : RequestBody`

### Types

| Name | Summary |
|---|---|
| [CountingSink](-counting-sink/index.md) | `inner class CountingSink : ForwardingSink` |
| [Listener](-listener/index.md) | `interface Listener` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ProgressRequestBody(delegate: RequestBody, listener: Listener)` |

### Properties

| Name | Summary |
|---|---|
| [mListener](m-listener.md) | `var mListener: Listener` |

### Functions

| Name | Summary |
|---|---|
| [contentLength](content-length.md) | `open fun contentLength(): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [contentType](content-type.md) | `open fun contentType(): MediaType` |
| [writeTo](write-to.md) | `open fun writeTo(sink: BufferedSink): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
