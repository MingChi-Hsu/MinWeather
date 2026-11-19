# R8 rules for MinWeather.
# Most libraries (Hilt, OkHttp, Coil, kotlinx.serialization 1.5+) ship their own rules,
# so only the gaps are covered here.

# Keep line numbers so crash reports in Play Console point to the right line,
# but hide the original file names
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Retrofit ---
# Retrofit reads the annotations and generic types of the API interface at runtime
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep the API interface (e.g. WeatherApiService) so Retrofit can create it
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# R8 full mode removes generic types from classes it does not keep. Retrofit needs them
# to work out the return type of suspend functions such as List<LocationInfo>.
# (Retrofit 2.10+ includes these rules; this project uses 2.9.0)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# --- kotlinx.serialization ---
# Keep the generated serializers of @Serializable classes (the API models)
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
