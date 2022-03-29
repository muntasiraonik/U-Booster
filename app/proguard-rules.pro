# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class org.niklab.utubebooster.viewholders.** { *; }
-keep class com.firebase.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-keep class org.niklab.utubebooster.ViewsAdapterHolder
-dontwarn com.google.gms.**
-dontwarn groovy.**
-dontwarn org.**
-dontwarn java.**
-dontwarn javax.**
-keep class org.apache.http.** { *; }
-keep class org.apache.** { *; }
-dontwarn org.apache.**
-dontwarn org.apache.http.**
-dontwarn org.apache.commons.**
-keep class com.google.api.** { *; }
-keepattributes Annotation
-ignorewarnings

-keep class com.startapp.** {
      *;
}

-keep class com.truenet.** {
      *;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**
-keep class com.revenuecat.purchases.** { *; }
-dontwarn org.jetbrains.annotations.**
-keep class android.support.v7.widget.LinearLayoutManager { *; }
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
