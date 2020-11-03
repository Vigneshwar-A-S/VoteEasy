package com.highsecured.voteeasy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

        private static final int REQUEST_CAMERA = 0;
        private ZXingScannerView scannerView;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);

            if (checkPermission())
            {
                Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
            }
            else {
                requestPermission();
            }
        }

        private boolean checkPermission()
        {
            return (ContextCompat.checkSelfPermission(ScanActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
        }

        private void requestPermission()
        {
            ActivityCompat.requestPermissions(this, new String[] {CAMERA}, REQUEST_CAMERA);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void OnRequestPermissionsResult(int requestCode, String permission[], int grantResults[])
        {
            switch (requestCode)
            {
                case REQUEST_CAMERA:
                    if (grantResults.length>0)
                    {
                        boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        if (cameraAccepted)
                        {
                            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                            if (shouldShowRequestPermissionRationale(CAMERA)){

                                displayAlertMessage("You need to allow access for both Permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                requestPermissions(new String[] {CAMERA}, REQUEST_CAMERA);
                                            }
                                        });
                                return;
                            }

                        }
                    }

                    break;
            }
        }

        @Override
        protected void onResume() {
            super.onResume();

            if (checkPermission())
            {
                if (scannerView == null)
                {

                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);

                }

                scannerView.setResultHandler(this);

                //front camera
//            scannerView.startCamera(1);

                //rear camera
                scannerView.startCamera();

            }
            else{
                requestPermission();
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            scannerView.stopCamera();
        }

        public void displayAlertMessage(String message, DialogInterface.OnClickListener listener)
        {
            new AlertDialog.Builder(ScanActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", listener)
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }

        @Override
        public void handleResult(Result result) {

            final String scanResult = result.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(scanResult);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scannerView.resumeCameraPreview(ScanActivity.this);
            }
        });
        builder.setNegativeButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(intent);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

//        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

           /* Intent intent = new Intent(MainActivity.this, VoiceDetailsActivity.class);

            final String scanResult = result.getText();

            Bundle bundle = new Bundle();
            bundle.putString("scannedResult" , scanResult);
            intent.putExtras(bundle);

            startActivity(intent);*/


        }

    }
