[kinta-lib](../../index.md) / [com.dailymotion.kinta.integration.slack](../index.md) / [Slack](index.md) / [sendNotification](./send-notification.md)

# sendNotification

`fun sendNotification(channel: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, text: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, webhookUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, username: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = "Release Bot", iconEmoji: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = ":robot_face:"): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Send a slack notification

### Parameters

`webhookUrl` - : the webhook url as in https://api.slack.com/messaging/webhooks
If null, it will default to the SLACK_WEBHOOK_URL environnment variable.

`channel` - : the slack channel to the message to

`text` - : the body of the message