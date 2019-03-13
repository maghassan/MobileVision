package com.maghassan.mobilevision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView    resultText;
    private ImageView   image;
    private Button  snap,   detect;
    private static final int    REQUEST_IMAGE_CAPTURE   =   1;
    private Bitmap  imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultText  =   findViewById(R.id.result);
        image   =   findViewById(R.id.image);
        snap    =   findViewById(R.id.snap);
        detect  =   findViewById(R.id.detect);

        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectText();
            }
        });
    }

    private void detectText() {
        FirebaseVisionImage image   =   FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer    recognizer  =   FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        processText(firebaseVisionText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void processText(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock>  blocks  =   firebaseVisionText.getTextBlocks();
        if (blocks.size()   ==  0)  {
            Toast.makeText(MainActivity.this,   "No Text :(",   Toast.LENGTH_LONG).show();
            return;
        }

        for (FirebaseVisionText.TextBlock   block   :   firebaseVisionText.getTextBlocks()) {
            String  text    =   block.getText();
            resultText.setText(text);
        }
    }

    private void dispatchTakePictureIntent  ()  {
        Intent  takePictureIntent   =   new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager())  !=  null) {
            startActivityForResult(takePictureIntent,   REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode ==  REQUEST_IMAGE_CAPTURE   &&  resultCode  ==  RESULT_OK)  {
            Bundle  extras  =   data.getExtras();
            imageBitmap =   (Bitmap)    extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
    }
}
