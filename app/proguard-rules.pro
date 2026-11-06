# Proguard rules for MALLUM'S RAHUSA DATA Android App

# Keep data models serialized by Gson/Retrofit
-keepclassmembers class com.mallum.rahusadata.data.model.** { <fields>; }
-keep class com.mallum.rahusadata.data.model.** { *; }

# Retain Retrofit service interfaces
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Android Jetpack Compose rules
-keepclassmembers class * extends androidx.compose.runtime.State { *; }
-dontwarn okio.**
-dontwarn javax.annotation.**
