package com.marvinelsen.willow.ui.undo

import java.util.Stack
import javafx.beans.property.SimpleBooleanProperty

object UndoManager {
    private val undoStack = Stack<Command>()
    private val redoStack = Stack<Command>()

    val canUndoProperty = SimpleBooleanProperty(false)
    val canRedoProperty = SimpleBooleanProperty(false)

    fun execute(command: Command) {
        redoStack.clear()

        undoStack.push(command).execute()

        canUndoProperty.value = true
        canRedoProperty.value = false
    }

    fun undo() {
        if (undoStack.isEmpty()) return

        redoStack.push(undoStack.pop()).undo()

        canUndoProperty.value = !undoStack.isEmpty()
        canRedoProperty.value = true
    }

    fun redo() {
        if (redoStack.isEmpty()) return

        undoStack.push(redoStack.pop()).redo()

        canUndoProperty.value = true
        canRedoProperty.value = !redoStack.isEmpty()
    }
}