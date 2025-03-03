---
title: MySQL必知必会4-9章
date: 2020-08-12 10:00:50
tags:
  - MySQL
  - MYSQL必知必会
---

这些章讲解检索数据，排序检索数据，过滤数据，数据过滤，用通配符过滤，用正则表达式进行搜索。

<!--more-->

## 第九章，用正则表达式进行搜索

### 9.1.正则表达式介绍

正则表达式是用来匹配文本的特殊的串(字符集合)。

### 9.2.使用MySQL正则表达式

MySQL用WHERE子句对正则表达式提供了初步的支持，允许你指定正则表达式，过滤SELECT检索出的数据。

#### 9.2.1.基本字符匹配

我们个一个非常简单例子开始，下面的语句检索列prod_name包含文本1000的所有行。

```mysql
输入：  SELECT prod_name
       FROM   products;
       WHERE   prod_name REGEXP '.000'
       ORDER BY prod_name;
```

输出：

| prod_name    |
| ------------ |
| JetPack 1000 |

分析：REGEXP后跟的东西为正则表达式。这里使用了正则表达式'.000'，是正则表达式语言中一个特殊的字符。它表示匹配任一一个字符。

**LIKE与REGEXP：LIKE匹配整个列，REGEXP在列值内进行匹配。**

**匹配不区分大小写：MySQL中的正则表达式匹配不区分大小写。**

#### 9.2.2.进行OR匹配

```mysql
输入：  SELECT prod_name
       FROM   products;
       WHERE   prod_name REGEXP '1000|2000'
       ORDER BY prod_name;
```

输出：

| prod_name    |
| ------------ |
| JetPack 2000 |
| JetPack 1000 |

分析：使用正则表达式1000|2000。|为正则表达式的OR操作符。它表示匹配其中之一。

#### 9.2.3.匹配几个字符之一

匹配任何单一字符。如果你想匹配特定的字符，可通过指定一组用[和]括起来的字符来完成。如下所示。

```mysql
输入：  SELECT prod_name
       FROM   products;
       WHERE   prod_name REGEXP '[123] Ton'
       ORDER BY prod_name;
```

输出：

| prod_name     |
| ------------- |
| 1  ton anvil  |
| 2  ton  anvil |

分析：这里，使用了正则表达式[123] Ton，[123]定义一组字符，它的意思是匹配1或2或3。

#### 9.2.4.匹配范围

集合可用来定义要匹配的一个或多个字符。例如下面的集合将匹配数字0-9。[0-9]。

#### 9.2.5.匹配特殊字符

为了匹配特殊字符，必须用\\为前导，\\-表示查找-。

#### 9.2.6.匹配字符类

涉及太多，自行百度。

#### 9.2.8.定位符

