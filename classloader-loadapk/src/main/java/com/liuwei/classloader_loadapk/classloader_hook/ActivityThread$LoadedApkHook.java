package com.liuwei.classloader_loadapk.classloader_hook;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.liuwei.classloader_loadapk.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

/**
 * Created by liuwei on 17/3/6.
 */
public class ActivityThread$LoadedApkHook {
    public static Map<String, Object> sLoadedApk = new HashMap<String, Object>();

    /**
     * 由源码可以看出系统在获取LoadedApk时会先去mPackages中去查找,如果有的话直接用缓存的,这正好给我们提供了一个切入点,
     * 我们可以事先手动构建好LoadedApk,然后提前放入mPackages中。
     */
    public static void doHookActivityThreadLoadedApk(Context context, File apkFile){

        try {

            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);

            // 获取mPackages,并将自己构建的loadedapk放入里面
            Field mPackagesField = activityThreadClass.getDeclaredField("mPackages");
            mPackagesField.setAccessible(true);
            Map mPackages = (Map)mPackagesField.get(activityThread);

            // 构建getPackageInfoNoCheck的第一个参数
            ApplicationInfo applicationInfo = generateApplicationInfo(apkFile);

            // 构建getPackageInfoNoCheck的第二个参数
            Class<?> compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
            Field defaultCompatibilityInfoField = compatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
            defaultCompatibilityInfoField.setAccessible(true);
            Object defaultCompatibilityInfo = defaultCompatibilityInfoField.get(null);

            /**
             * 源码位置:
             public void More ...handleMessage(Message msg) {
             1295            if (DEBUG_MESSAGES) Slog.v(TAG, ">>> handling: " + codeToString(msg.what));
             1296            switch (msg.what) {
             1297                case LAUNCH_ACTIVITY: {
             1298                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");
             1299                    final ActivityClientRecord r = (ActivityClientRecord) msg.obj;
             1300
             1301                    r.packageInfo = getPackageInfoNoCheck(
             1302                            r.activityInfo.applicationInfo, r.compatInfo);
             1303                    handleLaunchActivity(r, null);
             1304                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
             1305                } break;
             *************************
             *
             * public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai,
             1732            CompatibilityInfo compatInfo) {
             1733        return getPackageInfo(ai, compatInfo, null, false, true, false);
             1734    }
             */
            Method getPackageInfoNoChecksMethod = activityThreadClass.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, compatibilityInfoClass);
            Object loadedApk = getPackageInfoNoChecksMethod.invoke(activityThread, applicationInfo, defaultCompatibilityInfo);

            // 这里我还需要构建LoadedApk的mClassLoader
            String dexPath = apkFile.getPath();
            String optDir = Utils.getPluginOptDexDir(context, applicationInfo.packageName).getPath();
            String libPath = Utils.getPluginLibDir(context, applicationInfo.packageName).getPath();
            ClassLoader mClassLoader = new DexClassLoader(dexPath, optDir, libPath, ClassLoader.getSystemClassLoader());

            Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");
            Field mClassLoaderField = loadedApkClass.getDeclaredField("mClassLoader");
            mClassLoaderField.setAccessible(true);
            mClassLoaderField.set(loadedApk, mClassLoader);

            // 由于是若引用,存一份到内存中,防止gc
            sLoadedApk.put(applicationInfo.packageName, loadedApk);

            WeakReference weakReference = new WeakReference(loadedApk);
            mPackages.put(applicationInfo.packageName, weakReference);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 手动构建ApplicationInfo
     * @param apkFile
     * @return
     */
    private static ApplicationInfo generateApplicationInfo(File apkFile){
        /**
         * public static ApplicationInfo generateApplicationInfo(Package p, int flags,
         5370            PackageUserState state) {
         5371        return generateApplicationInfo(p, flags, state, UserHandle.getCallingUserId());
         5372    }
         */

        try {
            Class<?> packageParseClass = Class.forName("android.content.pm.PackageParser");
            Class<?> packageParse$PakageClass = Class.forName("android.content.pm.PackageParser$Package");
            Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
            Method generateApplicationInfoMethod = packageParseClass.getDeclaredMethod("generateApplicationInfo",
                    packageParse$PakageClass, int.class, packageUserStateClass);

            /**
             * 通过此段源码构建generateApplicationInfoMethod的第一个参数
             *
             * public Package parsePackage(File packageFile, int flags) throws PackageParserException {
             753         if (packageFile.isDirectory()) {
             754             return parseClusterPackage(packageFile, flags);
             755         } else {
             756             return parseMonolithicPackage(packageFile, flags);
             757         }
             758     }
             */
            Method parsePackageMethod = packageParseClass.getDeclaredMethod("parsePackage", File.class, int.class);
            Object parsePackage = parsePackageMethod.invoke(packageParseClass.newInstance(), apkFile, 0);

            // 构建第二个参数
            Object packageUserState = packageUserStateClass.newInstance();

            ApplicationInfo applicationInfo = (ApplicationInfo)generateApplicationInfoMethod.invoke(null, parsePackage, 0, packageUserState);
            applicationInfo.sourceDir = apkFile.getPath();
            applicationInfo.publicSourceDir = apkFile.getPath();

            return applicationInfo;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
