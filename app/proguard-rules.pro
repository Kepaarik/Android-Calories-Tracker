# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Hilt
-keep class dagger.** { *; }
-dontwarn dagger.internal.**
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.json.internal.**
-keepclassmembers class kotlinx.serialization.json.internal.** {
    <fields>;
    <methods>;
}

# ML Kit
-dontwarn com.google.mlkit.**
-keep class com.google.mlkit.** { *; }

# Google Fit
-dontwarn com.google.android.gms.fitness.**
-keep class com.google.android.gms.fitness.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep data classes for serialization
-keepclassmembers class com.calorietracker.data.model.** {
    <fields>;
    <init>(...);
}

# Vico Charts
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# Firebase Cloud Messaging
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**

# WorkManager
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.ListenableWorker {
    public <init>(...);
}

# Notification Helper
-keep class com.calorietracker.util.NotificationHelper { *; }
-keep class com.calorietracker.worker.** { *; }

# Telegram Auth
-keep class org.telegram.** { *; }
-dontwarn org.telegram.**
