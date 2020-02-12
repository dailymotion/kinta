[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.git.internal](../index.md) / [org.eclipse.jgit.api.FetchCommand](index.md) / [callWithCredentials](./call-with-credentials.md)

# callWithCredentials

`fun FetchCommand.callWithCredentials(): FetchResult`

execute the given fetch against a HTTPS remote
By default, Jgit uses the origin remote to fetch. It might be configured using a ssh config that Jgit
does not understand.
This method will create a temporary HTTPS remote to fetch against

