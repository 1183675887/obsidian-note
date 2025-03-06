---
title: 简单的HTTP协议
date: 2020-05-15 10:00:25
tags:
  - HTTP
  - 图解HTTP
---
本章将针对HTTP 协议结构进行讲解，主要使用HTTP/1.1版本。学完这章，想必大家就能理解HTTP 协议的基础了。
 
<!--more-->

## 2.1 HTTP协议用于客户端和服务器端之间的通信

HTTP 协议和TCP/IP协议族内的其他众多的协议相同，用于客户端和服务器之间的通信。
请求访问文本或图像等资源的一端称为客户端，而提供资源响应的一端称为服务器端。
![](https://raw.githubusercontent.com/1183675887/obsidian-picture-bed/main/104_%E5%9B%BE%E8%A7%A3HTTP/013.png)

在两台计算机之间使用HTTP 协议通信时，在一条通信线路上必定有一端是客户端，另一端则是服务器端。

有时候，按实际情况，两台计算机作为客户端和服务器端的角色有 可能会互换。但就仅从一条通信路线来说，服务器端和客户端的角色是确定的，而用HTTP协议能够明确区分哪端是客户端，哪端是服务器端。

## 2.2 通过请求和响应的交换达成通信

![](https://raw.githubusercontent.com/1183675887/obsidian-picture-bed/main/104_图解HTTP/014.png)

HTTP协议规定，请求从客户端发出，最后服务器端响应该请求并返回。换句话说，肯定是先从客户端开始建立通信的，服务器端在没有接收到请求之前不会发送响应。下面，我们来看一个具体的示例。

![](https://raw.githubusercontent.com/1183675887/obsidian-picture-bed/main/104_图解HTTP/015.png)

下面则是从客户端发送给某个HTTP服务器端的请求报文中的内容。

![](https://raw.githubusercontent.com/1183675887/obsidian-picture-bed/main/104_图解HTTP/016.png)

起始行开头的GET表示请求访问服务器的类型，称为方法(method) 。随后的字符串/index.htm指明了请求访问的资源对象，也叫做请求URI(request-URI)。最后的HTTP/1.1,即HTTP的版本号，用来提示客户端使用的HTTP协议功能。

综合来看，这段请求内容的意思是：请求访问某台HTTP服务器上的/index.htm页面资源 。请求报文是由请求方法、请求URI 、协议版本、可选的请求首部字段和内容实体构成的。

![](https://raw.githubusercontent.com/1183675887/obsidian-picture-bed/main/104_图解HTTP/017.png)

请求首部字段及内容实体稍后会作详细说明。接下来，我们继续讲解。接收到请求的服务器，会将请求内容的处理结果以响应的形式返回。

![](https://raw.githubusercontent.com/1183675887/obsidian-picture-bed/main/104_图解HTTP/018.png)

在起始行开头的HTTP/1.1表示服务器对应的HTTP版本。

紧挨着的200OK表示请求的处理结果的状态码(status code)和原因短语(reason-phrase) 。下一行显示了创建响应的日期时间，是首部字段 (header field)内的一个属性。

接着以一空行分隔，之后的内容称为资源实体的主体(entity body)。

响应报文基本上由协议版本、状态码(表示请求成功或失败的数字代码)、用以解释状态码的原因短语、可选的响应首部字段以及实体主体构成。稍后我们会对这些内容进行详细说明。

![](https://raw.githubusercontent.com/1183675887/obsidian-picture-bed/main/104_图解HTTP/019.png)

