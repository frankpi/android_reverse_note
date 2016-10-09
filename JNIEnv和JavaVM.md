##1.关于JNIEnv和JavaVM
JNIEnv是一个与线程相关的变量，不同线程的JNIEnv彼此独立。JavaVM是虚拟机在JNI层的代表，在一个虚拟机进程中只有一个JavaVM，因此该进程的所有线程都可以使用这个JavaVM。当后台线程需要调用JNI native时，在native库中使用全局变量保存JavaVM尤为重要，这样使得后台线程能通过JavaVM获得JNIEnv。  
native程序中频繁使用JNIEnv*和JavaVM*。而C和C++代码使用JNIEnv*和JavaVM*这两个指针的做法是有区别的，网上大部分代码都使用C++，基本上找不到关于C和C++在这个问题上的详细叙述.  
在C中：  
使用JNIEnv* env要这样      (*env)->方法名(env,参数列表)  
使用JavaVM* vm要这样       (*vm)->方法名(vm,参数列表)  
在C++中：  
使用JNIEnv* env要这样      env->方法名(参数列表)  
使用JavaVM* vm要这样       vm->方法名(参数列表)  
上面这二者的区别是，在C中必须先对env和vm间接寻址（得到的内容仍然是一个指针），在调用方法时要将env或vm传入作为第一个参数。C++则直接利用env和vm指针调用其成员。那到底C中的(*env)和C++中的env是否有相同的数据类型呢？C中的(*vm) 和C++中的vm是否有相同的数据类型呢？
为了验证上面的猜测，我们可以查看JNIEnv和JavaVM的定义。他们位于头文件jni.h。  
我开发JNI用的是Android-5平台，下面是 $NDK\platforms\android-5\arch-arm\usr\include\jni.h的部分代码
`struct _JNIEnv;  
  
struct _JavaVM;  
  
#if defined(__cplusplus)  
  
typedef _JNIEnv JNIEnv;                                 //C++使用这个类型  
  
typedef _JavaVM JavaVM;                                 //C++使用这个类型  
  
#else  
  
typedef const struct JNINativeInterface* JNIEnv;        //C使用这个类型  
  
typedef const struct JNIInvokeInterface* JavaVM;        //C使用这个类型  
  
#endif  
  
struct JNINativeInterface  
  
{  
  
    /****省略了的代码****/  
  
    jmethodID   (*GetMethodID)(JNIEnv*, jclass, const char*, const char*);  
  
    /****省略了的代码****/  
  
    jobject     (*GetStaticObjectField)(JNIEnv*, jclass, jfieldID);  
  
    /****省略了的代码****/  
  
};  
  
struct _JNIEnv  
{  
    const struct JNINativeInterface* functions;  
    #if defined(__cplusplus)  
    /****省略了的代码****/  
    jmethodID GetMethodID(jclass clazz, const char* name, const char* sig)  
    { return functions->GetMethodID(this, clazz, name, sig); }  
    /****省略了的代码****/  
    jobject GetStaticObjectField(jclass clazz, jfieldID fieldID)  
    { return functions->GetStaticObjectField(this, clazz, fieldID); }  
    /****省略了的代码****/  
    #endif /*__cplusplus*/  
};  
  
struct JNIInvokeInterface  
{  
     /****省略了的代码****/  
    jint (*GetEnv)(JavaVM*, void**, jint);  
    jint (*AttachCurrentThreadAsDaemon)(JavaVM*, JNIEnv**, void*);  
};  
  
struct _JavaVM  
{  
    const struct JNIInvokeInterface* functions;  
    #if defined(__cplusplus)  
    /****省略了的代码****/  
    jint GetEnv(void** env, jint version)  
    { return functions->GetEnv(this, env, version); }  
    jint AttachCurrentThreadAsDaemon(JNIEnv** p_env, void* thr_args)  
    { return functions->AttachCurrentThreadAsDaemon(this, p_env, thr_args); }  
    #endif /*__cplusplus*/  
};  `
