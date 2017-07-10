package com.sopfix_master.www;

import android.app.Application;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * 作者：Mou
 * 时间：2017/6/14 20:15
 * 邮箱：410903739@qq.com
 * 描述：
 */

public class MyApplication extends Application {
    public static MsgDisplayListener msgDisplayListener = null;
    public static StringBuilder cacheMsg = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();
        initSopfix();
    }

    private void initSopfix() {
        String appVersion;
        try {
            //获取当前版本号
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "1.0.0.0";
        }
        SophixManager.getInstance().setContext(this).setAppVersion(appVersion).setAesKey(null)
                //.setAesKey("0123456789123456")
                .setEnableDebug(false).setPatchLoadStatusStub(new PatchLoadStatusListener() {
            @Override
            public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                String msg = new StringBuilder("").append(" Mode:").append(mode).append(" Code:").append(code).append
                        (" Info:").append(info).append(" HandlePatchVersion:").append(handlePatchVersion).toString();
                if (msgDisplayListener != null) {
                    msgDisplayListener.handle(msg);
                } else {
                    cacheMsg.append("\n").append(msg);
                }
                if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                    // 表明补丁加载成功
                } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                    // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                    // 建议: 用户可以监听进入后台事件, 然后应用自杀
                } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                    // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                    SophixManager.getInstance().cleanPatches();
                } else {
                    // 其它错误信息, 查看PatchStatus类说明
                }
            }
        }).initialize();
        SophixManager.getInstance().queryAndLoadNewPatch();//加载新的补丁包
    }

    public interface MsgDisplayListener {
        void handle(String msg);
    }
}
