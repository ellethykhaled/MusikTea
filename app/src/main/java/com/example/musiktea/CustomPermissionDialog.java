package com.example.musiktea;

import static com.example.musiktea.MusicList.REQUEST_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

public class CustomPermissionDialog extends Dialog implements android.view.View.OnClickListener {
        public Activity activity;
        public Dialog d;
        public ImageView ok, cancel;

  public CustomPermissionDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.activity = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.customdialogpermission);
            ok = findViewById(R.id.ok);
            cancel = findViewById(R.id.cancel);
            ok.setOnClickListener(this);
            cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ok:
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                    break;
                case R.id.cancel:
                    activity.finish();
                    break;
                default:
                    break;
            }
            dismiss();
        }
}
