[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.git.internal](../index.md) / [org.eclipse.jgit.api.PullCommand](./index.md)

### Extensions for org.eclipse.jgit.api.PullCommand

| Name | Summary |
|---|---|
| [callWithCredentials](call-with-credentials.md) | execute the given pullCommand against a HTTPS remote By default, Jgit uses the origin remote to pull. It might be configured using a ssh config that Jgit does not understand. This method will create a temporary HTTPS remote to pull against`fun PullCommand.callWithCredentials(): PullResult` |
