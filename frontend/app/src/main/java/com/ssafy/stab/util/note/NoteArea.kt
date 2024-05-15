package com.ssafy.stab.util.note

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType

@Composable
fun NoteArea(
    currentPageId: String,
    paths: SnapshotStateList<PathInfo>?,
    modifier: Modifier,
    viewModel: NoteControlViewModel
) = AndroidView(
    modifier = modifier,
    factory = {
        ComposeView(it).apply {
            setContent {
                val canvasModifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                val adjustedOffset = adjustScale(offset , viewModel.scale.floatValue)
                                val coordinate = offsetToCoordinate(adjustedOffset)
                                viewModel.insertNewPathInfo(currentPageId, coordinate)
                                viewModel.updateLatestPath(coordinate)
                                viewModel.addNewPath()
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val adjustedOffset = adjustScale(offset , viewModel.scale.floatValue)
                                val coordinate = offsetToCoordinate(adjustedOffset)
                                if (viewModel.penType != PenType.Lasso) {
                                    viewModel.insertNewPathInfo(currentPageId, coordinate)
                                } else {
                                    // 올가미
                                }
                            },
                            onDrag = { change, _ ->
                                val newPoint = change.position
                                val adjustedOffset = adjustScale(newPoint, viewModel.scale.floatValue)
                                if (viewModel.penType != PenType.Lasso) {
                                    viewModel
                                        .updateLatestPath(
                                            offsetToCoordinate(adjustedOffset)
                                        )
                                } else {
                                    // 올가미
                                }
                            },
                            onDragEnd = {
                                viewModel.getLastPath()
                                viewModel.addNewPath()
                            }
                        )
                    }

                Canvas(
                    modifier = canvasModifier
                ) {
                    with(drawContext.canvas.nativeCanvas) {
                        val checkPoint = saveLayer(null, null)

                        paths?.forEach { pathInfo ->
                            when (pathInfo.penType) {
                                PenType.Pen -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(color = ("FF" + pathInfo.color).toLong(16)),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }

                                PenType.Highlighter -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(color = ("40" + pathInfo.color).toLong(16)),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Square,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }

                                else -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(color = ("00" + pathInfo.color).toLong(16)),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        ),
                                        blendMode = BlendMode.Clear
                                    )
                                }
                            }
                        }
                        if (viewModel.getCurrentPathList(currentPageId).isNotEmpty()) {
                            viewModel.getCurrentPathList(currentPageId)
                                .forEach { userPagePathInfo ->
                                val pathInfo = userPagePathInfo.pathInfo
                                when (pathInfo.penType) {
                                    PenType.Pen -> {
                                        drawPath(
                                            path = createPath(pathInfo.coordinates),
                                            color = Color(color = ("FF" + pathInfo.color).toLong(16)),
                                            style = Stroke(
                                                width = pathInfo.strokeWidth,
                                                cap = StrokeCap.Round,
                                                join = StrokeJoin.Round
                                            )
                                        )
                                    }

                                    PenType.Highlighter -> {
                                        drawPath(
                                            path = createPath(pathInfo.coordinates),
                                            color = Color(color = ("40" + pathInfo.color).toLong(16)),
                                            style = Stroke(
                                                width = pathInfo.strokeWidth,
                                                cap = StrokeCap.Square,
                                                join = StrokeJoin.Round
                                            )
                                        )
                                    }

                                    else -> {
                                        drawPath(
                                            path = createPath(pathInfo.coordinates),
                                            color = Color(color = ("00" + pathInfo.color).toLong(16)),
                                            style = Stroke(
                                                width = pathInfo.strokeWidth,
                                                cap = StrokeCap.Round,
                                                join = StrokeJoin.Round
                                            ),
                                            blendMode = BlendMode.Clear
                                        )
                                    }
                                }
                            }
                        }
                        if (viewModel.newPathList.isNotEmpty()) {
                            viewModel.newPathList.forEach { userPagePathInfo ->
                                if (userPagePathInfo.pageId == currentPageId) {
                                    val pathInfo = userPagePathInfo.pathInfo
                                    when (pathInfo.penType) {
                                        PenType.Pen -> {
                                            drawPath(
                                                path = createPath(pathInfo.coordinates),
                                                color = Color(color = ("FF" + pathInfo.color).toLong(16)),
                                                style = Stroke(
                                                    width = pathInfo.strokeWidth,
                                                    cap = StrokeCap.Round,
                                                    join = StrokeJoin.Round
                                                )
                                            )
                                        }
                                        PenType.Highlighter -> {
                                            drawPath(
                                                path = createPath(pathInfo.coordinates),
                                                color = Color(color = ("40" + pathInfo.color).toLong(16)),
                                                style = Stroke(
                                                    width = pathInfo.strokeWidth,
                                                    cap = StrokeCap.Square,
                                                    join = StrokeJoin.Round
                                                )
                                            )
                                        }
                                        else -> {
                                            drawPath(
                                                path = createPath(pathInfo.coordinates),
                                                color = Color(color = ("00" + pathInfo.color).toLong(16)),
                                                style = Stroke(
                                                    width = pathInfo.strokeWidth,
                                                    cap = StrokeCap.Round,
                                                    join = StrokeJoin.Round
                                                ),
                                                blendMode = BlendMode.Clear
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        restoreToCount(checkPoint)
                    }
                }
            }
        }
    }
)

fun adjustScale(offset: Offset, scale: Float): Offset {
    return Offset(
        x = (offset.x) / scale,
        y = (offset.y) / scale
    )
}
