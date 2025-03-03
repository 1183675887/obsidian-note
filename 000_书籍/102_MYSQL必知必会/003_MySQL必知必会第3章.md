---
title: MySQL必知必会1-3章
date: 2020-08-10 14:15:35
tags:
  - MySQL
  - MYSQL必知必会
---

这些章讲解使用MySQL。

<!--more-->

## 第三章，使用MySQL

### 3.1.连接

MySQL与所有客户机---服务器DBMS一样，要求在能执行命令之前登陆到DBMS。登录名可以与网络登录名不相同(假定你使用网络)。MySQL在内部保存自己的用户列表，并且把每个用户与各种权限关联起来。

为了连接到MySQL,需要以下信息。

1.主机名(或计算机名)：如果连接到到本地MySQL服务器，为localhost。

2.端口(如果使用默认端口3303之外的端口)。

3.一个合法的用户名。

4.用户口令(如果需要的话)

在连接之后，你就可以访问你的登录名能够访问的任意数据库和表了。(登陆，访问控制和安全可参考第28章)。

### 3.2.选择数据库

在你最初连接到MySQL时，没有任何数据库打开供你使用。在你能执行任意数据库操作前，需要选择一个数据库。为此可使用USE关键字。

关键字(key word)：作为MySQL语言组成部分的一个保留字。绝不要用关键字命名一个表或列。

例如，为了使用crashcourse数据库，应该输入以下内容

```mysql
输入： USE  crashcourse
输出： Database changed
```

分析：USE语句并不返回任何结果。依赖于使用的客户机，显示某种形式的通知。例如，这里显示出的Database changed消息是mysql命令行实用程序在数据库选择成功后显示的。

记住，必须先使用USE打开数据库，才能读取其中的数据。

### 3.3.了解数据库和表

数据库，表，列，用户，权限等的信息被存储在数据库中和表中(MySQL使用MySQL来存储这些信息)。不过，内部的表一般不直接访问。可用MySQL的SHOW命令来显示这些消息(MySQL从内部表中提取这些信息)。

输入： SHOW   DATABASES

输出：

| Database           |
| :----------------- |
| information_schema |
| crashcourse        |
| mysql              |
| forta              |
| coldfusion         |
| flex               |
| test               |

分析：SHOW  DATABASES.返回可以数据库的一个列表。包含在这个列表中的可能是MySQL内部使用的数据库(如例子中的mysql和information_schema)。当然，你自己的数据库列表可能看上去与这里的不一样。

为了获得一个数据库内的表的列表，使用SHOW TABLES。如下所示

输入：  SHOW   TABLES

输出：

| Tables_in_crashcourse |
| --------------------- |
| customers             |
| orderitems            |
| orders                |
| products              |
| productnotes          |
| vendors               |

分析：SHOW   TABLES。返回当前选择的数据库内可用表的列表。

输入：  SHOW  COLUMNS  FROM  customers

输出：

| Field        | Type      | Null | key  | Dedault | Extra          |
| ------------ | --------- | ---- | ---- | ------- | -------------- |
| cust_id      | int(11)   | NO   | PRI  | NULL    | auto_increment |
| cust_name    | char(50)  | NO   |      |         |                |
| cust_address | char(50)  | YES  |      | NULL    |                |
| cust_city    | char(50)  | YES  |      | NULL    |                |
| cust_state   | char(5)   | YES  |      | NULL    |                |
| cust_zip     | char(10)  | YES  |      | NULL    |                |
| cust_country | char(50)  | YES  |      | NULL    |                |
| cust_contact | char(50)  | YES  |      | NULL    |                |
| cust_email   | char(255) | YES  |      | NULL    |                |

分析：SHOW  COLUMS要求给出一个表名(这个例子中的FROM customers)，它对每个字段返回一行，行中包含字段名，数据类型，是否允许NULL，键信息，默认值以及其他信息(如字段cust_id的auto_increment)。

**什么是自动增量：某些表列需要唯一值。在每个行添加到表中时，MySQL可以自动地为每个行分配为下一个可以编号，不用在添加一行时手动分配唯一值(这样做必须记住最后一次使用的值)。这个功能就是所谓的自动增量。如果需要它，则必须在用CREATE语句创建表时把它作为表定义的组成部分。**

**DESCRIBE语句：MySQL支持用DESCRIBE作为SHOW  COLUMNS  FROM的一种快捷方式。换句话说，DESCRIBE  customers是SHOW  COLUMNS  FROM  customers的一种快捷方式。**

所支持的其他SHOW语句还有：

1.SHOW STATUS：用来显示广泛的服务器状态信息。

2.SHOW  CREATE  DATABASE和SHOW  CREATE  TABLE：分别用来显示创建特定数据库或表的MySQL语句。

3.SHOW  GRANTS：用来显示授权用户(所有用户或特定用户)的安全权限。

4.SHOW  ERRORS  和SHOW  WARNINGS：用来显示服务器错误或警告信息。

值得注意的是，客户机应用程序使用与这里相同的MySQL语句。