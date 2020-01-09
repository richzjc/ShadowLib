package com.xiangpan.roundimageview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.richzjc.shadowlib.MyShadowLayout;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends Activity implements ITestProxy, IProxy{

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.i("classLoader", getClassLoader() + "");
        Log.i("classLoader", ITestProxy.class.getClassLoader() + "");
        Log.i("classLoader", IProxy.class.getClassLoader() + "");
        Object obj = Proxy.newProxyInstance(getClassLoader(), new Class[]{ITestProxy.class, IProxy.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });

        Log.i("obj", "" + obj);
    }

    @Override
    public void iProxy() {
        Log.i(getClass().getSimpleName(), "IProxy");
    }

    @Override
    public void testProxy() {
        Log.i(getClass().getSimpleName(), "TestProxy");
    }

    @Override
    public void onBackPressed() {
        if(i%2 == 0){

        }else{

        }
    }
}
