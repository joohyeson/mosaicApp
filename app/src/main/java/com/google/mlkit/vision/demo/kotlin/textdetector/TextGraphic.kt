/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.kotlin.textdetector

import android.graphics.*
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.demo.GraphicOverlay
import com.google.mlkit.vision.demo.GraphicOverlay.Graphic
import com.google.mlkit.vision.text.Text
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Arrays
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextGraphic
constructor(
  overlay: GraphicOverlay?,
  private val text: Text,
  private val shouldGroupTextInBlocks: Boolean,
  private val showLanguageTag: Boolean
) : Graphic(overlay) {

  private val rectPaint: Paint

    init {
    rectPaint = Paint()
    rectPaint.color = Color.WHITE
    //rectPaint.style = Paint.Style.FILL
    rectPaint.setMaskFilter(BlurMaskFilter(5f, BlurMaskFilter.Blur.INNER))

    postInvalidate()
  }

  /** Draws the text block annotations for position, size, and raw value on the supplied canvas. */
  @RequiresApi(Build.VERSION_CODES.O)
  override fun draw(canvas: Canvas) {

    for (textBlock in text.textBlocks) { // Renders the text at the bottom of the box.

      if (shouldGroupTextInBlocks) {
        drawText(
          textBlock.cornerPoints,
          canvas
        )
      } else {
        for (line in textBlock.lines) {
          for (element in line.elements) {
            if (element.text == "대학생") {
              val rect = element.cornerPoints
              drawText(
                rect,
                canvas
              )
            }
          }
        }
      }
    }
  }

  private fun drawText(rect: Array<Point>?, canvas: Canvas) {

    // 텍스트 바운딩 박스 그리기
    rectPaint.strokeWidth = abs(translateY(rect!![1].x.toFloat())-translateY(rect!![2].x.toFloat())) * 1.25f

    canvas.drawLine(translateX(480-rect!![1].y.toFloat()),translateY(rect!![1].x.toFloat()),
      translateX(480-rect!![0].y.toFloat()),translateY(rect!![0].x.toFloat()), rectPaint)

    canvas.drawLine(translateX(480-rect!![2].y.toFloat()),translateY(rect!![2].x.toFloat()),
      translateX(480-rect!![3].y.toFloat()),translateY(rect!![3].x.toFloat()), rectPaint)
  }
}
