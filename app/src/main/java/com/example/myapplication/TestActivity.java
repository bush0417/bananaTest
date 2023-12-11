package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;
import org.pytorch.MemoryFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import androidx.appcompat.app.AppCompatActivity;



public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bitmap bitmap = null;
        Module module_ori = null;
        Bitmap rb = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("test.jpg"));
            rb = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            module_ori = Module.load(assetFilePath(this, "model.pt"));
        } catch (IOException e) {
            Log.e("PytorchHelloWorld", "Error reading assets", e);
            finish();
        }

        ImageView imageView = findViewById(R.id.image);
        imageView.setImageBitmap(rb);

        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(rb,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);


        long startTime_ori = System.currentTimeMillis();
        final Tensor outputTensor_ori = module_ori.forward(IValue.from(inputTensor)).toTensor();
        long endTime_ori = System.currentTimeMillis();
        long InferenceTimeOri = endTime_ori - startTime_ori;

        final float[] scores = outputTensor_ori.getDataAsFloatArray();

        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        System.out.println(maxScoreIdx);
        String className = classfied.IMAGENET_CLASSES[maxScoreIdx];
        String intro ="";
        switch (className){
            case "aphids": intro="結果為香蕉1"; break;
            case "cordana": intro="結果為香蕉2"; break;
            case "healthy": intro="結果為香蕉3"; break;
            case "panama": intro="香蕉黃葉病由尖孢鐮刀菌引起，葉片由邊緣至中心黃化，嚴重會導致全株病死或折倒"+"\n"+"以下是部分防治方法"; break;
            case "pestalotiopsis": intro="結果為香蕉5"; break;
            case "sigatoka": intro="結果為香蕉6"; break;
        }
        // showing className on UI
        TextView textView = findViewById(R.id.text);
        String tex = "推測结果：" + className + "\n模型推測时间：" + InferenceTimeOri + "ms";
        textView.setText(tex);

        TextView textView1 = findViewById(R.id.text11);
        textView1.setText(intro);

    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}