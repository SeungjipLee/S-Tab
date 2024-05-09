package com.ssafy.stab.util.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ssafy.stab.data.note.Coordinate
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.data.note.response.PageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NoteController internal constructor(val trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }) {

    private val undoPageList = mutableStateListOf<Int>()
    private val redoPageList = mutableStateListOf<Int>()
    private val redoPathList = mutableStateListOf<PathInfo>()

    private val historyTracking = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val historyTracker = historyTracking.asSharedFlow()

    fun trackHistory(
        scope: CoroutineScope,
        trackHistory: (undoCount: Int, redoCount: Int) -> Unit
    ) {
        historyTracker
            .onEach { trackHistory(undoPageList.size, redoPageList.size) }
            .launchIn(scope)
    }

    var penType by mutableStateOf(PenType.Pen)
        private set

    fun changePenType(value: PenType) {
        penType = value
    }

    var strokeWidth by mutableFloatStateOf(10f)
        private set

    fun changeStrokeWidth(value: Float) {
        strokeWidth = value
    }

    var color by mutableStateOf("000000")
        private set

    fun changeColor(value: String) {
        color = value
    }

    fun insertNewPathInfo(index: Int, newCoordinate: Coordinate, paths: MutableList<PathInfo>) {
        val pathInfo = PathInfo(
            penType = penType,
            coordinates = mutableStateListOf(newCoordinate),
            strokeWidth = strokeWidth,
            color = color
        )

        paths.add(pathInfo)
        undoPageList.add(index)

        redoPageList.clear()
        redoPathList.clear()

        historyTracking.tryEmit("insert path")
    }

    fun updateLatestPath(newCoordinate: Coordinate, paths: MutableList<PathInfo>) {
        val index = paths.lastIndex
        paths[index].coordinates.add(newCoordinate)
    }

    fun undo(pageList: MutableList<PageData>) {
        if (undoPageList.isNotEmpty() && pageList.isNotEmpty()) {
            val page = undoPageList.last()
            val paths = pageList[page].page.paths
            val last = paths.last()

            // redo 경로 정보 저장
            redoPageList.add(page)
            redoPathList.add(last)

            // 현재 경로에서 삭제
            paths.remove(last)
            undoPageList.remove(page)

            trackHistory(undoPageList.size, redoPageList.size)
            historyTracking.tryEmit("undo")
        }
    }

    fun redo(pageList: MutableList<PageData>) {
        if (redoPageList.isNotEmpty() && redoPathList.isNotEmpty()) {
            val page = redoPageList.last()
            val last = redoPathList.last()
            val paths = pageList[page].page.paths

            // 경로 복원
            paths.add(last)

            // undo 경로 정보 저장
            undoPageList.add(page)

            redoPathList.remove(last)
            redoPageList.remove(page)

            trackHistory(undoPageList.size, redoPageList.size)
            historyTracking.tryEmit("redo")
        }
    }

    fun reset() {
        undoPageList.clear()
        redoPageList.clear()
        redoPathList.clear()
        historyTracking.tryEmit("reset")
    }

}

@Composable
fun rememberNoteController(): NoteController {
    return remember { NoteController() }
}