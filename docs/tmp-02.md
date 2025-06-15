# arthas常用命令

## trace

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


```text
[arthas@15052]$ trace org.springframework.web.servlet.DispatcherServlet doService '#cost > 10'

`---ts=2025-06-15 16:28:50.124;thread_name=http-nio-9080-exec-6;id=38;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@16ade133
    `---[5.6955ms] org.springframework.web.servlet.DispatcherServlet:doService()
        +---[11.65% 0.6637ms ] org.springframework.web.servlet.DispatcherServlet:logRequest() #911
        +---[0.20% 0.0113ms ] org.springframework.web.util.WebUtils:isIncludeRequest() #916
        +---[0.15% 0.0083ms ] org.springframework.web.servlet.DispatcherServlet:getWebApplicationContext() #928
        +---[0.30% 0.0169ms ] javax.servlet.http.HttpServletRequest:setAttribute() #928
        +---[0.09% 0.0049ms ] javax.servlet.http.HttpServletRequest:setAttribute() #929


[arthas@15052]$ trace org.springframework.web.servlet.DispatcherServlet doService

`---ts=2025-06-15 16:29:58.131;thread_name=http-nio-9080-exec-7;id=39;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@16ade133
    `---[5.7001ms] org.springframework.web.servlet.DispatcherServlet:doService()
        +---[7.06% 0.4025ms ] org.springframework.web.servlet.DispatcherServlet:logRequest() #911
        +---[0.21% 0.0121ms ] org.springframework.web.util.WebUtils:isIncludeRequest() #916
        +---[0.16% 0.0094ms ] org.springframework.web.servlet.DispatcherServlet:getWebApplicationContext() #928
        +---[0.24% 0.0134ms ] javax.servlet.http.HttpServletRequest:setAttribute() #928
        +---[0.07% 0.004ms ] javax.servlet.http.HttpServletRequest:setAttribute() #929
        +---[0.07% 0.0041ms ] javax.servlet.http.HttpServletRequest:setAttribute() #930
        +---[0.17% 0.0095ms ] org.springframework.web.servlet.DispatcherServlet:getThemeSource() #931
        +---[0.07% 0.004ms ] javax.servlet.http.HttpServletRequest:setAttribute() #931
        +---[0.28% 0.016ms ] org.springframework.web.servlet.FlashMapManager:retrieveAndUpdate() #934
        +---[0.16% 0.0093ms ] org.springframework.web.servlet.FlashMap:<init>() #938
        +---[0.08% 0.0044ms ] javax.servlet.http.HttpServletRequest:setAttribute() #938
        +---[0.06% 0.0034ms ] javax.servlet.http.HttpServletRequest:setAttribute() #939
        +---[88.88% 5.0661ms ] org.springframework.web.servlet.DispatcherServlet:doDispatch() #943
        +---[0.09% 0.005ms ] org.springframework.web.context.request.async.WebAsyncUtils:getAsyncManager() #946
        `---[0.08% 0.0048ms ] org.springframework.web.context.request.async.WebAsyncManager:isConcurrentHandlingStarted() #946

```