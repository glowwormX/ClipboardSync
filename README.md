# ClipboardSync
基于netty的剪切板同步工具

1. 运行环境 java8

2. 客户端根据-host和-group绑定服务器和用户组   
``java -jar client.jar -host {ip} -group xqw123456789``
3. 服务端接收内容，同步至相同group下的客户端，监听端口23146
`` java -jar server.jar ``

