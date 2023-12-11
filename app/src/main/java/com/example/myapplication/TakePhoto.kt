package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.pytorch.IValue
import org.pytorch.MemoryFormat
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TakePhoto : AppCompatActivity() {
    private lateinit var imageView: ImageView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)
        imageView = findViewById(R.id.image22)

        val compressedBitmap: ByteArray? = intent.getByteArrayExtra("image")
        var bitmap = compressedBitmap?.let {
            BitmapFactory.decodeByteArray(compressedBitmap,0,
                it.size)
        }
        imageView.setImageBitmap(bitmap)
        var module_ori: Module? = null
        try {
            module_ori = Module.load(assetFilePath(this, "model.pt"))
        } catch (e: IOException) {
            Log.e("PytorchHelloWorld", "Error reading assets", e)
            finish()
        }

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB,
            MemoryFormat.CHANNELS_LAST
        )


        val startTime_ori = System.currentTimeMillis()
        val outputTensor_ori = module_ori!!.forward(IValue.from(inputTensor)).toTensor()
        val endTime_ori = System.currentTimeMillis()
        val InferenceTimeOri = endTime_ori - startTime_ori

        val scores = outputTensor_ori.dataAsFloatArray

        // searching for the index with maximum score

        // searching for the index with maximum score
        var maxScore = -Float.MAX_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }
        println(maxScoreIdx)
        val className = classfied.IMAGENET_CLASSES[maxScoreIdx]
        var intro = ""
        when (className) {
            "aphids" -> intro = "結果為香蕉1"
            "cordana" -> intro = "結果為香蕉2"
            "healthy" -> intro = "結果為香蕉3"
            "panama" -> intro = """
     香蕉黃葉病由尖孢鐮刀菌引起，葉片由邊緣至中心黃化，嚴重會導致全株病死或折倒
     以下是部分防治方法
     """.trimIndent()
            "pestalotiopsis" -> intro = "結果為香蕉5"
            "sigatoka" -> intro = "結果為香蕉6"
        }
        // showing className on UI
        // showing className on UI
        val textView = findViewById<TextView>(R.id.text)
        val tex = """
            推測结果：$className
            模型推測时间：${InferenceTimeOri}ms
            """.trimIndent()
        textView.text = tex

        val textView1 = findViewById<TextView>(R.id.text11)
        textView1.text = intro

    }

    @Throws(IOException::class)
    fun assetFilePath(context: Context, assetName: String?): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName!!).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    }
}