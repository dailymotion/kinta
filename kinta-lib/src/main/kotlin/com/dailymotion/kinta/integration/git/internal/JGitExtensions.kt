package com.dailymotion.kinta.integration.git.internal

import com.dailymotion.kinta.integration.github.internal.GithubOauthClient
import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.PushCommand
import org.eclipse.jgit.transport.FetchResult
import org.eclipse.jgit.transport.PushResult

/**
 * execute the given pushCommand against a HTTPS remote
 * By default, Jgit uses the origin remote to push. It might be configured using a ssh config that Jgit
 * does not understand.
 * This method will create a temporary HTTPS remote to push against
 */
fun PushCommand.callWithCredentials(): MutableIterable<PushResult> {
    setCredentialsProvider(GithubOauthClient.credentials)
    return call()
}

/**
 * execute the given pullCommand against a HTTPS remote
 * By default, Jgit uses the origin remote to pull. It might be configured using a ssh config that Jgit
 * does not understand.
 * This method will create a temporary HTTPS remote to pull against
 */
fun PullCommand.callWithCredentials(): PullResult {
    setCredentialsProvider(GithubOauthClient.credentials)
    return call()
}

/**
 * execute the given fetch against a HTTPS remote
 * By default, Jgit uses the origin remote to fetch. It might be configured using a ssh config that Jgit
 * does not understand.
 * This method will create a temporary HTTPS remote to fetch against
 */
fun FetchCommand.callWithCredentials(): FetchResult {
    setCredentialsProvider(GithubOauthClient.credentials)
    return call()
}
