package com.dailymotion.kinta.integration.jira.internal

import kotlinx.serialization.Serializable

@Serializable
data class Issue(val id: String, val fields: IssueFields) {

    @Serializable
    data class IssueFields(val components: List<IssueComponent>, val summary: String, val status : IssueStatus){

        @Serializable
        data class IssueComponent(val id : String)

        @Serializable
        data class IssueStatus(val name : String)
    }
}

@Serializable
data class Status(val id: String, val name: String)
@Serializable
data class Transition(val id: String, val to: Status? = null)
@Serializable
data class TransitionBody(val transition: Transition)
@Serializable
data class TransitionResult(val transitions: List<Transition>)
@Serializable
data class AssignBody(val name: String)
@Serializable
data class CommentBody(val body: String)