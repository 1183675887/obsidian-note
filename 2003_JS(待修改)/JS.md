---
title: JS
date: 2020-12-07 10:06:51
tags: 前端
---

JS

<!--more-->

## 1.JS

### 1.引入JS

1.在html中直接写， 内部标签

```html
<script>
        alert("xxx");          //alert是弹框输出
        console.log(xxx);     //在浏览器的控制台打印变量
    </script>
```

2.引入外部时的写法。    外部引入。

```html
 <script src="js/qj.js" type="text/javascript"></script>     //src是引入的js的具体位置。
```

### 2.基本语法

1.变量。语法： var xxx = xx;

```html
<script>
        var num = 1;
        alert(num);
        var name = "renzhicheng";
        alert(name);
</script>
```

2.控制语句。可以嵌套

```html
<script>
      if (2 > 1) {
          alert("1");
      }
</script>
```

### 3.数据类型概要

变量，数值，文本，图形，音频，视频.....

```html
//为避免变量出现问题，可以在<script>第一行加入'use strict'

//变量

var a      //JS中所有变量都用var或者let。let是es6引入的。这两者都是局部变量。建议多使用let。var算是成员变量。

//numbner(数值)

123       //整数123
123.1     //浮点数123.1
1.23e3    //科学计数法
-99       //负数
NaN       // 不是一个数字
Infinity    //表示无限大

//字符串

'abc'
"abc"

//布尔值
true, false

//逻辑运算
&&   //与   
||   //或
！   //真即假，假即真

//比较运算符

=      //赋值
==     //等于(类型不一样，值一样，也会判断为true)
===    //绝对等于(类型一样，值一样才为true)

//null和undefined

null            //空
underfined      //未定义

//数组,一些相同类型的对象。创建一个数组是用[].

var arr = [1,2,3,4,5];

//对象,创建一个对象是用{}。

var person = { 
     name: "任支程",
     age: 18,
     tags: ['js','java','web']
    }

//取对象的值

person.name
    
```

### 4.数据类型详解

#### 4.1.字符串类型详解

1.正常字符串使用 单引号‘ ’ 或者双引号“ ”包裹

2.注意转义字符 \

```
\'        //输出单引号
\n        //换行
剩下的需要再查。
```

3.多行字符串编写

```
//符号在tab上面，esc下面。
var mas = `xxx
            xxx
             xxx
             xxx`
```

4.模板字符串

```
let name = "renzhic";
let age = 3;
let mag = `你好啊，${name}`
```

5.字符串长度

```
console.log(str.length)
```

6.字符串的可变性，不可变

7.大小写转换。

```
str.toUpperCase()            //转换为大写
str.toLowerCase()           //小写
str.indexOf('t')            //获得特定下标
str.substring(1)           //截取字符串

```

#### 4.2.数组类型详解

Array可以包含任意类型的数组

```
var arr = [1,2,3,4];         //通过下标取值和赋值
arr[0]
arr[0] = 1;
```

1.长度

```
arr.length
```

注意：给arr.length赋值，数组大小就会发生变化。如果赋值过小，元素会丢失

2.indexOf,通过元素获得下标处理

```
arr.indexOf(2)
1
```

字符串的"1"和数字1是不同的。

3.slice()截取Array的一部分，返回一个新数组，类似于String的substring。

4.push, pop

```
arr.push('a');     //压入到尾部
arr.pop();      //弹出尾部的元素
```

5.unshift(), shift()头部

```
unshift:  压入到头部
shift:  弹出头部的一个元素
```

6.排序sort()

```
arr.sort()
```

7.元素反转reverse().     concat()添加数组。   连接符join。

8.多维数组

```
arr = [[1,2],[1,3]];
arr[1][1];
```

#### 4.3对象

若干个键值对

```
//定义了对象，它有多个属性
var 对象名 = {
    属性名： 属性值，
    属性名： 属性值，
    属性名： 属性值
}
```

JS中对象，{.....}表示一个对象，键值对描述属性xxxx: xxx，多个属性之间使用逗号分隔，最后一个属性不加逗号。

1.对象赋值

```
person.name = "renzhic";
```

2.动态的删除属性，通过delete删除对象的属性

```
delete person.name
```

3.动态的添加，直接给新的属性添加值就可以

```
person.haha = "haha"
```

4.判断属性值是否在这个对象中。 xxx   in   xx

```
'age' in person
```

#### 4.4.Map和Set

Map。多余的方法自行搜索。

```
//Map通过key来获得value

var map = new Map([['tom',100],['jack',90],['haha',80]]);
var name = map.get('tom');        //获取
map.set('admin',123456);          //新增或修改
map.delete("tom");               //删除
```

Set:无序不重复的集合。多余方法自行搜索。

```
var set = new Set([3,1,1,1]);      //set会自动去重
set.add();                      //增加
```

使用iterator来遍历我们的map和set。具体用法搜索。

### 5.函数

#### 5.1.定义函数

定义方式一：绝对值函数

```javascript
function abs(x){
   if(x>=0){
   return x;
   }else{
   return -x;
   }
}

abs(10);             //调用函数
```

一旦执行到return代表函数结束，返回结果！如果没有执行return ,也会返回结果，结果就是undefined。

定义方式二：匿名函数

```javascript
var abs = function(x){
   if(x>=0){
   return x;
   }else{
   return -x;
   }
}

abs(10);            //调用函数
```

#### 5.2.变量的作用域

1.函数体内的声明的变量只有在函数中存在，函数体外不可以使用，不同的函数变量名可以一样，因为不影响(闭包)

```javascript
function abs(){
 var x = x+1;
 x = x + 1;
}

function ab(){
 var x = x+1;
 x = x + 1;
}

x = x + 2;                  //会报错。
```

嵌套函数的时候内部函数可以调用外部函数的变量，但外部不可以调用内部。

```javascript
function abs(){
var x = 1;

function abw(){
var y = x + 1;        //y=2
}

var z = y + 1;         //会报错。
}

```

函数查找变量从内向外查找。就近原则，哪个近的变量哪个就实现，其他的会被屏蔽掉。

2.在函数体外部声明的变量是全局变量。

```
var x = 1;

function abs(){

}
```

> 规范

由于所有的全局变量都会绑定到我们window上。如果不同的js文件，使用了相同的全局变量，就会产生冲突。

```
 var renzhicheng = {};                //唯一全局变量
 
 //定义全局变量
 renzhicheng.name = 'ren';           
 renzhicheng.add = function(a,b) {
   return a + b;
 }
 
```

把自己的代码全部放入自己定义的唯一空间中，降低全局命名冲突的问题~

ES6关键字let,解决局部作用域冲突问题！

建议局部变量使用let,可以解决局部变量的范围问题。常量使用const，可以解决常量会被修改的问题。

```javascript
let aaa = function(){

}

const pi = '3.14';         //常量不可变
```

#### 5.3.方法

```javascript
var xxx = {
  name: 'ren',
  birthday: 2020,
  
  age: function(){         //定义方法
     //今年-出生的年
     var now = new Date().getFullYear();
     return now - birthday;
  }
}
xxx.name          //调用属性
xxx.age();        //调用方法，一定要带括号。


或者还可以把方法拆开

function getAge() {
    //今年-出生的年
     var now = new Date().getFullYear();
     return now - birthday;
}

var xxx = {
name: 'ren',
birthday: 2020,
age: getAge
}
xxx.age()              //调用方法
```

#### 5.4.内部对象

1.Date

```
 var now = new Date();            //当前时间
 now.getFullYear();               //年
 now.getMonth();                  //月
 now.getDate();                   //日
 now.getDay();                   //星期几
 now.getHours();                  //时
 now.getMinutes();                //分
 now.getSeconds();                //秒
 now.getTime();                   //时间戳，全世界统一。
```

2.JSON

在JS中，一切皆为对象，任何js支持的类型都可以用JSON来表示

格式：

- 对象都是用{}

- 数组都是用[]

- 所有的键值对都是用key: value

```
//创建对象
var xxx = {
name: 'ren',
birthday: 2020,
age: 18
}

//将对象转换为JSON {"name": "ren", "birthday": "2020", "age": "18"}
var jonuser = JSON.stringify();

//将JSON转换为对象
var obj = JSON.parse(jonuser);
```

### 6.面向对象编程

class关键字，ES6引用的

1.定义一个类，属性，方法

```
//定义一个学生的类
class Student{
   constructor(name){
     this.name = name;
   }
   hello(){
     alert('hello')
   }
   
}

var xiaoming = new Student("xiaoming");
xiaoming.hello()               
```

更复杂的用到再搜索。

### 7.操作BOM对象(重点)

BOM：浏览器对象模型

1.window对象,代表浏览器

```
window.alert();
```

2.navigator对象，封装了浏览器的信息

```
navigator.userAgent
```

3.screen,代表屏幕尺寸

```
screen.width
screen.height
```

4.location，代表当前URL信息

```

```

5.document，代表当前页面，HTML,DOM文档树

```
//获取具体的文档树节点
<dl id ="app">
   <dt>java</dt>
   <dd>javaSE</dd>
   <dd>javaSE</dd>
</dl>

<script>
   var dl = document.getElementById('app');    //根据id获取文档
</script>
```

### 8.操作DOM对象(重点)

DOM：文档对象模型

以下都为原生代码，以后一般都用jquery。

1.要操作一个Dom节点，就必须要先获得这个Dom节点

```html
<div id="father">
    <h1>标题1</h1>
    <p id="p1">p1</p>
    <p class="p2">p2</p>
</div>

<script>
    //对应css选择器
    var h1 = document.getElementsByTagName('h1');          //根据标签名字获取
    var h1 = document.getElementById('p1');                //根据id获取
    var h1 = document.getElementsByClassName('p2');        //根据class获得
    var father = document.getElementById('father');        //根据id获取
    
    var children = father.children;                        //获取父节点下的所有子节点
    
</script>
```

2.更新节点

```
<script>
    var id1 = document.getElementById('id1');
    id1.innerText='456';
    id1.style.color = 'yellow';
</script>
```

3.删除节点

```
father.remove(p1)
```

4.创建节点。

```html
<script>
   var js = document.getElementById('js');      //已经存在的节点
   var list = document.getElementById('list');
   //通过JS创建一个新的节点
   var newP = document.createElement('p');     //创建一个p标签
   newP.id = 'newP';
   newP.innerText = 'Hello';
   //创建一个标签节点(通过这个属性，可以设置任意的值)
   var myScript = document.createElement('script');
   myScript.setAttribute('type','text/javascript')
</script>
```

### 9.点击事件onclick

```html
//按钮绑定事件，onclick被点击。即点击提交按钮会触发a事件的函数。
    <button type="button" onclick="a()">提交</button>
//表单绑定事件的校验。
<form action="www.baidu.com" method="post" onsubmit="a()">   
<script>
function a() {
     var uname = document.getElementById('username');
     console.log(uname.value);
}
</script>
```

### 10.JQUERY

JQUERY里面有大量的JS函数。需要引入

```
//CDN使用
<script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.js"></script>
//自行下载并放入包文件下,具体路径看安放位置
<script src="jq/jquery.js"></script>
```

JQUERY只有一个公式，记住就好,公式：$(选择器).action()

```html
<a href="" id="test-jquery">点我</a>

<script>
//原生
   var id = document.getElementById('id');
   id.click(function () {
     alert('hello');
   })
//JQUERY方式,以后都用这种。
   $('#test-jquery').click(function () {
      alert('hello');
   })
</script>
```

选择器

```html
<script>
    //原生的JS选择器少，太麻烦
    //标签
    document.getElementsByTagName()
    //id
    document.getElementById()
    //类
    document.getElementsByClassName()

    //JQUERY，css的选择器它全部都能用！
    $('p').click()    //标签选择器
    $('#id1').click()    //id选择器
    $('.class').click()    //类选择器
</script>
```

文档地址：https://jquery.cuishifeng.cn/

事件：鼠标事件，键盘事件，其他事件

```
$('选择器').mouxxx();             //鼠标事件
//具体使用查文档
```

