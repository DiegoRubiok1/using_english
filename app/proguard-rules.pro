# Room rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.Room
-keep class androidx.room.RoomDatabase
-keep class androidx.room.util.TableInfo
-keep class androidx.room.util.TableInfo$Column
-keep class androidx.room.util.TableInfo$ForeignKey
-keep class androidx.room.util.TableInfo$Index

# Keep your data entities and DAOs
-keep class com.example.using_english.data.** { *; }

# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep class com.example.using_english.data.ExerciseEntity { *; }
-keep class com.example.using_english.data.UserStatsEntity { *; }
