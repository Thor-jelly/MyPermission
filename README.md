# [MyPermission](https://github.com/Thor-jelly/MyPermission)
Android 6.0权限申请框架

[![GitHub release](https://img.shields.io/badge/release-v1.0.1-green.svg)](https://github.com/Thor-jelly/MyPermission/releases)

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
dependencies {
    compile 'com.github.Thor-jelly:MyPermission:v1.0.1'
}
```

# Android 6.0权限申请框架搭建
> 有不足之处请联系745661590@qq.com  
> 反射加注解的方式  
> 仿[PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher)一个好用的权限请求框架


# 方法
|方法名|描述|
|:----|:---|
|with|  传入当前界面对象|
|setRequestCode|    设置请求码|
|setRequestPermission|  设置请求权限|
|requestPermission|  请求权限|

# 使用样例
可以直接查看代码中的样例

## 第一步，在需要请求权限的地方写请求权限代码

```
    MyPermission.with(this)
                .setRequestCode(REQUEST_PERMISSION_CODE)
                .setRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS)
                .requestPermission();
```

## 第二步，通过注解方式写出 权限赋予成功，失败，don't ask again 这三种情况方法

```
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
```

## 最后一步，重写onRequestPermissionsResult方法，并添加`MyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);`一句话

```
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
```

# 注解的一些小常识
java中元注解有四个： @Retention @Target @Document @Inherited  
注解作用：注解就是在反射的时候做一个标记

|注解名|描述|
|:---|:---|
|@Retention|    注解的保留位置|　　　　　　　　　
|@Retention(RetentionPolicy.SOURCE)|    注解仅存在于源码中，在class字节码文件中不包含|
|@Retention(RetentionPolicy.CLASS)| 默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得|
|@Retention(RetentionPolicy.RUNTIME)|   注解会在class字节码文件中存在，在运行时可以通过反射获取到|
|-----|-----|
|@Target| 注解的作用目标|
|@Target(ElementType.TYPE)| 接口、类、枚举、注解|
|@Target(ElementType.FIELD)|    字段、枚举的常量|
|@Target(ElementType.METHOD)|   方法|
|@Target(ElementType.PARAMETER)|    方法参数|
|@Target(ElementType.CONSTRUCTOR)|  构造函数|
|@Target(ElementType.LOCAL_VARIABLE)|   局部变量|
|@Target(ElementType.ANNOTATION_TYPE)|  注解|
|@Target(ElementType.PACKAGE)|  包|
|-----|-----|
|@Document| 说明该注解将被包含在javadoc中|
|-----|-----|
|@Inherited|    说明子类可以继承父类中的该注解|
