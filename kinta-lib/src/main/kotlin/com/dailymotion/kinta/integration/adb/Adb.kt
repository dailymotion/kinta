package com.dailymotion.kinta.integration.adb

import com.dailymotion.kinta.integration.commandline.CommandLine

object Adb {

    fun pullFileFromDevice(src: String, dst: String) {
        val command = "adb pull $src $dst"
        CommandLine.executeOrFail(command = command)
    }

    fun moveFile(src: String, dst: String) {
        val command = "adb shell mv $src $dst"
        CommandLine.executeOrFail(command = command)
    }

    fun instrument(arguments: String) {
        val command = "adb shell am instrument $arguments"
        CommandLine.executeOrFail(command = command)
    }

    fun install(arguments: String) {
        val command = "adb install $arguments"
        CommandLine.executeOrFail(command = command)
    }

    fun enableDemoMode() {
        val command = "adb shell settings put global sysui_demo_allowed 1"
        CommandLine.executeOrFail(command = command)
    }

    fun disableDemoMode() {
        val command = "adb shell am broadcast -a com.android.systemui.demo -e command exit"
        CommandLine.executeOrFail(command = command)
    }

    fun setDemoTime(hhmm: String) {
        val command = "adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm $hhmm"
        CommandLine.executeOrFail(command = command)
    }

    fun setDemoHideNotification() {
        val command = "adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false"
        CommandLine.executeOrFail(command = command)
    }

    fun setDemoBatteryLevel(batteryLevel: Int) {
        val command = "adb shell am broadcast -a com.android.systemui.demo -e command battery -e plugged false -e level $batteryLevel"
        CommandLine.executeOrFail(command = command)
    }
}