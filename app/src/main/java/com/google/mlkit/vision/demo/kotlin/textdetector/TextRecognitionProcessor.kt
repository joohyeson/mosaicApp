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

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.demo.GraphicOverlay
import com.google.mlkit.vision.demo.kotlin.VisionProcessorBase
import com.google.mlkit.vision.demo.preference.PreferenceUtils
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface
import okhttp3.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/** Processor for the text detector demo.  */
class TextRecognitionProcessor(private val context: Context, textRecognizerOptions: TextRecognizerOptionsInterface) : VisionProcessorBase<Text>(context) {
  private val textRecognizer: TextRecognizer = TextRecognition.getClient(textRecognizerOptions)
  private val shouldGroupRecognizedTextInBlocks: Boolean = PreferenceUtils.shouldGroupRecognizedTextInBlocks(context)
  private val showLanguageTag: Boolean = PreferenceUtils.showLanguageTag(context)

  private val filename = "test.txt"
  private var frameVar = 1
  private var serverRun = 0

  override fun stop() {
    super.stop()
    textRecognizer.close()
  }

  override fun detectInImage(image: InputImage): Task<Text> {
    return textRecognizer.process(image)
  }

  override fun onSuccess(text: Text, graphicOverlay: GraphicOverlay) {
    Log.d(TAG, "On-device Text detection successful")


    if (serverRun==0) {
      HttpCheckId()
      serverRun = 1
    }

    if(frameVar%10==0)
    {
      // 텍스트 저장하기
      val fileContents = text.text

      try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
            Log.d(TAG, "Write!")
              frameVar = 1
        }
      } catch (e: IOException) {
        Log.d(TAG, "error!")
      }

    }
    else
    {
      frameVar++
      Log.d("Frame TEST", frameVar.toString())
    }

//    logExtrasForTesting(text)
    graphicOverlay.add(
      TextGraphic(graphicOverlay, text, shouldGroupRecognizedTextInBlocks, showLanguageTag))
  }

  fun HttpCheckId(){

    // URL을 만들어 주고
    val url = "https://myser.run-asia-northeast1.goorm.io/post"

    //데이터를 담아 보낼 바디를 만든다
    val requestBody : RequestBody = FormBody.Builder()
      .add("id","지난달 28일 수원에 살고 있는 윤주성 연구원은 코엑스(서울 삼성역)에서 개최되는 DEVIEW 2019 Day1에 참석했다. LaRva팀의 '엄~청 큰 언어 모델 공장 가동기!' 세션을 들으며 언어모델을 학습시킬때 multi-GPU, TPU 모두 써보고 싶다는 생각을 했다.")
      .build()

    // OkHttp Request 를 만들어준다.
    val request = Request.Builder()
      .url(url)
      .post(requestBody)
      .build()

    // 클라이언트 생성
    val client = OkHttpClient()

    Log.d("전송 주소 ","https://myser.run-asia-northeast1.goorm.io/post")

    // 요청 전송
    client.newCall(request).enqueue(object : Callback {

      override fun onResponse(call: Call, response: Response) {
        val body = response.body?.string();


        Log.d("요청", body!!)
      }

      override fun onFailure(call: Call, e: IOException) {
        Log.d("요청","요청 실패 ")
      }

    })

  }

  override fun onFailure(e: Exception) {
    Log.w(TAG, "Text detection failed.$e")
  }

  companion object {
    private const val TAG = "TextRecProcessor"

    private fun logExtrasForTesting(text: Text?) {
      if (text != null) {
        Log.v(
          MANUAL_TESTING_LOG,
          "Detected text has : " + text.textBlocks.size + " blocks"
        )

        for (i in text.textBlocks.indices) {
          val lines = text.textBlocks[i].lines
          Log.v(
            MANUAL_TESTING_LOG,
            String.format("Detected text block %d has %d lines", i, lines.size)
          )
          for (j in lines.indices) {
            val elements =
              lines[j].elements
            Log.v(
              MANUAL_TESTING_LOG,
              String.format("Detected text line %d has %d elements", j, elements.size)
            )
            for (k in elements.indices) {
              val element = elements[k]
              Log.v(
                MANUAL_TESTING_LOG,
                String.format("Detected text element %d says: %s", k, element.text)
              )
              Log.v(
                MANUAL_TESTING_LOG,
                String.format(
                  "Detected text element %d has a bounding box: %s",
                  k, element.boundingBox!!.flattenToString()
                )
              )
              Log.v(
                MANUAL_TESTING_LOG,
                String.format(
                  "Expected corner point size is 4, get %d", element.cornerPoints!!.size
                )
              )
              for (point in element.cornerPoints!!) {
                Log.v(
                  MANUAL_TESTING_LOG,
                  String.format(
                    "Corner point for element %d is located at: x - %d, y = %d",
                    k, point.x, point.y
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}
