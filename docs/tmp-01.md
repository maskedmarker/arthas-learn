# arthas的基本应用

获取帮助信息
```text
java -jar arthas-boot.jar -h

EXAMPLES:
  java -jar arthas-boot.jar <pid>
  java -jar arthas-boot.jar --telnet-port 9999 --http-port -1
  java -jar arthas-boot.jar --username admin --password <password>
  java -jar arthas-boot.jar --tunnel-server 'ws://192.168.10.11:7777/ws' --app-name demoapp
  java -jar arthas-boot.jar --tunnel-server 'ws://192.168.10.11:7777/ws' --agent-id bvDOe8XbTM2pQWjF4cfw
  java -jar arthas-boot.jar --stat-url 'http://192.168.10.11:8080/api/stat'
  java -jar arthas-boot.jar -c 'sysprop; thread' <pid>
  java -jar arthas-boot.jar -f batch.as <pid>
  java -jar arthas-boot.jar --use-version 4.0.5
  java -jar arthas-boot.jar --versions
  java -jar arthas-boot.jar --select math-game
  java -jar arthas-boot.jar --session-timeout 3600
  java -jar arthas-boot.jar --attach-only
  java -jar arthas-boot.jar --disabled-commands stop,dump
  java -jar arthas-boot.jar --repo-mirror aliyun --use-http

WIKI:
  https://arthas.aliyun.com/doc

Options and Arguments:
    --use-http                    Enforce use http to download, default use https
    --select <value>              select target process by classname or
                                  JARfilename
    --target-ip <value>           The target jvm listen ip, default 127.0.0.1
    --versions                    List local and remote arthas versions
    --height <value>              arthas-client terminal height
    --stat-url <value>            The report stat url
    --width <value>               arthas-client terminal width
 -h,--help                        Print usage
 <pid>                            Target pid

```

## 表达式核心变量

无论是匹配表达式也好、观察表达式也罢，他们核心判断变量都是围绕着一个 Arthas 中的通用通知对象 Advice 进行

```java
public class Advice {

    private final ClassLoader loader;
    private final Class<?> clazz;
    private final ArthasMethod method;
    private final Object target;
    private final Object[] params;
    private final Object returnObj;
    private final Throwable throwExp;
    private final boolean isBefore;
    private final boolean isThrow;
    private final boolean isReturn;
    // getter/setter
}
```

```text
这里列一个表格来说明不同变量的含义

变量名	     变量解释
loader	     本次调用类所在的 ClassLoader
clazz	     本次调用类的 Class 引用
method	     本次调用方法反射引用
target	     本次调用类的实例
params	     本次调用参数列表，这是一个数组，如果方法是无参方法则为空数组
returnObj	 本次调用返回的对象。当且仅当 isReturn==true 成立时候有效，表明方法调用是以正常返回的方式结束。如果当前方法无返回值 void，则值为 null
throwExp	 本次调用抛出的异常。当且仅当 isThrow==true 成立时有效，表明方法调用是以抛出异常的方式结束。
isBefore	 辅助判断标记，当前的通知节点有可能是在方法一开始就通知，此时 isBefore==true 成立，同时 isThrow==false 和 isReturn==false，因为在方法刚开始时，还无法确定方法调用将会如何结束。
isThrow	     辅助判断标记，当前的方法调用以抛异常的形式结束。
isReturn	 辅助判断标记，当前的方法调用以正常返回的形式结束。
```

所有变量都可以在表达式中直接使用，如果在表达式中编写了不符合 OGNL 脚本语法或者引入了不在表格中的变量，则退出命令的执行；用户可以根据当前的异常信息修正条件表达式或观察表达式

## 命令列表

### jvm 相关
```text
dashboard         - 当前系统的实时数据面板
getstatic         - 查看类的静态属性
heapdump          - dump java heap, 类似 jmap 命令的 heap dump 功能
jvm               - 查看当前 JVM 的信息
logger            - 查看和修改 logger
mbean             - 查看 Mbean 的信息
memory            - 查看 JVM 的内存信息
ognl              - 执行 ognl 表达式
perfcounter       - 查看当前 JVM 的 Perf Counter 信息
sysenv            - 查看 JVM 的环境变量
sysprop           - 查看和修改 JVM 的系统属性
thread            - 查看当前 JVM 的线程堆栈信息
vmoption          - 查看和修改 JVM 里诊断相关的 option
vmtool            - 从 jvm 里查询对象，执行 forceGc
```

### class/classloader 相关
```text
classloader       - 查看 classloader 的继承树，urls，类加载信息，使用 classloader 去 getResource
dump              - dump 已加载类的 byte code 到特定目录
jad               - 反编译指定已加载类的源码
mc                - 内存编译器，内存编译.java文件为.class文件
redefine          - 加载外部的.class文件，redefine 到 JVM 里
retransform       - 加载外部的.class文件，retransform 到 JVM 里
sc                - 查看 JVM 已加载的类信息
sm                - 查看已加载类的方法信息
```

### monitor/watch/trace 相关

请注意，这些命令，都通过字节码增强技术来实现的，会在指定类的方法中插入一些切面来实现数据统计和观测，因此在线上、预发使用时，请尽量明确需要观测的类、方法以及条件，诊断结束要执行 stop 或将增强过的类执行 reset 命令。

```text
monitor            - 方法执行监控
stack              - 输出当前方法被调用的调用路径
trace              - 方法内部调用路径，并输出方法路径上的每个节点上耗时
tt                 - 方法执行数据的时空隧道，记录下指定方法每次调用的入参和返回信息，并能对这些不同的时间下调用进行观测
watch              - 方法执行数据观测
```

### profiler/火焰图

```text
profiler           - 使用async-profiler对应用采样，生成火焰图
jfr                - 动态开启关闭 JFR 记录
```

### 后台异步任务

```text
当线上出现偶发的问题，比如需要 watch 某个条件，而这个条件一天可能才会出现一次时，异步后台任务就派上用场了，详情请参考这里

使用       > 将结果重写向到日志文件，使用 & 指定命令是后台运行，session 断开不影响任务执行（生命周期默认为 1 天）
jobs      - 列出所有 job
kill      - 强制终止任务
fg        - 将暂停的任务拉到前台执行
bg        - 将暂停的任务放到后台执行
```
### 基础命令

```text
base64     - base64 编码转换，和 linux 里的 base64 命令类似
cat        - 打印文件内容，和 linux 里的 cat 命令类似
cls        - 清空当前屏幕区域
echo       - 打印参数，和 linux 里的 echo 命令类似
grep       - 匹配查找，和 linux 里的 grep 命令类似
help       - 查看命令帮助信息
history    - 打印命令历史
keymap     - Arthas 快捷键列表及自定义快捷键
pwd        - 返回当前的工作目录，和 linux 命令类似
quit       - 退出当前 Arthas 客户端，其他 Arthas 客户端不受影响
reset      - 重置增强类，将被 Arthas 增强过的类全部还原，Arthas 服务端关闭时会重置所有增强过的类
session    - 查看当前会话的信息
stop       - 关闭 Arthas 服务端，所有 Arthas 客户端全部退出
tee        - 复制标准输入到标准输出和指定的文件，和 linux 里的 tee 命令类似
version    - 输出当前目标 Java 进程所加载的 Arthas 版本号
```


