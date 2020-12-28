[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.git.internal](../index.md) / [org.eclipse.jgit.api.PullCommand](index.md) / [callWithCredentials](./call-with-credentials.md)

# callWithCredentials

`fun PullCommand.callWithCredentials(): PullResult`

execute the given pullCommand against a HTTPS remote
By default, Jgit uses the origin remote to pull. It might be configured using a ssh config that Jgit
does not understand.
This method will create a temporary HTTPS remote to pull against

