package com.twinc.halmato.lottogo;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.twinc.halmato.lottogo.model.Draw;

import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.src;
import static android.R.id.list;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Tiaan on 3/8/2017.
 */

public class CameraFragment extends Fragment
{
    private static final String TAG = "CameraFragment";
    private static final int REQUEST_CAMERA_PERMISSION_ID = 1001;
    private static final int BALLS_DRAWN = 6;

    private Button captureImageButton, retryCaptureButton, acceptCaptureButton;
    private CameraSource cameraSource;
    private TextView resultTextView;
    private SurfaceView cameraSurfaceView;

    private Button[] btnResultPreviews = new Button[BALLS_DRAWN];
    private LinearLayout llResultPreview;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_camera,container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeComponents();

        setResultDisplayButtonsOnClickListeners();

        setUpCamera();
    }



    private void setResultDisplayButtonsOnClickListeners() {

        View.OnClickListener listener = getResultDisplayButtonOnClickListener();

        for (Button btn:btnResultPreviews) {

            btn.setOnClickListener(listener);
        }
    }

    @NonNull
    private View.OnClickListener getResultDisplayButtonOnClickListener() {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Button btn = (Button)view;

                displayNumberPicker(btn.getText().toString());
            }
        };
    }

    private void displayNumberPicker(String text) {

        final Dialog d = new Dialog(getActivity());
        //d.setTitle("NumberPicker");
        //d.setContentView(R.layout.dialog);

        // Powerball / Powerball Plus :  (5) from 45, (1) from 20
        // Lotto / Lotto Plus : (6) from 49

        d.show();

    }

    private void initializeComponents() {

        captureImageButton = (Button) getView().findViewById(R.id.btn_capture_image);
        retryCaptureButton = (Button) getView().findViewById(R.id.btn_retry_capture);
        acceptCaptureButton = (Button) getView().findViewById(R.id.btn_accept_capture);
        cameraSurfaceView = (SurfaceView) getView().findViewById(R.id.surface_view);
        resultTextView = (TextView) getView().findViewById(R.id.result_text_view);

        llResultPreview = (LinearLayout) getView().findViewById(R.id.ll_result_preview);

        btnResultPreviews[0] = (Button) getView().findViewById(R.id.btn_result_preview_1);
        btnResultPreviews[1] = (Button) getView().findViewById(R.id.btn_result_preview_2);
        btnResultPreviews[2] = (Button) getView().findViewById(R.id.btn_result_preview_3);
        btnResultPreviews[3] = (Button) getView().findViewById(R.id.btn_result_preview_4);
        btnResultPreviews[4] = (Button) getView().findViewById(R.id.btn_result_preview_5);
        btnResultPreviews[5] = (Button) getView().findViewById(R.id.btn_result_preview_6);
    }

    private void setUpCamera() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "setUpCamera: Detector Dependencies are not yet available");
        } else {
            Toast.makeText(getApplicationContext(), "Operational ", Toast.LENGTH_SHORT).show();
            setCameraSource(textRecognizer);

            setCameraSurfaceViewCallback();

            setTextRecognizerProcessor(textRecognizer);

            setCameraButtonListeners();
        }
    }
    private void setTextRecognizerProcessor(TextRecognizer textRecognizer) {

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>()
        {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections)
            {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if(items.size() != 0) {
                    resultTextView.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }

                            resultTextView.setText(stringBuilder.toString());
                        }
                    });
                }
            }
        });
    }

    public void captureImage() {

        hideCaptureButton();
        showRetryButton();
        showAcceptButton();

        stopCamera();
        displayCapturedResults();
    }
    private void retryCapture() {

        hideRetryButton();
        hideAcceptButton();
        showCaptureButton();

        startCamera();
    }

    private void displayCapturedResults() {

        String results = getCapturedResultString();

        String[] listOfNumbers = parseResults(results);

        assignNumberSelectionsToBallsDisplays(listOfNumbers);
    }

    private void hideResultPreviewButtons() {

        llResultPreview.setVisibility(View.INVISIBLE);
    }

    private void showResultPreviewButtons() {

        llResultPreview.setVisibility(View.VISIBLE);
    }

    private void assignNumberSelectionsToBallsDisplays(String[] numbers) {

        for (int i = 0; i < numbers.length; i++) {

            btnResultPreviews[i].setText(numbers[i]);
        }
    }

    private String[] parseResults(String results) {

        ArrayList<String> resultsList = new ArrayList<>(BALLS_DRAWN);

        for(int i = 0; i < results.length(); i += 2) {
            resultsList.add(results.substring(i, (i+2 > results.length()) ? results.length() : i+2));
        }

        return resultsList.toArray(new String[] {});
    }

    private void startCamera() {

       if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

           requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION_ID);
            return;
        }

        try {
            hideResultPreviewButtons();
            cameraSource.start(cameraSurfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopCamera()
    {
        showResultPreviewButtons();
        cameraSource.stop();
    }

    private void onAcceptCaptureButton() {

        if(isMainActivity()) {

            sendDrawToMainActivity();
        }
    }

    private void sendDrawToMainActivity() {
        Draw draw = new Draw(getCapturedResultString());
        ((MainActivity) getActivity()).onReceiveResultsFromCamera(draw);
    }

    private void setCameraButtonListeners() {
        setCaptureButtonOnClickListener();
        setAcceptCaptureButtonOnClickListener();
        setRetryCaptureButtonOnClickListener();
    }
    private void setRetryCaptureButtonOnClickListener() {

        retryCaptureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                retryCapture();
            }

        });
    }
    private void setCaptureButtonOnClickListener() {

        captureImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                captureImage();
            }
        });
    }
    private void setAcceptCaptureButtonOnClickListener() {

        acceptCaptureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onAcceptCaptureButton();
            }
        });

    }

    private void showAcceptButton() {
        acceptCaptureButton.setVisibility(View.VISIBLE);
    }
    private void hideAcceptButton()
    {
        acceptCaptureButton.setVisibility(View.INVISIBLE);
    }
    private void showRetryButton()
    {
        retryCaptureButton.setVisibility(View.VISIBLE);
    }
    private void hideRetryButton()
    {
        retryCaptureButton.setVisibility(View.INVISIBLE);
    }
    private void showCaptureButton() {
        captureImageButton.setVisibility(View.VISIBLE);
    }
    private void hideCaptureButton()
    {
        captureImageButton.setVisibility(View.INVISIBLE);
    }

    private void setCameraSource(TextRecognizer textRecognizer) {
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .build();

        Toast.makeText(getApplicationContext(), "Built ", Toast.LENGTH_SHORT).show();
    }
    private void setCameraSurfaceViewCallback() {
        cameraSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder)
            {
                startCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
            {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder)
            {
                stopCamera();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_ID: {
                if (requestIsGranted(grantResults[0])) {
                    startCamera();
                }
            }
        }
    }
    private boolean requestIsGranted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }
    private boolean isMainActivity()
    {
        return getActivity() instanceof MainActivity;
    }

    private String getCapturedResultString() {
        // TODO: 3/9/2017
        return "010509111564";
    }
}