-keepparameternames

-keepattributes Signature,Exceptions,*Annotation*,
                InnerClasses,PermittedSubclasses,EnclosingMethod,
                Deprecated,SourceFile,LineNumberTable

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keep class dev.nosytools.logger.Logger { public *; }
-keep class dev.nosytools.logger.scheduler.SendLogsWorker { public *; }

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# GRPC
-keep public class io.grpc.** { *; }
-keep public interface io.grpc.** { *; }

-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
  <fields>;
}
