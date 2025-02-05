---
title: MySQL必知必会10-14章
date: 2020-08-15 11:21:40
tags: MySQL
---

这些章讲解创建计算字段，使用数据处理函数，汇总数据，分组数据，使用子查询。

<!--more-->

## 第十章，创建计算字段

### 10.1.计算字段

假若存储在表中的数据都不是应用程序所需要的。我们需要直接从数据库中检索出转换，计算或格式化过的数据：而不是检索出数据，然后再在客户机应用程序或报告程序中重新格式化。

这就是计算字段发挥作用的所在了。与前面各章介绍过的列不同，计算字段并不实际存在于数据库表中。计算字段是运行时在SELECT语句内创建的。

**字段(field)：基本上与列的意思相同，经常互换使用，不过数据库列一般称为列，而术语字段通常在计算字段的连接上。**

重要的是要注意到，只有数据库知道SELECT语句中哪些是实际的表列，哪些列是计算字段。荣客户机(如应用程序)的角度来看，计算字段的数据是以其他列的数据相同的方式返回。

### 10.2.拼接字段

为了说明如何使用计算字段，举一个创建由两列组成的标题的简单例子。

vendors表包含供应商名和位置信息。假如要生成一个供应商报表，需要在供应商的名字中按照name(location)这样的格式列出供应商的位置。此报表需要单个值，而表中数据存储在两个列vend_name和vend_country中，此外，需要用括号将vend_country括起来，这些东西都没有明确存储在数据库表中。

**拼接：将值联结到一起构成单个值。**

解决办法是把两个列拼接起来。在MySQL的SELECT语句中，可使用Concat()函数来拼接两个列。

**MySQL的不同之处：多数DBMS使用+或||来实现拼接MySQL则使用Concat()函数来实现。**

```mysql
输入： SELECT Concat(vend_name, '(',vend_country, ')')
      FROM  vendors
      ORDER BY vend_namme
```

输出：

| Concat(vend_name, '(',vend_country')') |
| -------------------------------------- |
| ACME  (USA)                            |
| Anvils  R  Us  (USA)                   |

分析：Concat()拼接串，即把多个串连接起来形成一个较长的串。Concat()需要一个或多个指定的串，各个串之间用逗号分隔。上面的SELECT语句连接以下4个元素。

1.存储在vend_name列中的名字

2.包含一个空格和一个左圆括号的串

3.存储在vend_country列中的国家

4.包含一个右圆括号的串

从上述输出可以看出，SELECT语句返回包含上述4个元素的单个列(计算字段)。

在第八章提到通过删除数据右侧多余的空格来整理数据，这可以使用MySQL的RTrim()函数来完成。如下所示。

```mysql
输入： SELECT Concat(RTrim(vend_name), '(', RTrim(vend_country, ')')
      FROM  vendors
      ORDER BY vend_name
```

分析：RTrim()函数去掉右边的所有空格。通过使用RTrim()，各个列都进行了整理。

Trim函数：MySQL处理支持RTrim()去掉右边的空格，还支持LTrim()去掉左边的空格以及Trim()去掉右边的空格。

##### 使用别名

SQL支持列别名。别名是一个字段或值的替换名。别名用AS关键字赋予。看下面的例子。

```mysql
输入： SELECT Concat(RTrim(vend_name), '(', RTrim(vend_country, ')')   AS vend_title
      FROM  vendors
      ORDER BY vend_namme;
```

输出：

| vend_title           |
| -------------------- |
| ACME  (USA)          |
| Anvils  R  Us  (USA) |

分析：现在列名为vend_title，，任何客户机应用都可以按名引用这个列，就是它是一个实际的表列一样。

**别名的其他用途：别名还有其他用途。常见的用途包括在实际的表列名包含不符合规定的字符(如空格)时重新命名它，在原来的名字含混或容易误解时扩充它。**

### 10.3.执行算术计算

计算字段的另一常见用途是对检索出的数据进行算术计算。举一个例子，orders表包含收到的所有订单，orderitems表包含每个订单中的各项物品。下面的例子检索订单号20005中的所有物品。

```mysql
输入： SELECT prod_id, quantity, item_price
      FROM orderitems
      WHERE order_num = 20005;
```

输出：

| prod_id | quantity | item_price |
| ------- | -------- | ---------- |
| ANV01   | 10       | 5.99       |
| ANV02   | 3        | 9.99       |

item_price列包含订单中每项物品的单价。如下汇总物品的价格(单价乘以订购数量)。

```mysql
输入： SELECT prod_id, quantity, item_price  quantity*item_price AS expanded_price
      FROM orderitems
      WHERE order_num = 20005;
```

输出：

| prod_id | quantity | item_price | expanded_price |
| ------- | -------- | ---------- | -------------- |
| ANV01   | 10       | 5.99       | 59.90          |
| ANV02   | 3        | 9.99       | 29.97          |

## 第十一章，使用数据处理函数

### 11.2.使用函数

大多数SQL实现支持以下类型的函数。

1.用于处理文本串(如删除或填充值，转换值为大写或小写)的文本函数。

2.用于在数值数据上进行算术操作(如返回绝对值，进行代数运算)的数值函数。

3.用于处理日期和时间值并从这些值中提取特定成分(例如，返回两个日期之差，检查日期有效性等)的日期和时间函数。

4.返回DBMS正使用的特殊信息(如返回用户登录信息，检查版本细节)的系统函数。

#### 11.2.1.文本处理函数

上一章中我们已经看到了文本处理函数的例子，其他的用法也差不多，下面是常见的文本处理函数。

| 函数        | 说明              |
| ----------- | ----------------- |
| Left()      | 返回串左边的字符  |
| Length()    | 返回串的长度      |
| Locate()    | 找出串的一个子串  |
| Lower()     | 将串转换为小写    |
| LTrom()     | 去掉串左边的空格  |
| Right()     | 返回串右边的字符  |
| RTrim()     | 去掉串右边的空格  |
| Soundex()   | 返回串的SOUNDEX值 |
| SubString() | 返回子串的字符    |
| Upper()     | 将串转换为大写    |

#### 12.2.日期和时间处理函数

下面列出了常用的日期和时间处理函数。

| 函数         | 说明                           |
| ------------ | ------------------------------ |
| AddDate()    | 增加一个日期(天，周等)         |
| AddTime()    | 增加一个时间(时，分等)         |
| CurDate()    | 返回当前日期                   |
| CurTime()    | 返回当前时间                   |
| Date()       | 返回日期时间的日期部分         |
| DateDiff()   | 计算两个日期之差               |
| Date_Add()   | 高度灵活的日期运算函数         |
| Day_Format() | 返回一个格式化的日期或时间串   |
| Day()        | 返回一个日期的天数部分         |
| DayOfWeek()  | 对于一个日期，返回对于的星期几 |
| Hour()       | 返回一个日期的小时部分         |
| Minute()     | 返回一个日期的分钟部分         |
| Month()      | 返回一个日期的月份部分         |
| Now()        | 返回当前日期和时间             |
| Second()     | 返回一个时间的秒部分           |
| Time()       | 返回一个日期时间的时间部分     |
| Year()       | 返回一个日期的年份部分         |

**应该总是使用4位数字的年份：日期格式为yyyy-mm-dd。**

如果你想检索2005年9月下的所有订单，

```mysql
输入： SELECT cust_id, order_num
      FROM orders
      WHERE Date(order_data) BETWEEN '2005-09-01'  AND  '2005-09-30';
```

输出：

| cust_id | order_num |
| ------- | --------- |
| 10001   | 20005     |
| 10003   | 20006     |

分析：其中BETWEEN操作符用来把2005-09-01和2005-09-30定义为一个要匹配的日期范围。

#### 11.2.3.数值处理函数。

下面列出了常用的数值处理函数。

| 函数   | 说明                 |
| ------ | -------------------- |
| Abs()  | 返回一个数的绝对值   |
| Cos()  | 返回一个角度的余弦值 |
| Exp()  | 返回一个数的指数值   |
| Mod()  | 返回除操作的余数     |
| Pi()   | 返回圆周率           |
| Rand() | 返回一个随机数       |
| Sin()  | 返回一个角度的正弦   |
| Sqrt() | 返回一个数的平方根   |
| Tan()  | 返回一个角度的正切   |

## 第十二章，汇总数据

### 12.1.聚集函数

我们经常需要汇总数据而不用它们实际检索出来，为此MySQL提供了专门的函数。使用这些函数，MySQL查询可用于检索数据，以便分析和报表生成。这种类型的检索例子有以下几种。

1.确定表中行数(或者满足某个条件或包含某个特定值的行数)。

2.获得表中行组的和。

3.找出表列(或所有行或特定的行)的最大值，最小值和平均值。

为方便这种类型的检索，MySQL给出了5个聚集函数。

**聚集函数：允许在行组上，计算和返回单个值的函数。**

| 函数    | 说明             |
| ------- | ---------------- |
| AVG()   | 返回某列的平均值 |
| COUNT() | 返回某列的行数   |
| MAX()   | 返回某列的最大值 |
| MIN()   | 返回某列的最小值 |
| SUM()   | 返回某列值之和   |

#### 12.1.AVG()函数

下面使用AVG()返回products表中所有产品的平均价格。

```mysql
输入： SELECT AVG(prod_prices)  AS  avg_price
      FROM  products
```

输出：

| avg_price |
| --------- |
| 16.133571 |

分析：此SELECT语句返回值avg_price，它包含products表中所有产品的平均价格。

**只用于单个例：AVG()只能用来确定特定数值列的平均值，而且列名必须作为函数参数给出。为了获得多个列的平均值，必须使用多个AVG()函数。**

**NULL值：AVG()函数忽略列值为NULL的行。**

#### 12.1.2.COUNT()函数

COUNT()函数有两种使用模式。

1.使用COUNT(*)对表中行的数目进行计数，不管列中包含的是空值(NULL)还是非空值。

2.使用COUNT(column)对特定列中具有值的行进行计数，忽略NULL值。

**NULL值：如果指定列名，则指定列的值为空的行被COUNT()函数忽略，但如果COUNT()函数中用的是星号(*)，则不省略。**

#### 12.1.3.MAX()函数

MAX()返回指定列中的最大值。MAX()要求指定列名。

**对非数值数据使用MAX()：虽然MAX()一般用来找出最大的数值或日期值，但MySQL允许将它用来返回任意列中的最大值，包括发那会文本列中的最大值。在用于文本数据时，如果数据按相应的列排序，则MAX()返回最后一行。**

**NULL值：MAX()函数忽略列值为NULL的行。**

#### 12.1.4.MIN()函数

MIN()函数与MAX()功能相反。

#### 12.1.5.SUN()函数

SUM()用来返回指定列值的和(总计)。

**在多个列上进行计算：SUM()可以在多个列如(items_price*quantity)进行计算。**

### 12.2.聚集不同值

以上5个聚集函数都可以如下使用：

1.对所有的行执行计算，指定ALL参数或不给参数(因为ALL是默认行为)。

2.只包含不同的值：指定DISTINCT参数。

下面的例子使用AVG()函数返回特定供应商提供的产品的平均价格。它与上面的SELECT语句相同，但使用DISTINCT参数，因此平均值只考虑各个不同的价格。

```mysql
输入： SELECT AVG(DISTINCT prod_prices)  AS  avg_price
      FROM  products
      WHERE vend_id = 1003;
```

| prod_prices |
| ----------- |
| 15.9998     |

**注意：如果指定列名，则DISTINCT只能用于COUNT()。DISTINCT不能用于COUNT(*)，因此不允许使用COUNT(DISTINCT)，否则会产生错误。类似地，DISTINCT必须使用列名，不能用于计算或表达式。**

### 12.3.组合聚集函数

目前为止的所有聚集函数都只涉及单个函数。但实际上SELECT语句可根据需要包含多个聚集函数，如下例子。

```mysql
输入： SELECT  COUNT(*)  AS num_item
              AVG(prod_prices)  AS  avg_price
      FROM  products
```

输出：

| num_item | avg_price |
| -------- | --------- |
| 14       | 16        |

取别名：在指定别名以及包含某个聚集函数的结果时，不应该使用表中实际的列名，虽然这样做并不合法，但使用唯一的名字会使你的SQL更易于理解和使用(以及奖励容易排除故障)。

## 第十三章，分组数据

分组允许把数据分成多个逻辑组，以便能对每个组进行聚集计算。

### 13.2.创建分组

分组是在SELECT语句的GROUP BY子句中建立的。理解分组的最好办法是看一个例子。

```mysql
输入： SELECT vend_id, COUNT(*)  AS num_prods
      FROM  products
      GROUP BY vend_id;
```

输出：

| vend_id | num_prods |
| ------- | --------- |
| 1001    | 3         |
| 1002    | 2         |
| 1003    | 7         |
| 1004    | 2         |

分析：上面的SELECT语句指定了两个列，vend_id包含产品供应商的ID，num_prods为计算字段(用COUN(*)函数建立)。GROUP BY 子句指示MySQLan按vend_id排序并分组数据。这导致对每个vend_id而不是整个表计算num_prods一次。因为使用了GROUP BY，就不必指定要计算和估值的每个组了。系统会自动完成。GROUP BY子句指示MySQL分组数据。然后对每个组而不是整个结果集进行聚集。

在具体使用GROUP BY之前，需要知道一些重要的规定。

1.GROUP BY子句可以包含任意数目的列。这使得能对分组进行嵌套，为数据分组提供更细致的控制。

2.如果在GROUP BY子句中嵌套了分组，数据将在最后规定的分组上进行汇总。换句话说，在建立分组时，指定的所有列都一起计算(所有不能从个别的列取回数据)。

3.GROUP BY子句中列出的每个列都必须是检索列或有效的表达式(但不能是聚集函数)，换句话说，在建立分组时，指定的所有列都一起计算(所以不能从个别的列取回数据)。

4.除聚集计算语句外，SELECT语句中的每个列都必须在GROUP BY子句中给出。

5.GROUP BY子句必须出现在WHERE子句之后，ORDER BY子句之前。

### 13.3.过滤分组

除了能用GROUP BY分组数据外，MySQL还允许过滤分组，规定包括哪些分组，排除哪些分组。例如，想要列出至少有两个订单的所有顾客。为得出这种数据，必须基于完整的分组而不是个别的行进行过滤。这时候就可以使用HAVING子句了。HAVING子句与WHERE子句很类似，唯一的差别是WHERE过滤行，HAVING过滤分组。

那么怎么过滤分组呢，看下面的例子。

```mysql
输入： SELECT vend_id, COUNT(*)  AS orders
      FROM  products
      GROUP BY vend_id;
      HAVING COUNT(*) >=2;
```

输出：

| vend_id | orders |
| ------- | ------ |
| 10001   | 2      |

分析：这条语句与之前类似，指示最后一行的HAVING子句过滤COUNT(*)>=2的那些分组。只会显示>==2的分组。

**HAVING和WHERE的差别：这里有另一种理解方法，WHERE在数据分组前进行过滤，HAVING在数据分组后进行过滤。**

当然，可以同时使用WHERE和HAVING子句，例如如下例子，它列出具有2个(含)以上，价格为10(含)以上的产品的供应商。

```mysql
输入： SELECT vend_id, COUNT(*)  AS num_prodes
      FROM  products
      WHERE prod_price >= 10
      GROUP BY vend_id;
      HAVING COUNT(*) >=2;
```

输出：

| vend_id | num_prodes |
| ------- | ---------- |
| 1003    | 4          |
| 1005    | 2          |

分析：WHERE先过滤，再按vend_id分组数据，之后HAVING再过滤。

### 13.4.分组和排序

虽然GROUP BY 和ORDER BY经常完成相同的工作，但它们是非常不同的。

| ORDER BY                                   | GROUP BY                                                 |
| ------------------------------------------ | -------------------------------------------------------- |
| 排序产生的输出                             | 分组行，但输出可能不是分组的顺序                         |
| 任意列都可以使用(甚至非选择的列也可以使用) | 只可能使用选择列或表达式列，而且必须使用每个选择列表达式 |
| 不一定需要                                 | 如果与聚焦函数一起使用(或表达式)，则必须使用             |

**不要忘记ORDER BY：一般在使用GROUP子句时，应该也给出ORDER BY子句。这是保证数据正确排序的唯一方法。千万不要依赖GROUP BY排序数据。**

### 13.5.SELECT子句顺序

| 子句     | 说明               | 是否必须使用           |
| -------- | ------------------ | ---------------------- |
| SELECT   | 要返回的列或表达式 | 是                     |
| FROM     | 从中检索数据的表   | 仅在从表选择数据时使用 |
| WHERE    | 行级过滤           | 否                     |
| GROUP BY | 分组说明           | 仅在按组计算聚集时使用 |
| HAVING   | 组级过滤           | 否                     |
| ORDER BY | 输出排序顺序       | 否                     |
| LIMIT    | 要检索的行数       | 否                     |

## 第十四章，使用子查询

子查询：即嵌套在其他查询中的查询。

### 14.2.利用子查询进行过滤

假如需要列入订购物品TNT2的所有客户，应该怎样检索？下面是具体步骤。

1.检索包含物品TNT2的所有订单的编号。

2.检查具有前一步骤列出的订单编号的所有客户的ID。

3.检索前一步骤返回的所有客户ID的客户信息。

上述每一个步骤都可以作为一个查询来执行。可以把一条SELECT语句返回的结果用于另一条SELECT语句的WHERE子句。

```mysql
第一条输入： SELECT order_name
           FROM orderitems
           WHERE prod_id = 'TNT2';
```

```mysql
第二条输入： SELECT cust_id
           FROM   orders
           WHERE  order_num  IN (2005,2007)    //2005和2007为上一步输出的订单编号。
```

现在，把第一个查询变为子查询组合两个查询。看下面的语句。

```mysql
输入：  SELECT cust_id
       FROM   orders
       WHERE  order_num  IN(SELECT order_name
                            FROM orderitems
                            WHERE prod_id = 'TNT2')
```

分析：在子查询中，子查询总是从内向外处理。在处理上面的SELECT语句。MySQL实际上执行了两个操作。

1.首先，它执行括号内的查询。2.查询返回值作为外部查询的条件。

现在已经得到1.2步的查询结果，现在执行第三步，得到这些客户ID的客户信息。

```mysql
第三条输入： SELECT cust_name, cust_contact
           FROM customers
           WHERE cust_id IN(10001,10004)  //10001和10004为得到的客户ID
```

现在把其中的WHERE转换自子查询。

```mysql
输入：      SELECT cust_name, cust_contact
           FROM customers
           WHERE cust_id IN(SELECT cust_id
                            FROM   orders
                            WHERE  order_num  IN(SELECT order_name
                                                 FROM orderitems
                                                 WHERE prod_id = 'TNT2'));
```

分析：与上面一样，由内括号向外延伸，实际执行了3个操作。

**列必须匹配：在WHERE子句中使用子查询，应该保证SELECT语句具有与WHERE子句相同数目的列。通常，子查询将返回单个列并且与单个列匹配，但如果需要也可以使用多个列。**

### 14.3.作为计算字段使用子查询

使用子查询的另一个方法是创建计算字段。假如需要显示customers表中每个客户的订单总量。订单与相应的客户ID存储在orders表中。

为了执行这个操作，遵循下面的步骤。

1.从customers表中检索客户列表。

2.对于检索出的每个客户，统计其在orders表中的订单数目。

可使用SELECT COUNT(*)对表中的进行计数，并且通过提供一条WHERE子句来过滤某个特定的客户ID，可仅对该客户的订单进行计算。

例如，如下的代码对客户10001的订单进行计数。

```mysql
输入： SELECT COUNT(*) AS orders
      FROM   orders
      WHERE  cust_id = 10001;
```

为了对每个客户只需COUNT(*)计算，应该将COUNT(*)作为一个子查询。如下所示。

```mysql
输入： SELECT cust_name,cust_state,(SELECT COUNT(*) 
                                  FROM   orders
                                  WHERE  orders.cust_id = customers.cust_id) AS orders
      FROM customers
      ORDER BY cust_name;
```

输出：

| cust_name | cust_state | orders |
| --------- | ---------- | ------ |
| Coy       | MI         | 2      |
| E         | IL         | 1      |
| Mou       | OH         | 0      |

分析：这条SELECT语句对customers表中每个客户返回3列：cust_name，cust_state，orders。orders是一个计算字段，它是由圆括号中的子查询建立的。该子查询为检索出的每个客户执行一次。

**相关子查询：设计外部查询的子查询。**

任何时候只要列名有很多义性，就必须使用这种语法(表名和列名由一个句点分隔)。