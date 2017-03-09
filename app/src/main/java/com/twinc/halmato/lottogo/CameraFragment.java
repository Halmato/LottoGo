package com.twinc.halmato.lottogo;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.twinc.halmato.lottogo.model.Draw;

import java.io.IOException;

import static android.R.attr.start;
import static android.R.attr.visibility;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Tiaan on 3/8/2017.
 */

public class CameraFragment extends Fragment
{
    private static final String TAG = "CameraFragment";
    private static final int REQUEST_CAMERA_PERMISSION_ID = 1001;
    private static final int VISIBLE = View.VISIBLE;
    private static final int INVISIBLE = View.INVISIBLE;

    //private Context parentContext;

    private Button captureImageButton, retryCaptureButton, acceptCaptureButton;
    private CameraSource cameraSource;
    private TextView resultTextView;
    private SurfaceView cameraSurfaceView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //parentContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.camera_layout,container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeComponents();

        setUpCamera();

    }

    private void setCameraButtonListeners() {
        setCaptureButtonOnClickListener();
        setAcceptCaptureButtonOnClickListener();
        setRetryCaptureButtonOnClickListener();
    }

    private void retryCapture() {

        hideRetryButton();
        hideAcceptButton();
        showCaptureButton();

        startCamera();
    }


    private void initializeComponents()
    {
        captureImageButton = (Button) getView().findViewById(R.id.btn_capture_image);
        retryCaptureButton = (Button) getView().findViewById(R.id.btn_retry_capture);
        acceptCaptureButton = (Button) getView().findViewById(R.id.btn_accept_capture);
        cameraSurfaceView = (SurfaceView) getView().findViewById(R.id.surface_view);
        resultTextView = (TextView) getView().findViewById(R.id.result_text_view);
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

    public void captureImage() {
        
        hideCaptureButton();
        showRetryButton();
        showAcceptButton();

        stopCamera();
        displayCapturedResults();
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

    private void setUpCamera() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "setUpCamera: Detector Dependencies are not yet available");
        } else {

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

                            //createDrawFromString(resultTextView.getText().toString());
                        }
                    });
                }
            }
        });
    }

    private void setCameraSource(TextRecognizer textRecognizer) {
        cameraSource = createCameraSource(textRecognizer);
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

    private CameraSource createCameraSource(TextRecognizer textRecognizer) {
         return new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void startCamera() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            cameraSource.start(cameraSurfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopCamera()
    {
        cameraSource.stop();
    }

    private void displayCapturedResults() {

        Toast.makeText(getContext(), "Displaying captured results!", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_ID: {
                if (requestIsGranted(grantResults[0])) {
                    startCamera();
                }
            }
        }
    }

    private boolean requestIsGranted(int grantResult)
    {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    private void onAcceptCaptureButton() {
        
        if(isMainActivity()) {

            sendDrawToMainActivity();
        }
    }

    private boolean isMainActivity()
    {
        return getActivity() instanceof MainActivity;
    }

    private void sendDrawToMainActivity() {
        Draw draw = new Draw(getCapturedResult());
        ((MainActivity) getActivity()).onReceiveResultsFromCamera(draw);
    }

    private String getCapturedResult() {
        // TODO: 3/9/2017  
        return "1-5-9-17-55-19";
    }

}
