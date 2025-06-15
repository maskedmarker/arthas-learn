# jvm监控相关的工具


## jstat 

```text
jstat [option] <pid> [interval] [count]

-gc     	        显示 GC 概况（常用）
-gccapacity	        显示各个内存区域的容量
-gcutil	            显示内存区域使用率（最直观）
-class	            类加载统计信息
-compiler	        JIT 编译器统计
-printcompilation	最近被编译的方法
```

### gc情况
```text
# 持续查看 GC 情况（每 2 秒刷新一次）
jstat -gc 12345 2000
jstat -gcutil 12345 2000

YGC：       Young GC 次数，YGCT：其耗时
FGC：       Full GC 次数，FGCT：其耗时
EC / EU：   Eden 区容量 / 使用量
OC / OU：   老年代容量 / 使用量
```

### class加载情况
```text
# 类加载统计信息
jstat -class 12345 1000 5

Loaded	    JVM 当前已加载的类总数（自启动以来）
Bytes	    已加载类占用的字节数（class 文件大小）
Unloaded	被卸载的类数量（通常很少）
Bytes	    卸载类释放的字节数
Time	    类加载/卸载所耗的累计时间（单位：秒）

#
还可以通过java的参数 -verbose:class
这会在 stdout 中输出每个被加载的类


# 启动参数启用 JFR（推荐：profile 模式）
java -XX:+UnlockCommercialFeatures -XX:StartFlightRecording=filename=hello-arthas.jfr,duration=60s,settings=profile -jar  hello-arthas.jar
参数说明：
filename=hello-arthas.jfr：生成的 JFR 文件名
duration=60s：记录时长 60 秒（也可以用 maxsize=100MB 控制大小）
settings=profile：比默认 default 更适合性能分析，包括类加载事件

# 你可以用 jcmd 在运行中打标记：
jcmd <pid> JFR.dump name=default filename=now.jfr
```