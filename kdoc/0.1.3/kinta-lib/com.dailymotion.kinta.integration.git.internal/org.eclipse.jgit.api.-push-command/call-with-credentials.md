[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.git.internal](../index.md) / [org.eclipse.jgit.api.PushCommand](index.md) / [callWithCredentials](./call-with-credentials.md)

# callWithCredentials

`fun PushCommand.callWithCredentials(): `[`MutableIterable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-iterable/index.html)`<PushResult>`

execute the given pushCommand against a HTTPS remote
By default, Jgit uses the origin remote to push. It might be configured using a ssh config that Jgit
does not understand.
This method will create a temporary HTTPS remote to push against

