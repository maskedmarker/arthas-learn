# arthas的使用样例

```text

java -jar arthas-boot.jar


trace org.springframework.web.servlet.DispatcherServlet doService


trace -E org.example.learn.* .* 

curl http://localhost:9080/myapp/findAllUsers
trace org.springframework.web.servlet.DispatcherServlet doService -n 1000

trace org.springframework.web.servlet.DispatcherServlet doService '#cost > 1' --listenerId myTrace


```


样例
```text
trace -E org.example.learn..* .* -n 5 -#cost 50

参数说明：
-E：启用正则
org.example.learn..*：匹配该包及其所有子包中的类
.*：匹配所有方法名
-n 5：最多打印 5 次调用链
-#cost 50：只显示执行时间超过 50ms 的调用
```

