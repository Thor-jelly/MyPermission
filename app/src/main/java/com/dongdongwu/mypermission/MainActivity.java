package com.dongdongwu.mypermission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "123===";
    /**
     * 请求权限码
     */
    private final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPermission.with(MainActivity.this)
                        .setRequestCode(REQUEST_PERMISSION_CODE)
                        .setRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS)
                        .requestPermission();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ----------------");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ------------------");
    }

    @PermissionSuccess(requestCode = REQUEST_PERMISSION_CODE)
    private void callPhoneSuccess() {
        //打电话方法
        Log.d(TAG, "callPhone: 打电话1PermissionSuccess");
    }

    @PermissionFailure(requestCode = REQUEST_PERMISSION_CODE)
    private void callPhoneFailure() {
        //打电话方法
        Log.d(TAG, "callPhone: 打电话2PermissionFailure");
    }

    //反射会返回don't ask Again 的权限
    @PermissionNotAskAgain(requestCode = REQUEST_PERMISSION_CODE)
    private void callPhoneNotAskAgin(List<String> notAskAgainList) {
        //打电话方法
        Log.d(TAG, "callPhone: 打电话3PermissionNotAskAgain");
        if (notAskAgainList != null) {
            for (String s : notAskAgainList) {
                Log.d(TAG, "callPhoneNotAskAgin: "+ s);
            }
            //调用写好的don't ask Again权限的弹窗，如果同意则跳转到权限界面，不同意则调用 失败方法。
            MyPermission.showDialogTipUserGoToAppSettting(this, REQUEST_PERMISSION_CODE, notAskAgainList);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
