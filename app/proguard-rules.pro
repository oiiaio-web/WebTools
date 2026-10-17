# Keep Kotlin metadata
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
# WebView JavaScript bridge if used
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
