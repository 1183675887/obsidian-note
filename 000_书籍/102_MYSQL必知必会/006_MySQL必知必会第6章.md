---
title: MySQL必知必会4-9章
date: 2020-08-12 10:00:50
tags:
  - MySQL
  - MYSQL必知必会
---

这些章讲解检索数据，排序检索数据，过滤数据，数据过滤，用通配符过滤，用正则表达式进行搜索。

<!--more-->

## 第六章，过滤数据

### 6.1.使用WHRER子句

数据库表一般包含大量的数据，很少需要检索表中所有行。通常只会根据特定操作或报告的需要提取数据的子集。只检索所需数据需要制定搜索条件，搜索条件也称为过滤条件。

在SELECT语句中，数据根据WHERE子句中指定的搜索条件进行过滤。WHERE子句在表名(FROM子句)之后给出。如下所示：

```
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE prod_price = 2.50;
```

分析：这条语句从products表中检索两个列，但不返回所有行，只返回prod_price值为2.50的行，，如下所示：

| prod_name | prod_price |
| --------- | ---------- |
| Carrots   | 2.50       |
| TNT       | 2.50       |

**WHERE子句的位置：在同时使用ORDER BY和WHERE子句时，应该让ORDER BY位于WHERE之后，否则将会产生错误。**

### 6.2.WHERE子句操作符

MySQL支持以下的条件操作符。

| 操作符  | 说明               |
| ------- | ------------------ |
| =       | 等于               |
| <>      | 不等于             |
| !=      | 不等于             |
| <       | 小于               |
| <=      | 小于等于           |
| >       | 大于               |
| >=      | 大于等于           |
| BETWEEN | 在指定的两个值之间 |

#### 6.2.1.检查单个值

我们已经看到了测试相等的例子。再来看一个类似的例子。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE prod_name = 'fuses';
```

输出：

| prod_name | prod_price |
| --------- | ---------- |
| fuses     | 3.42       |

分析：检查WHERE  prod_name = 'fuses'语句，它返回prod_name的值为Fuses的一行。MySQL在执行匹配时默认不区分大小写，所以fuses与Fuses匹配。

再来看其他例子。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE prod_price < 5;
```

输出：

| prod_name      | prod_price |
| -------------- | ---------- |
| .5  ton  anvil | 4.49       |
| 1  ton  anvil  | 2.50       |

这个例子是列出价格小于5美元的所有产品。

#### 6.2.3.范围值检查

为了检查某个范围的值，可使用BETWEEN操作符。其语法与其他WHERE子句的操作符稍有不同，因为它要两个值，即范围的开始值和结束值。

下面的例子说明如何使用BETWEEN操作符，它检索价格在9美元和10美元之间的所有产品。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE prod_price BETWEEN 9 AND 10;
```

输出：

| prod_name    | prod_price |
| ------------ | ---------- |
| 1  ton anvil | 9.99       |
| Bird         | 10.00      |

在使用BETWEEB时，必须指定两个值-----所需范围的底端值和高端值。这两个值必须用AND关键字分隔。BETWEEN匹配范围中所有的值，包括指定的开始值和结束值。

#### 6.2.4.空值检查

在创建表时，表设计人员可以指定其其中的列是否可以不包含值。在一个列不包含值时，称其为包含空值NULL。

**NULL  无值(no value)：它与字段包含0，空字符串或仅仅包含空格不同。**

SELECT语句由一个特殊的WHERE语句，可用来检查具有NULL值的列。这个WHERE子句就是IS NULL子句。语法如下：

```mysql
输入：  SELECT prod_name
       FROM   products;
       WHERE prod_price IS NULL;
```

因为prod_price值没有一个为NULL，所以不返回。