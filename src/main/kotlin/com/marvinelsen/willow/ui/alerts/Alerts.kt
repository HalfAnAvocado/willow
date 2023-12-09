package com.marvinelsen.willow.ui.alerts

import javafx.scene.control.Alert

val addedUserSentenceAlert = Alert(Alert.AlertType.INFORMATION).apply {
    title = "Successfully added sentence"
    headerText = "Successfully added new example sentence"
    contentText = ""
    isResizable = false
}

val addedUserEntryAlert = Alert(Alert.AlertType.INFORMATION).apply {
    title = "Successfully added entry"
    headerText = "Successfully added new entry"
    contentText = ""
    isResizable = false
}

@AlertDsl
fun alert(type: Alert.AlertType, init: Alert.() -> Unit): Alert {
    val alert = Alert(type)
    alert.init()
    return alert
}

@DslMarker
annotation class AlertDsl