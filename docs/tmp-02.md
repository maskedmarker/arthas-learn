# arthas常用命令

## trace

展示指定方法内调用其他方法的耗时.
正常清空下,只能展示本方法所调用的其他方法的耗时,不会再进一步展示被调用方法内的耗时情况,如需则需要配合使用--listenerId

```text
USAGE:                                                                                                                                                                                                                                
   trace [--exclude-class-pattern <value>] [-h] [-n <value>] [--listenerId <value>] [-m <value>] [-p <value>] [-E] [--skipJDKMethod <value>] [-v] class-pattern method-pattern [condition-express]

OPTIONS:
 -p, --path <value>                                                           path tracing pattern
 -E, --regex                                                                  Enable regular expression to match (wildcard matching by default)
 --skipJDKMethod <value>                                                      skip jdk method trace, default value true.
 -v, --verbose                                                                Enables print verbose information, default value false.
 <class-pattern>                                                              Class name pattern, use either '.' or '/' as separator
 <method-pattern>                                                             Method name pattern
 <condition-express>                                                          Conditional expression in ognl style,   

SUMMARY:                                                                                                                                                                                                                              
   Trace the execution time of specified method invocation. (即跟踪指定方法内部各步骤的耗时)                                                                                                                                                           

condition-express中涉及到的变量
The express may be one of the following expression (evaluated dynamically):                                                                                                                                                         
       target : the object                                                                                                                                                                                                         
        clazz : the object's class                                                                                                                                                                                                 
       method : the constructor or method                                                                                                                                                                                          
       params : the parameters array of method                                                                                                                                                                                     
 params[0..n] : the element of parameters array                                                                                                                                                                                    
    returnObj : the returned object of method
     throwExp : the throw exception of method
     isReturn : the method ended by return                                                                                                                                                                                         
      isThrow : the method ended by throwing exception
        #cost : the execution time in ms of method invocation
       
```

## 样例

trace org.springframework.web.servlet.DispatcherServlet doService '#cost > 10'
表示: 如果org.springframework.web.servlet.DispatcherServlet#doService的总耗时大于10ms时,才展示.

```text
[arthas@14804]$ trace org.springframework.web.servlet.DispatcherServlet doService '#cost > 5'
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 71 ms, listenerId: 7
`---ts=2025-06-15 18:05:34.062;thread_name=http-nio-9080-exec-7;id=41;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@4c0e426a
    `---[5.4405ms] org.springframework.web.servlet.DispatcherServlet:doService()
        +---[7.02% 0.3818ms ] org.springframework.web.servlet.DispatcherServlet:logRequest() #911
        +---[0.18% 0.0099ms ] org.springframework.web.util.WebUtils:isIncludeRequest() #916
        +---[0.23% 0.0124ms ] org.springframework.web.servlet.DispatcherServlet:getWebApplicationContext() #928
        +---[0.21% 0.0115ms ] javax.servlet.http.HttpServletRequest:setAttribute() #928
        +---[0.08% 0.0043ms ] javax.servlet.http.HttpServletRequest:setAttribute() #929
        +---[0.07% 0.0039ms ] javax.servlet.http.HttpServletRequest:setAttribute() #930
        +---[0.15% 0.008ms ] org.springframework.web.servlet.DispatcherServlet:getThemeSource() #931
        +---[0.07% 0.004ms ] javax.servlet.http.HttpServletRequest:setAttribute() #931
        +---[0.25% 0.0134ms ] org.springframework.web.servlet.FlashMapManager:retrieveAndUpdate() #934
        +---[0.14% 0.0078ms ] org.springframework.web.servlet.FlashMap:<init>() #938
        +---[0.08% 0.0041ms ] javax.servlet.http.HttpServletRequest:setAttribute() #938
        +---[0.07% 0.0037ms ] javax.servlet.http.HttpServletRequest:setAttribute() #939
        +---[88.48% 4.8136ms ] org.springframework.web.servlet.DispatcherServlet:doDispatch() #943
        +---[0.09% 0.0051ms ] org.springframework.web.context.request.async.WebAsyncUtils:getAsyncManager() #946
        `---[0.07% 0.0039ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #946
```

当condition-express配置为'#cost > 10'时,由于org.springframework.web.servlet.DispatcherServlet#doService的总耗都小于10ms所以没有可展示的
```text
[arthas@14804]$ trace org.springframework.web.servlet.DispatcherServlet doService '#cost > 10'
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 76 ms, listenerId: 9
```

如果方法调用的次数很多,那么可以用-n参数指定捕捉结果的次数
如果没有配置-n,则最多展示一次.
如下-n 10表示最多展示10次符合条件的调用.
```text
[arthas@14804]$ trace -n 10 org.springframework.web.servlet.DispatcherServlet doService '#cost > 3'
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 75 ms, listenerId: 10
`---ts=2025-06-15 18:08:31.328;thread_name=http-nio-9080-exec-2;id=36;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@4c0e426a
    `---[6.9635ms] org.springframework.web.servlet.DispatcherServlet:doService()
        +---[5.55% 0.3868ms ] org.springframework.web.servlet.DispatcherServlet:logRequest() #911
        +---[0.12% 0.0083ms ] org.springframework.web.util.WebUtils:isIncludeRequest() #916
        +---[0.10% 0.007ms ] org.springframework.web.servlet.DispatcherServlet:getWebApplicationContext() #928
        +---[0.15% 0.0103ms ] javax.servlet.http.HttpServletRequest:setAttribute() #928
        +---[0.04% 0.0028ms ] javax.servlet.http.HttpServletRequest:setAttribute() #929
        +---[0.05% 0.0038ms ] javax.servlet.http.HttpServletRequest:setAttribute() #930
        +---[0.13% 0.009ms ] org.springframework.web.servlet.DispatcherServlet:getThemeSource() #931
        +---[0.05% 0.0033ms ] javax.servlet.http.HttpServletRequest:setAttribute() #931
        +---[0.17% 0.0119ms ] org.springframework.web.servlet.FlashMapManager:retrieveAndUpdate() #934
        +---[0.09% 0.0066ms ] org.springframework.web.servlet.FlashMap:<init>() #938
        +---[0.04% 0.0028ms ] javax.servlet.http.HttpServletRequest:setAttribute() #938
        +---[0.04% 0.003ms ] javax.servlet.http.HttpServletRequest:setAttribute() #939
        +---[90.21% 6.2816ms ] org.springframework.web.servlet.DispatcherServlet:doDispatch() #943
        +---[0.11% 0.0079ms ] org.springframework.web.context.request.async.WebAsyncUtils:getAsyncManager() #946
        `---[0.09% 0.0061ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #946

`---ts=2025-06-15 18:08:32.199;thread_name=http-nio-9080-exec-3;id=37;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@4c0e426a
    `---[6.0743ms] org.springframework.web.servlet.DispatcherServlet:doService()
        +---[0.13% 0.0082ms ] org.springframework.web.servlet.DispatcherServlet:logRequest() #911
        +---[0.08% 0.005ms ] org.springframework.web.util.WebUtils:isIncludeRequest() #916
        +---[0.06% 0.0035ms ] org.springframework.web.servlet.DispatcherServlet:getWebApplicationContext() #928
        +---[0.07% 0.0041ms ] javax.servlet.http.HttpServletRequest:setAttribute() #928
        +---[0.05% 0.0028ms ] javax.servlet.http.HttpServletRequest:setAttribute() #929
        +---[0.04% 0.0024ms ] javax.servlet.http.HttpServletRequest:setAttribute() #930
        +---[0.06% 0.0034ms ] org.springframework.web.servlet.DispatcherServlet:getThemeSource() #931
        +---[0.05% 0.0028ms ] javax.servlet.http.HttpServletRequest:setAttribute() #931
        +---[0.11% 0.0068ms ] org.springframework.web.servlet.FlashMapManager:retrieveAndUpdate() #934
        +---[0.08% 0.005ms ] org.springframework.web.servlet.FlashMap:<init>() #938
        +---[0.05% 0.0028ms ] javax.servlet.http.HttpServletRequest:setAttribute() #938
        +---[0.04% 0.0025ms ] javax.servlet.http.HttpServletRequest:setAttribute() #939
        +---[96.98% 5.8911ms ] org.springframework.web.servlet.DispatcherServlet:doDispatch() #943
        +---[0.11% 0.0066ms ] org.springframework.web.context.request.async.WebAsyncUtils:getAsyncManager() #946
        `---[0.07% 0.0042ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #946

`---ts=2025-06-15 18:08:32.815;thread_name=http-nio-9080-exec-4;id=38;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@4c0e426a
    `---[3.5292ms] org.springframework.web.servlet.DispatcherServlet:doService()
        +---[0.52% 0.0182ms ] org.springframework.web.servlet.DispatcherServlet:logRequest() #911
        +---[0.09% 0.0033ms ] org.springframework.web.util.WebUtils:isIncludeRequest() #916
        +---[0.06% 0.0021ms ] org.springframework.web.servlet.DispatcherServlet:getWebApplicationContext() #928
        +---[0.10% 0.0035ms ] javax.servlet.http.HttpServletRequest:setAttribute() #928
        +---[0.07% 0.0025ms ] javax.servlet.http.HttpServletRequest:setAttribute() #929
        +---[0.09% 0.0032ms ] javax.servlet.http.HttpServletRequest:setAttribute() #930
        +---[0.13% 0.0045ms ] org.springframework.web.servlet.DispatcherServlet:getThemeSource() #931
        +---[0.05% 0.0019ms ] javax.servlet.http.HttpServletRequest:setAttribute() #931
        +---[0.18% 0.0063ms ] org.springframework.web.servlet.FlashMapManager:retrieveAndUpdate() #934
        +---[0.10% 0.0037ms ] org.springframework.web.servlet.FlashMap:<init>() #938
        +---[0.05% 0.0019ms ] javax.servlet.http.HttpServletRequest:setAttribute() #938
        +---[0.05% 0.0018ms ] javax.servlet.http.HttpServletRequest:setAttribute() #939
        +---[95.99% 3.3877ms ] org.springframework.web.servlet.DispatcherServlet:doDispatch() #943
        +---[0.07% 0.0023ms ] org.springframework.web.context.request.async.WebAsyncUtils:getAsyncManager() #946
        `---[0.06% 0.0021ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #946
```

### 动态 trace

```text
打开终端 1,trace org.springframework.web.servlet.DispatcherServlet doService函数,可以看到打印出 listenerId: 11
现在想要深入子函数doDispatch,可以打开一个新终端 2. 使用telnet localhost 3658连接上 arthas,再 trace org.springframework.web.servlet.DispatcherServlet doDispatch时,指定listenerId.
这时终端 2 打印的结果,说明已经增强了一个函数：Affect(class count: 1 , method count: 1),但不再打印更多的结果.
再查看终端 1,可以发现 trace 的结果增加了一层,打印了doDispatch函数里的内容
通过指定listenerId的方式动态 trace,可以不断深入.
另外 watch/tt/monitor等命令也支持类似的功能.
```

```text
[arthas@14804]$ trace  org.springframework.web.servlet.DispatcherServlet doService '#cost > 3'
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 102 ms, listenerId: 11
`---ts=2025-06-15 18:15:11.561;thread_name=http-nio-9080-exec-4;id=38;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@4c0e426a
    `---[7.0167ms] org.springframework.web.servlet.DispatcherServlet:doService()
        +---[10.27% 0.7204ms ] org.springframework.web.servlet.DispatcherServlet:logRequest() #911
        +---[0.27% 0.0186ms ] org.springframework.web.util.WebUtils:isIncludeRequest() #916
        +---[0.14% 0.0097ms ] org.springframework.web.servlet.DispatcherServlet:getWebApplicationContext() #928
        +---[0.25% 0.0176ms ] javax.servlet.http.HttpServletRequest:setAttribute() #928
        +---[0.06% 0.0044ms ] javax.servlet.http.HttpServletRequest:setAttribute() #929
        +---[0.04% 0.0031ms ] javax.servlet.http.HttpServletRequest:setAttribute() #930
        +---[0.16% 0.0109ms ] org.springframework.web.servlet.DispatcherServlet:getThemeSource() #931
        +---[0.07% 0.0046ms ] javax.servlet.http.HttpServletRequest:setAttribute() #931
        +---[0.26% 0.0185ms ] org.springframework.web.servlet.FlashMapManager:retrieveAndUpdate() #934
        +---[0.11% 0.0077ms ] org.springframework.web.servlet.FlashMap:<init>() #938
        +---[0.04% 0.0029ms ] javax.servlet.http.HttpServletRequest:setAttribute() #938
        +---[0.04% 0.0029ms ] javax.servlet.http.HttpServletRequest:setAttribute() #939
        +---[83.57% 5.8636ms ] org.springframework.web.servlet.DispatcherServlet:doDispatch() #943
        +---[0.13% 0.0088ms ] org.springframework.web.context.request.async.WebAsyncUtils:getAsyncManager() #946
        `---[0.10% 0.0072ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #946

--------------------------------------------------------------------------------------------------------------------
        
[arthas@14804]$ trace org.springframework.web.servlet.DispatcherServlet doDispatch --listenerId 11                                                         
Press Q or Ctrl+C to abort.                                                                                                                                
Affect(class count: 1 , method count: 1) cost in 239 ms, listenerId: 11 

--------------------------------------------------------------------------------------------------------------------
        
`---ts=2025-06-15 18:17:01.250;thread_name=http-nio-9080-exec-8;id=42;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@4c0e426a
    `---[4.9927ms] org.springframework.web.servlet.DispatcherServlet:doService()
        +---[7.00% 0.3496ms ] org.springframework.web.servlet.DispatcherServlet:logRequest() #911
        +---[0.14% 0.007ms ] org.springframework.web.util.WebUtils:isIncludeRequest() #916
        +---[0.12% 0.0058ms ] org.springframework.web.servlet.DispatcherServlet:getWebApplicationContext() #928
        +---[0.15% 0.0073ms ] javax.servlet.http.HttpServletRequest:setAttribute() #928
        +---[0.03% 0.0017ms ] javax.servlet.http.HttpServletRequest:setAttribute() #929
        +---[0.03% 0.0015ms ] javax.servlet.http.HttpServletRequest:setAttribute() #930
        +---[0.12% 0.0061ms ] org.springframework.web.servlet.DispatcherServlet:getThemeSource() #931
        +---[0.04% 0.0018ms ] javax.servlet.http.HttpServletRequest:setAttribute() #931
        +---[0.26% 0.0129ms ] org.springframework.web.servlet.FlashMapManager:retrieveAndUpdate() #934
        +---[0.12% 0.0061ms ] org.springframework.web.servlet.FlashMap:<init>() #938
        +---[0.04% 0.0018ms ] javax.servlet.http.HttpServletRequest:setAttribute() #938
        +---[0.04% 0.0018ms ] javax.servlet.http.HttpServletRequest:setAttribute() #939
        +---[88.89% 4.438ms ] org.springframework.web.servlet.DispatcherServlet:doDispatch() #943
        |   `---[99.55% 4.4181ms ] org.springframework.web.servlet.DispatcherServlet:doDispatch()
        |       +---[0.13% 0.0057ms ] org.springframework.web.context.request.async.WebAsyncUtils:getAsyncManager() #1005
        |       +---[0.29% 0.0129ms ] org.springframework.web.servlet.DispatcherServlet:checkMultipart() #1012
        |       +---[11.47% 0.5069ms ] org.springframework.web.servlet.DispatcherServlet:getHandler() #1016
        |       +---[0.17% 0.0073ms ] org.springframework.web.servlet.HandlerExecutionChain:getHandler() #1023
        |       +---[0.32% 0.0141ms ] org.springframework.web.servlet.DispatcherServlet:getHandlerAdapter() #1023
        |       +---[0.09% 0.0039ms ] javax.servlet.http.HttpServletRequest:getMethod() #1026
        |       +---[0.04% 0.0016ms ] org.springframework.web.servlet.HandlerExecutionChain:getHandler() #1029
        |       +---[0.09% 0.0041ms ] org.springframework.web.servlet.HandlerAdapter:getLastModified() #1029
        |       +---[0.12% 0.0051ms ] org.springframework.web.context.request.ServletWebRequest:<init>() #1030
        |       +---[0.20% 0.0089ms ] org.springframework.web.context.request.ServletWebRequest:checkNotModified() #1030
        |       +---[0.18% 0.0079ms ] org.springframework.web.servlet.HandlerExecutionChain:applyPreHandle() #1035
        |       +---[0.04% 0.0019ms ] org.springframework.web.servlet.HandlerExecutionChain:getHandler() #1040
        |       +---[83.54% 3.6911ms ] org.springframework.web.servlet.HandlerAdapter:handle() #1040
        |       +---[0.30% 0.0132ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #1042
        |       +---[0.20% 0.009ms ] org.springframework.web.servlet.DispatcherServlet:applyDefaultViewName() #1046
        |       +---[0.23% 0.0101ms ] org.springframework.web.servlet.HandlerExecutionChain:applyPostHandle() #1047
        |       +---[0.48% 0.0214ms ] org.springframework.web.servlet.DispatcherServlet:processDispatchResult() #1057
        |       `---[0.06% 0.0025ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #1067
        +---[0.07% 0.0034ms ] org.springframework.web.context.request.async.WebAsyncUtils:getAsyncManager() #946
        `---[0.06% 0.0028ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #946        
```