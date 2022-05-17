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

import android.R
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.demo.GraphicOverlay
import com.google.mlkit.vision.demo.GraphicOverlay.Graphic
import com.google.mlkit.vision.demo.kotlin.CameraXLivePreviewActivity
import com.google.mlkit.vision.text.Text
import java.util.*
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

  private val rectPaint: Paint = Paint()
  private val textPaint: Paint
  private val labelPaint: Paint

//  private var wordPoint: Array<Point> = emptyArray<Point>()
//  private var wordPoint: RectF = RectF()
//  private var wordText: String = String()
//  private var wordLang: String = String()

//  private val context: Context = CameraXLivePreviewActivity()

//  private val bitmap: Bitmap = CameraXLivePreviewActivity().getBitmap()

  init {

    rectPaint.color = MARKER_COLOR
    rectPaint.style = Paint.Style.STROKE
    rectPaint.strokeWidth = STROKE_WIDTH
    textPaint = Paint()
    textPaint.color = TEXT_COLOR
    textPaint.textSize = TEXT_SIZE
    labelPaint = Paint()
    labelPaint.color = MARKER_COLOR
    labelPaint.style = Paint.Style.FILL
    labelPaint.strokeWidth=100f

    // Redraw the overlay, as this graphic has been added.
    postInvalidate()
  }

  /** Draws the text block annotations for position, size, and raw value on the supplied canvas. */
  @RequiresApi(Build.VERSION_CODES.O)
  override fun draw(canvas: Canvas) {

    for (textBlock in text.textBlocks) { // Renders the text at the bottom of the box.
//      Log.d(TAG, "TextBlock text is: " + textBlock.text)
//      Log.d(TAG, "TextBlock boundingbox is: " + textBlock.boundingBox)
//      Log.d(TAG, "TextBlock cornerpoint is: " + Arrays.toString(textBlock.cornerPoints))
      if (shouldGroupTextInBlocks) {
        drawText(
          getFormattedText(textBlock.text, textBlock.recognizedLanguage),
          RectF(textBlock.boundingBox),
          TEXT_SIZE * textBlock.lines.size + 2 * STROKE_WIDTH,
          canvas
        )
        // 대학생 자리에 계속 바운딩 박스 그리기
//        drawText(
//          getFormattedText(wordText, wordLang),
//          wordPoint,
//          TEXT_SIZE + 2 * STROKE_WIDTH,
//          canvas
//        )
      } else {
        for (line in textBlock.lines) {
//          Log.d(TAG, "Line text is: " + line.text)
//          Log.d(TAG, "Line boundingbox is: " + line.boundingBox)
//          Log.d(TAG, "Line cornerpoint is: " + Arrays.toString(line.cornerPoints))
          // Draws the bounding box around the TextBlock.
          var rect = RectF(line.boundingBox)
//          var rect = line.cornerPoints
//          Log.d(TAG, "rect = " + rect)
          drawText(
              getFormattedText(line.text, line.recognizedLanguage),
              rect,
              TEXT_SIZE + 2 * STROKE_WIDTH,
              canvas
            )
          // 대학생 자리에 계속 바운딩 박스 그리기
//          drawText(
//            getFormattedText(wordText, wordLang),
//            wordPoint,
//            TEXT_SIZE + 2 * STROKE_WIDTH,
//            canvas
//          )
          //
          for (element in line.elements) {
//            Log.d(TAG, "Element text is: " + element.text)
//            Log.d(TAG, "Element boundingbox is: " + element.boundingBox)
//            Log.d(TAG, "Element cornerpoint is: " + Arrays.toString(element.cornerPoints))
//            Log.d(TAG, "Element language is: " + element.recognizedLanguage)
            // 대학생일 때, 정보 갱신
//            if (element.text == "대학생") {
//              wordPoint = RectF(element.boundingBox)
////              wordPoint = element.cornerPoints
//              wordText = element.text
//              wordLang = element.recognizedLanguage
//            }

          }
        }
      }
    }
  }

  private fun getFormattedText(text: String, languageTag: String): String {
    if (showLanguageTag) {
      return String.format(
        TEXT_WITH_LANGUAGE_TAG_FORMAT,
        languageTag,
        text
      )
    }
    return text
  }

  private fun drawText(text: String, rect: RectF, textHeight: Float, canvas: Canvas) {
    // If the image is flipped, the left will be translated to right, and the right to left.
    val x0 = translateX(rect.left)
    val x1 = translateX(rect.right)
    rect.left = min(x0, x1)
    rect.right = max(x0, x1)
    rect.top = translateY(rect.top)
    rect.bottom = translateY(rect.bottom)

//    val x0 = rect!![3].y.toFloat()
//    val x1 = rect!![3].x.toFloat()
//
//    canvas.drawLine(translateX(480-rect!![1].y.toFloat()),translateY(rect!![1].x.toFloat()),translateX(480-rect!![0].y.toFloat()),translateY(rect!![0].x.toFloat()), labelPaint)
//    canvas.drawLine(translateX(480-rect!![2].y.toFloat()),translateY(rect!![2].x.toFloat()),translateX(480-rect!![3].y.toFloat()),translateY(rect!![3].x.toFloat()), labelPaint)
//    canvas.drawText(text, x0, x1 - STROKE_WIDTH*2, textPaint)

    // 텍스트 바운딩 박스 그리기
    canvas.drawRect(rect, labelPaint)
//    canvas.drawBitmap(bitmap, null, rect, null)
    // 텍스트의 배경이 되는 사각형 그리기
//    val textWidth = textPaint.measureText(text)
//    canvas.drawRect(
//      rect.left - STROKE_WIDTH,
//      rect.top - textHeight,
//      rect.left + textWidth + 2 * STROKE_WIDTH,
//      rect.top,
//      labelPaint
//    )
    // Renders the text at the bottom of the box.
//    canvas.drawText(text, rect.left, rect.top - STROKE_WIDTH, textPaint)
  }

  companion object {
    private const val TAG = "TextGraphic"
    private const val TEXT_WITH_LANGUAGE_TAG_FORMAT = "%s:%s"
    private const val TEXT_COLOR = Color.BLACK
    private const val MARKER_COLOR = Color.BLACK
    private const val TEXT_SIZE = 54.0f
    private const val STROKE_WIDTH = 4.0f
  }
}