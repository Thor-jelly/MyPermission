package com.dongdongwu.mypermission;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：权限请求工具类<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/2/27 14:51 <br/>
 */

public class MyPermissionUtils {
    private static final String TAG = "MyPermissionUtils";

    private MyPermissionUtils() {
        throw new UnsupportedOperationException("不能够初始化权限请求工具类");
    }

    /**
     * 判断是否6.0以上的版本
     */
    public static boolean isSDKVersionOverM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 执行请求权限成功方法
     *  @param reflectObject  反射的类
     * @param requestCode 请求码
     */
    public static void exectueSuccessMethod(Object reflectObject, int requestCode) {
        //获取aClass中所有方法
        Method[] methods = reflectObject.getClass().getDeclaredMethods();
        //遍历所有方法，找到我们打注解的方法
        for (Method method : methods) {
            Log.d(TAG, "exectueSuccessMethod: "+method);
            //获取该方法上面有没有打PermissionSuccess标记，没有这个注解返回为null
            PermissionSuccess permissionSuccess = method.getAnnotation(PermissionSuccess.class);
            if (permissionSuccess != null) {
                //获取方法上请求码
                int methodRequestCode = permissionSuccess.requestCode();
                //判断是否方法上的和设置的是否为一样
                if (methodRequestCode == requestCode) {
                    //如果一样该方法就是我们要执行的方法
                    exectueMethod(reflectObject, method);
                    break;
                }
            }
        }
    }
    /**
     * 执行请求权限失败方法
     *  @param reflectObject  反射的类
     * @param requestCode 请求码
     */
    public static void exectueFailureMethod(Object reflectObject, int requestCode) {
        //获取aClass中所有方法
        Method[] methods = reflectObject.getClass().getDeclaredMethods();
        //遍历所有方法，找到我们打注解的方法
        for (Method method : methods) {
            Log.d(TAG, "exectueFailureMethod: " + method);
            //获取该方法上面有没有打PermissionSuccess标记，没有这个注解返回为null
            PermissionFailure permissionFailure = method.getAnnotation(PermissionFailure.class);
            if (permissionFailure != null) {
                //获取方法上请求码
                int methodRequestCode = permissionFailure.requestCode();
                //判断是否方法上的和设置的是否为一样
                if (methodRequestCode == requestCode) {
                    //如果一样该方法就是我们要执行的方法
                    exectueMethod(reflectObject, method);
                    break;
                }
            }
        }
    }
    /**
     * 执行请求权限失败方法
     * @param reflectObject  反射的类
     * @param requestCode 请求码
     * @param notAskAgainList
     */
    public static void exectueNotAskAgainMethod(Object reflectObject, int requestCode, List<String> notAskAgainList) {
        //获取aClass中所有方法
        Method[] methods = reflectObject.getClass().getDeclaredMethods();
        //遍历所有方法，找到我们打注解的方法
        for (Method method : methods) {
            Log.d(TAG, "exectueSuccessMethod: "+method);
            //获取该方法上面有没有打PermissionSuccess标记，没有这个注解返回为null
            PermissionNotAskAgain permissionNotAskAgain = method.getAnnotation(PermissionNotAskAgain.class);
            if (permissionNotAskAgain != null) {
                //获取方法上请求码
                int methodRequestCode = permissionNotAskAgain.requestCode();
                //判断是否方法上的和设置的是否为一样
                if (methodRequestCode == requestCode) {
                    //如果一样该方法就是我们要执行的方法
                    exectueMethod(reflectObject, method, notAskAgainList);
                    break;
                }
            }
        }
    }

    /**
     * 反射执行该方法
     *
     * @param reflectObject 反射的方法在哪一个类中
     * @param method 反射的方法
     */
    private static void exectueMethod(Object reflectObject, Method method) {
        exectueMethod(reflectObject, method, null);
    }
    private static void exectueMethod(Object reflectObject, Method method, List<String> notAskAgainList) {
        Log.d(TAG, "找到类该方法并执行类该方法: "+method);
        try {
            //允许执行私有方法
            method.setAccessible(true);
            //反射执行方法
            //第一个参数 该方法属于哪一个类
            //第二个参数 参数，没有传一个空数组
            if (notAskAgainList == null) {
                method.invoke(reflectObject, new Object[]{});
            } else {
                method.invoke(reflectObject, new Object[]{notAskAgainList});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有还未授予的权限
     *
     * @param object 当前类也就是activity 或者 fragment
     * @param requestPermissions 请求的权限
     * @return 还未授予的权限
     */
    public static List<String> getDeniedPermissions(Object object, String[] requestPermissions) {
        List<String> deniedPermissions = null;
        for (String requestPermission : requestPermissions) {
            //把没有授予过的权限加入到deniedPermissions集合中

            if (ContextCompat.checkSelfPermission(getActivity(object), requestPermission) == PackageManager.PERMISSION_DENIED) {
                if (deniedPermissions == null) {
                    deniedPermissions = new ArrayList<>();
                }
                deniedPermissions.add(requestPermission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 获取上下文
     * @param object 当前类也就是activity 或者 fragment
     * @return 上下文
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        throw new RuntimeException("MyPermissionUtils getContext方法 传入参数错误！");
    }

    /**
     * 判断是否有不在提示的权限
     */
    public static List<String> shouldShowRequestPermissionRationale(Object object, String... deniedPermissions) {
        Activity activity = getActivity(object);
        List<String> notAskAgainList = null;
        for (String deniedPermission : deniedPermissions) {
            /*
                如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                注：
                如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。
                如果设备规范禁止应用具有该权限，此方法也会返回 false。
             */
            Log.d(TAG, "shouldShowRequestPermissionRationale: "+deniedPermission+"  "+(ActivityCompat.shouldShowRequestPermissionRationale(activity, deniedPermission)));
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, deniedPermission)) {
                if (notAskAgainList == null) {
                    notAskAgainList = new ArrayList<>();
                }
                notAskAgainList.add(deniedPermission);
            }
        }
        if (deniedPermissions != null && notAskAgainList != null) {
            return notAskAgainList;
        }
        return notAskAgainList;
    }
}
