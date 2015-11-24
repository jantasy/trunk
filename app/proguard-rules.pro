# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/chenshang/Developer/adt-bundle-mac-x86_64-20140321/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}



-keep class cn.yjt.oa.app.beans.**{*;}
-keep class cn.yjt.oa.app.teleconference.beans.**{*;}
-keep class cn.yjt.oa.app.lifecircle.model.**{*;}
-ignorewarnings
-keepattributes Signature,EnclosingMethod
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

-keep public class * extends com.umeng.**

-keep class com.umeng.** { *; }
-keep,allowshrinking class org.android.agoo.service.* {
    public <fields>;
    public <methods>;
}

-keep,allowshrinking class com.umeng.message.* {
    public <fields>;
    public <methods>;
}

-keep public class cn.yjt.oa.app.R$*{
	public static final int *;
}

#-libraryjars lib/org.simalliance.openmobileapi.jar
-keep class org.simalliance.openmobileapi.** {*;}
-keepclassmembers class cn.yjt.oa.app.utils.YjtJavaScriptInterface$CustAdmin {*;}

#-dontwarn android.support.v4.**
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.app.** { *; }
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.app.Fragment

-keep class org.simalliance.openmobileapi.** {*;}

-keep class com.telecompp.** {*;}
-keep class com.transport.cypher.** {*;}

-keep class com.tcps.fzNfc.** {*;}

-keep class com.telecompp.ContextUtil{
    public <methods>;
}

-keep class com.transport.ConvertUtil{
    public <methods>;
}

-keep class com.telecompp.util.AlgoManager{
    public <methods>;
}
-keep class com.transport.db.dao.PConfirmDao{
    public <methods>;
}
-keep class com.telecompp.util.TerminalDataManager{
    public <methods>;
}
-keep class com.transport.cypher.KEYSUtil{
    public <methods>;
}
-keep class com.transport.db.dao.PConfirmDao{
    public <methods>;
}
-keep class com.transport.db.dao.PIsLoadSuccessDao{
    public <methods>;
}

-keep class cn.robusoft.http.** {*;}
-keep class com.activeandroid.**{*;}
