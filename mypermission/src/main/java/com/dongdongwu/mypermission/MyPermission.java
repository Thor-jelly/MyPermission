package com.dongdongwu.mypermission;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

/**
 * 类描述：权限申请6.0，反射加注解的方式，builder设计模式和单例设计模式<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/2/26 18:31 <br/>
 */

public class MyPermission {
    private volatile static MyPermission singleton = null;

    /**
     * 当前界面，必选，final类型需要再构造器初始化时候赋值
     */
    private final Object mObject;
    /**
     * 请求码
     */
    private int mRequestCode;
    /**
     * 请求权限
     */
    private String[] mRequestPermission;

    //1、想想创建好类之后需要什么，也就是要传递什么数据过来
    //第一个参数肯定是当前页面对象activity 或者 fragment
    //第二个参数肯定是要请求的权限码
    //第三个参数肯定是要请求的权限
    //暂时就这些如果还有再传进来

//    /**
//     * 第一次请求时候必须调用的方法
//     * 方法淘汰转变为builder设计模式
//     *
//     * @param activity 当前界面
//     * @param requestCode 请求码
//     * @param requestPermission 请求权限
//     */
//    @Deprecated
//    public static void requestPermission(Activity activity, int requestCode, String... requestPermission) {
//        this.mObject = activity;
//        this.mRequestCode = requestCode;
//        this.mRequestPermission = requestPermission;
//    }

    private MyPermission(Builder builder) {
        mObject = builder.mObject;
    }

    public static MyPermission with(Activity activity) {
        getSingleton(activity);
        return singleton;
    }

    public static MyPermission with(Fragment fragment) {
        getSingleton(fragment);
        return singleton;
    }

    private static void getSingleton(Object object) {
        if (singleton == null) {
            synchronized (MyPermission.class) {
                if (singleton == null) {
                    if (object instanceof Activity) {
                        singleton = new Builder().with((Activity) object).build();
                    } else if (object instanceof Fragment) {
                        singleton = new Builder().with((Fragment) object).build();
                    } else {
                        throw new RuntimeException("with方法 传入参数错误!");
                    }
                }
            }
        }
    }

    /**
     * 设置请求码
     */
    public MyPermission setRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    /**
     * 设置请求权限
     */
    public MyPermission setRequestPermission(String... requestPermission) {
        mRequestPermission = requestPermission;
        return this;
    }

    /**
     * 请求权限
     */
    public void requestPermission() {
        //判断是否为6.0以上的版本
        //MyPermissionUtils.isSDKVersionOverM()

        //1-如果不是就直接执行方法
        if (!MyPermissionUtils.isSDKVersionOverM()) {
            //通过反射执行方法(activity 或 fragment 中的方法)
            //因为方法不确定，所以采用注解的方式给方法打一个标记
            //然后通过反射去执行。
            MyPermissionUtils.executeSuccessMethod(mObject, mRequestCode);
        } else {
            //2-如果是就请求权限之类的操作
            //获取权限中是否有还未授予的权限
            List<String> deniedPermissions = MyPermissionUtils.getDeniedPermissions(mObject, mRequestPermission);

            //如果deniedPermissions为null表示都是授权过的直接执行成功方法
            if (deniedPermissions == null) {
                MyPermissionUtils.executeSuccessMethod(mObject, mRequestCode);
                return;
            }

            //如果不为null表示有未授权的权限，重新申请权限
            //MyPermissionUtils.shouldShowRequestPermissionRationale(mObject, deniedPermissions.toArray(new String[deniedPermissions.size()]));
            ActivityCompat.requestPermissions(MyPermissionUtils.getActivity(mObject),
                    deniedPermissions.toArray(new String[deniedPermissions.size()]),
                    mRequestCode);
        }
    }

    /**
     * 重新请求权限走的方法
     */
    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        onRequestPermissionsResultMethod(activity, requestCode, permissions, grantResults);
    }

    /**
     * 重新请求权限走的方法
     */
    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        onRequestPermissionsResultMethod(fragment, requestCode, permissions, grantResults);
    }

    /**
     * 重新请求权限走的方法
     */
    private static void onRequestPermissionsResultMethod(Object object, int requestCode, String[] permissions, int[] grantResults) {
        //再次获取没有获取的权限
        if (!MyPermissionUtils.isSDKVersionOverM()) {
            MyPermissionUtils.executeSuccessMethod(object, requestCode);
            return;
        } else {
            //如果deniedPermissions为null表示都是授权过的直接执行成功方法
            List<String> notAskAgainList = MyPermissionUtils.shouldShowRequestPermissionRationale(object, permissions);
            if (notAskAgainList != null) {
                //showDialogTipUserGoToAppSettting(object, notAskAgainList.get(0), requestCode);
                //用户点了don't ask againt
                MyPermissionUtils.executeNotAskAgainMethod(object, requestCode, notAskAgainList);
                return;
            } else {
                //
                List<String> deniedPermissions = MyPermissionUtils.getDeniedPermissions(object, permissions);
                if (deniedPermissions == null) {
                    MyPermissionUtils.executeSuccessMethod(object, requestCode);
                    return;
                } else {
                    //申请权限用户拒绝
                    MyPermissionUtils.executeFailureMethod(object, requestCode);
                    return;
                }
            }
        }
    }

    /**
     * 提示用户去应用设置界面手动开启权限
     */
    public static void showDialogTipUserGoToAppSettting(final Activity activity, final int requestCode, List<String> titles) {
        showDialogTipUserGoToAppSetttingMethod(activity, requestCode, titles);
    }

    /**
     * 提示用户去应用设置界面手动开启权限
     */
    public static void showDialogTipUserGoToAppSettting(final Fragment fragment, final int requestCode, List<String> titles) {
        showDialogTipUserGoToAppSetttingMethod(fragment, requestCode, titles);
    }

    private static void showDialogTipUserGoToAppSetttingMethod(final Object object, final int requestCode, List<String> titles) {
        StringBuilder sb = new StringBuilder("");
        if (titles != null && titles.size() > 0) {
            for (String title : titles) {
                sb.append("\u3000\u3000" + title + "\n");
            }
        }
        // 跳转到应用设置界面
        new AlertDialog.Builder(MyPermissionUtils.getActivity(object))
                .setTitle("don't ask again 的权限！")
                .setMessage("请跳转到设置界面同意下面权限：\n"
                        + sb.toString())
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting(MyPermissionUtils.getActivity(object));
                    }
                })
                .setNegativeButton("不同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyPermissionUtils.executeFailureMethod(object, requestCode);
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private static void goToAppSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static final class Builder {
        /**
         * 当前界面
         */
        private Object mObject;

        /**
         * activity 调用
         */
        public Builder with(Activity activity) {
            mObject = activity;
            return this;
        }

        /**
         * fragment 调用
         */
        public Builder with(Fragment fragment) {
            mObject = fragment;
            return this;
        }

        public MyPermission build() {
            MyPermission myPermission = new MyPermission(this);
            //如需要一些判断在这里添加
            //......
            return myPermission;
        }
    }
}
