//[kinta-lib](../../../index.md)/[com.dailymotion.kinta.integration.slack](../index.md)/[Slack](index.md)/[sendNotification](send-notification.md)



# sendNotification  
[jvm]  
Content  
fun [sendNotification](send-notification.md)(channel: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), text: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), webhookUrl: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, username: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = "Release Bot", iconEmoji: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = ":robot_face:")  
More info  


Send a slack notification



## Parameters  
  
jvm  
  
| | |
|---|---|
| <a name="com.dailymotion.kinta.integration.slack/Slack/sendNotification/#kotlin.String#kotlin.String#kotlin.String?#kotlin.String#kotlin.String/PointingToDeclaration/"></a>webhookUrl| <a name="com.dailymotion.kinta.integration.slack/Slack/sendNotification/#kotlin.String#kotlin.String#kotlin.String?#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>: the webhook url as in https://api.slack.com/messaging/webhooks If null, it will default to the SLACK_WEBHOOK_URL environnment variable.<br><br>|
| <a name="com.dailymotion.kinta.integration.slack/Slack/sendNotification/#kotlin.String#kotlin.String#kotlin.String?#kotlin.String#kotlin.String/PointingToDeclaration/"></a>channel| <a name="com.dailymotion.kinta.integration.slack/Slack/sendNotification/#kotlin.String#kotlin.String#kotlin.String?#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>: the slack channel to the message to<br><br>|
| <a name="com.dailymotion.kinta.integration.slack/Slack/sendNotification/#kotlin.String#kotlin.String#kotlin.String?#kotlin.String#kotlin.String/PointingToDeclaration/"></a>text| <a name="com.dailymotion.kinta.integration.slack/Slack/sendNotification/#kotlin.String#kotlin.String#kotlin.String?#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>: the body of the message<br><br>|
  
  



