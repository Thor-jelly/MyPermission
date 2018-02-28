package com.dongdongwu.mypermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类描述：权限成功注解<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/2/27 15:23 <br/>
 */

@Target(ElementType.METHOD)//放在什么位置：ElementType.METHOD 放在方法上面
@Retention(RetentionPolicy.RUNTIME)//是编译时检测 还是 运行时检测
public @interface PermissionNotAskAgain {
    int requestCode(); //请求码
}
