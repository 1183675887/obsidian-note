---
title: MySQL必知必会4-9章
date: 2020-08-12 10:00:50
tags:
  - MySQL
  - MYSQL必知必会
---

这些章讲解检索数据，排序检索数据，过滤数据，数据过滤，用通配符过滤，用正则表达式进行搜索。

<!--more-->

## 第四章，检索数据

### 4.1.SELECT语句

SELECT语句，它的用途是从一个或多个表中检索信息。

### 4.2.检索单个列

我们将从简单的SQL  SELECT语句开始介绍。此语句如下所示。

```mysql
输入:   SELECT  prod_name
        FROM  products;
```

分析：上述语句利用SELECT语句从products表中检索一个名为prod_name的列。所需的列名在SELECT关键字之后给出，REOM关键字之处从其中检索数据的表名。此语句的输出如下所示：

| prod_name      |
| -------------- |
| .5  ton  anvil |
| 1  ton   anvil |
| 2  ton   anvil |

如上一条的一个简单SELECT语句将返回表中所有行。数据没有过滤(过滤将得到结果集的一个子集)，也没有排序。

**结束SQL语句：多条SQL语句必须以分号(;)分隔。MySQL如同多数DBMS一样，不需要在单条SQL语句后加分号。但特定的DBMS可能必须在单条SQL语句后加上分号。当然，如果愿意可以总是加上分号。事实上，即使不一定需要，但加上分号肯定没有坏处。如果你使用的是mysql命令行，必须加上分号来结束SQL语句。**

**SQL语句和大小写：SQL语句不区分大小写，因此SELECT与select是相同的。许多开发人员喜欢对所有SQL关键字使用大写，而对所有列和表名使用小写，这样做使代码更易于阅读和调试。最佳方式是按照大小写的惯例，切使用时保持一致。**

**使用空格：在处理SQL语句时，其中所有空格都会忽略。SQL语句可以在一行上给出，也可以分成许多行。多数开发人员认为将SQL语句**

**分成多行更容易阅读和调试。**

### 4.3.检索多个列

想从一个表中检索多个列，使用相同的SELECT语句，唯一的不同是必须在SELECT关键字后面给出多个列名，列名之间必须以逗号分隔。

下面的SELECT语句从products表中选择3列。

```mysql
输入：  SELECT  prod_id, prod_name, prod_price
       FROM   products;
```

分析：与前一个例子一样，这条语句使用SELECT语句从表products中选择数据。在这个例子中，指定了3个列名，列名之间用逗号分隔。

此语句的输出如下：

| prod_id | prod_name      | prod_price |
| ------- | -------------- | ---------- |
| ANVO1   | .5  ton  anvil | 5.99       |
| ANVO2   | 1   ton  anvil | 9.99       |
| ANVO3   | 2   ton  anvil | 14.99      |
| OL1     | Oil  can       | 8.99       |

**数据表示：从上述输出可以看到，SQL语句一般返回原始的，无格式的数据。数据的格式化是一个表示问题，而不是一个检索问题。**

### 4.4.检索所有列

SELECT语句还可以检索所有的列而不必逐个列出它们。这可以通过在实际列名的位置使用星号(*)通配符来达到。如下所示。

```mysql
输入：  SELECT  *
       FROM   products;
```

分析：如果给定一个通配符(*)，则返回表中所有列。列的顺序一般是列在表定义中出现的顺序。

**使用通配符：一般，除非你确实需要表中的每个列，否则最好别使用*通配符。虽然使用通配符可能会使你自己省事，不用明确列出所需列，但检索不需要的列通常会降低检索和应用程序的性能。**

### 4.5.检索不同的行

如果你不想要每个值都出现。例如，加入你想得出products表中产品的所有供应商ID。

```mysql
输入： SELECT  vend_id
      FROM  products;
```

输出

| vend_id |
| ------- |
| 1001    |
| 1001    |
| 1002    |
| 1002    |

解决办法是使用DISTINCT关键字，顾名思义，此关键字指示MySQL只返回不同的值。

```
输入： SELECT  DISTINCT   vend_id
      FROM  porducts;
```

分析：SELECT  DISTINCT  vend_id告诉MySQL只返回不同(唯一)的vend_id行。输入如下。

| vend_id |
| ------- |
| 1001    |
| 1002    |

**不能部分使用DISTINCT：DISTINCT关键字应用于所有列而不仅是前置它的列。如果给出SELECT  DISTINCT  vend_id，prod_price，除非指定的两个列都不同，否则所有行都将被检索出来。**

### 4.6.限制结果

SELECT语句返回所有匹配的行，它们可能是指定表中的每个行。为了返回第一行或前几行，可使用LIMIT子句。下面举个例子：

```mysql
输入：  SELECT prod_name
       FROM   products
       LIMIT  2;
```

 分析：此语句使用SELECT语句检索单个列。LIMIT 2指示MySQL返回不多于2行。此语句的输入如下所示：

| prod_name      |
| -------------- |
| .5  ton  anvil |
| 1  ton  anvil  |

为得出下一个2行，可指定检索的开始行和行数，如下所示：

```mysql
输入：  SELECT prod_name
       FROM   products
       LIMIT  2,2;
```

分析：LIMIT  2,2指示MySQL返回从行2开始的2行。第一个数为开始位置，第二个数为要检索的行数。此语句的输出如下。

| prod_name      |
| -------------- |
| 2   ton  anvil |
| Oil  can       |

所以，带一个值的LIMIT总数从第一行开始，给出的数为返回的行数。带二个值的LIMIT可以指定从行号为第一个值的位置开始。

**行0：检索出来的第一行为行0而不是行1.因此LIMIT 1,1将检索出第二行而不是第一行**

### 4.7.使用完全限定的表名

迄今为止使用的SQL例子只通过列名引用列。可能会使用完全限定的名字来引用列(同时使用表名和列字)。看以下例子：

```mysql
输入：  SELECT  products.prod_name
       FROM  products;
```

这里指定了一个完全限定的列名。

表名也可以是完全限定的。如下所示：

```mysql
输入：  SELECT  products.prod_name
       FROM  crashcourse.products
```

有一些情形需要完全限定名。现在，需要注意这个语法，以便在遇到时知道它的作用。