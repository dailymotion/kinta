[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.zip](../index.md) / [ZipIntegration](index.md) / [zip](./zip.md)

# zip

`fun zip(input: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)`, output: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)`, baseDir: `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)`? = null): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

### Parameters

`input` - the directory to zip

`output` - the produced zip file

`baseDir` - the base used to create the hierarchy inside the zip. This is usually input.parentFile to
have only one root entry and avoid clutter when unzipping.