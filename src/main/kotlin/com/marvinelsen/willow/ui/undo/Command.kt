package com.marvinelsen.willow.ui.undo

interface Command {
    fun execute()
    fun undo()
    fun redo() = execute()
}