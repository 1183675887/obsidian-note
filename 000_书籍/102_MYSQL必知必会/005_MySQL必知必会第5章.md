---
title: MySQL必知必会4-9章
date: 2020-08-12 10:00:50
tags:
  - MySQL
  - MYSQL必知必会
---

这些章讲解检索数据，排序检索数据，过滤数据，数据过滤，用通配符过滤，用正则表达式进行搜索。

<!--more-->

## 第五章，排序检索数据

### 5.1.排序检索数据

**子句：SQL语句由子句构成的，有些子句是必须的，而有的是可选的。一个子句通常由一个关键字和所提供的数据组成。**

为了明确地排序用SELECT语句检索出的数据，可使用ORDER  BY子句。ORDER  BY子句取一个或多个列的名字，据此对输出进行排序。如下面的例子。

```mysql
输入：  SELECT prod_name
       FROM   products
       ORDER BY  prod_name;
```

分析：这条语句除了指示MySQL对prod_name列以字母顺序排序数据的ORDER BY子句外，与前面的语句一样。结果如下：

| prod_name    |
| ------------ |
| .5 ton anvil |
| Bird seed    |
| Carrots      |

通过非选择列进行排序：通常，ORDER  BY子句中使用的列将是为显示所选择的列。但是，实际上并不一定要这样，用非建设的列排序数据是完全合法的。

### 5.2.按多个列排序

为了按多个列排序，只需要指定列名，列名之间用逗号分开即可。

下面的代码检索3个列，并按照其中两个列对结果进行排序。首先是价格，然后在按名称排序。

```mysql
输入：  SELECT  prod_id, prod_price,prod_name
       FROM   products;
       ORDER BY prod_price,prod_name
```

输出：

| prod_id | prod_price | prod_name    |
| ------- | ---------- | ------------ |
| FC      | 2.50       | Carrots      |
| TNT1    | 2.50       | TNT(1 stick) |
| FU1     | 3.42       | Fuses        |

重要的是理解在按多个列排序时，排序完全按所规定的顺序进行。换句话说，对于上述例子中的输出，仅在多个行具有相同的prod_price值时才对产品按prod_name进行排序。如果prod_price列中所有的值都是唯一的，则不会按prod_name排序。

### 5.3.指定排序方向

数据排序不限于升序排序。这只是默认的排序顺序，还可以使用ORDER  BY子句以降序顺序排序。为了进行降序排序，必须指定DESC关键字。下面的例子按价格以降序排序产品(最贵的排在最前面)。

```mysql
输入：  SELECT  prod_id, prod_price,prod_name
       FROM   products;
       ORDER BY prod_price DESC;
```

输出：

| prod_id | prod_price | prod_name |
| ------- | ---------- | --------- |
| JP2000  | 55.00      | Jetpack   |
| SAFE    | 50.00      | Safe      |
| ANV03   | 35.00      | Jepack    |

还可以使用多个列排序。下面的例子以降序排序产品，然后再对产品名排序。

```mysql
输入：  SELECT  prod_id, prod_price,prod_name
       FROM   products;
       ORDER BY prod_price DESC,prod_name
```

输出：

| prod_id | prod_price | prod_name |
| ------- | ---------- | --------- |
| JP2000  | 55.00      | Jetpack   |
| FB      | 10.00      | Bird      |
| TNT2    | 10.00      | TNT       |

DESC关键字只应用到直接位于前面的列名。在上例中，只对prod_price列指定DESC，对prod_name列不指定。因此，prod_price列以降序排序，而prod_name仍然按标准的升序排序。

**在多个列上降序排序：如果想在多个列上进行降序排序，必须对每个列指定DESC关键字。**

使用ORDER BY和LIMIT的组合，能够找到一个列中最高或最低的值。下面的例子演示了如何找到最昂贵物品的值。

```mysql
输入：  SELECT prod_price
       FROM   products;
       ORDER BY prod_price DESC
       LIMIT 1;
```

输出：

| prod_price |
| ---------- |
| 55.00      |

ORDER BY子句的位置：在给出ORDER  BY子句时，应该保证它位于FROM子句之后。如果使用LIMIT，它必须位于ORDER BY之后。使用子句的次序不对将产生错误消息。