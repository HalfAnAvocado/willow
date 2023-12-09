package com.marvinelsen.willow.ui.alerts

import javafx.scene.control.Alert

@DslMarker
annotation class AlertDsl

@AlertDsl
fun alert(type: Alert.AlertType, init: Alert.() -> Unit): Alert {
    val alert = Alert(type)
    alert.init()
    return alert
}