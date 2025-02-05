---
title: HTML和CSS
date: 2020-12-06 11:05:29
tags: 前端
---

HTML和CSS。

<!--more-->

## 1.HTML

```html
<head>
    <!--网页头部 -->
</head>
<meta> <!--描述型标签，它用来描述我们网站的一些信息-->
<title> 
    <!--网页标题-->
</title>

<body>
    <!--网页主体内容，所有显现的都在这个body标签内-->
</body>

<h1>
    <!--标题标签，123456就是不同的标签-->
</h1>

<p>
    <!--段落标签，每一个就是一行-->
</p>

<br>或者<br/>  <!--这是换行标签，两种都可以，只需要写一个就会换行了-->

<hr>或者<hr/>  <!--水平线标签-->

<Strong>
<!--，粗体标签将里面的字体加粗-->
</Strong>

<em>
<!--斜体标签-->
</em

<!--特殊符号-->
空格 &nbsp;
大于号 &gt;
小于号 &lt;
版权符号 &copy;

<!-- 图像标签-->
<!--img学习
src:图片地址； ../代表上一级目录，/代表下一级目录 
alt：当图片加载失败时显示的东西，一般是中文
title:悬停时产生的文字
width:图像宽度
height:图像高度-->
<img src="" alt="" width="" height="">
    
<!--链接标签-->
<!--a中的超链接学习，与img类似
href:链接路径，表示要跳转到哪个路径，可以嵌套图片等内容当做点击样式
target:表示链接在哪个窗口打开:_blank可以在新页面打开，默认_self,在自己的网页中打开，或者自己定义-->    
<a href="" target="" >链接名字</a>
<a href="" target="" >
    <img src="" alt="" width="" height=""></a> 
<!--a的锚链接学习
1.需要一个锚标记，2.跳转到标记
name:做标记，href中的就是name中的东西，点击就会跳转到name的位置-->
<a name="">锚链接名字</a>
<a href="name属性中的东西"></a>
<!--a的功能性链接，如邮件mailto:qq链接，也在href中填写-->
    
<!--有序列表，即顺序1.2.3.4.5.应用范围试卷，问答。ol为标签，li为显示内容-->
<ol>
  <li></li>
</ol>
<!--无序列表,即全是*，没顺序，应用范围导航，侧边栏，ul为标签，li为显示内容-->
<ul>
  <li></li>
</ul>
<!--自定义列表，dl为标签，dt为列表名称，dd为列表内容-->
<dl>
    <dt></dt>
    
    <dd></dd>
</dl>
    
<!--表格标签，table为标签，tr为行标签，td为列标签。注意td在tr内.border为边框；colspan为列的占据，rowspan为行的占据。-->
<table border="">
    <tr>
        <td colspan=""或者rowspan=""></td>
    </tr>
</table>
    
<!--音频audio标签和视频video标签,src:资源路径，controls:控制条。autoplay：自动播放-->
<video src="" controls autoplay></video>

<!--网页基本结构-->
<header></header>   //标题头部区域的内容
<footer></footer>   //标记脚部区域的内容
<section></section> //Web页面中的一块独立区域
<article></article> //独立的文章内容
<aside></aside>     //相关内容或应用(或用于侧边栏)
<nav></nav>         //导航类辅助内容
    
<!--iframe内联框架-->
<iframe src="" name="" width="" height=""></iframe>
    
<!--form为表单标签，method为用什么方式发送表单，action为向何处发送表单数据
input标签为文本输入框，name为名字，type为框内填写的方法，有很多种，以下都是。 value为初始默认值-->
<form method="" action="">
    <p>名字:<input name="name" type="text" value="" /></p>
    <p>密码:<input name="pass" type="password" /></p>
    <p>
        <input type="submit" name="Button" value="提交"/>
        <input type="reset" name="Reset" value="重填"/>
    </p>
</form>
    
<!--单选框标签name要一致。type中的radio就代表单选框，check为默认-->
<p>
    <input type="radio" value="boy" name="sex" checked />男
    <input type="radio" value="girl" name="sex" />女
</p>

<!--多选框标签name要一致。type的checkbox就代表多选框，checked为默认-->
<p>爱好：
    <input type="checkbox" value="sleep" name="hobby" checked>睡觉
    <input type="checkbox" value="code" name="hobby">敲代码
</p>

<!--按钮,type的button就代表普通按钮，image代表图像按钮，submit代表提交按钮，reser代表设置按钮。value代表按钮上的值-->
<p>
    <input type="button" name="btn" value="提交">
</p>

<!--select为下拉列表框，name为提交的列表名称，value为选项提交的值。如选择中国则会提交value中的值。selected为默认-->
<p>
    <select name="提交的列表名称">
        <option value="选项提交的值" selected>中国</option>
        <option value="选项的提交值">美国</option>
    </select>
</p>
    
<!--表单中的input各种属性。hidden为隐藏域，disabled为禁用。readonly只读-->
<input-----   hidden disabled readonly>   //只能选择其中一个，其中隐藏域经常用。
<!--placeholder为框内提示,required表示框内不能为空，pattern为正则表达式验证-->
<input placeholder=""  required   pattern=""/>
```

## 2.CSS

### 1.CSS的三种导入方式

```css
<!--HTML页面代码-->
<h1>我是标题</h1>

<!--3种导入css的方式-->
<!--第一种，行内样式。直接在标签中引用style属性，在属性内可用定义css样式。-->
<h1 style="color: red;"></h1>

<!--第二种内部样式。<style>标签编写css代码。-->
<style>
h1{   
    color: red;
}
</style>

<!--第三种外部样式。<style>中的代码转移到css包下即可。删除<style></style>标签。在<head></hean>内使用link标签引用。-->
 <link rel="stylesheet" href="css/style.css">  //其中rel是固定的。href是css样式的具体位置。

<!--总结：语法：选择器{声明1: --;},括号内写渲染.每一个声明最好以分号结束。其中标签优先级：就近原则，离标签近的就生效-->
```

###   2.选择器

> 作用：选择页面上的某一个或者某一类元素

注意：以下为了演示都会在一个页面写css。

#### 2.1.基本选择器

1.标签选择器,会选择所有的h1标签的样式进行更改。

```html
<style>
    h1{
        color: red;
    }
</style>
---------
<h1>学java</h1>
<h1>学java</h1>

```

2.类选择器  class，用.class名进行。只会对class属性相同的样式进行更改。好处：可以多个标签一个class属性。标签可以不一样。

```html
<style>
 .renzhicheng{
      color: red;
        }
 .yangshi{
     color: red;
        }
</style>
---------
<h1 class="renzhicheng"></h1>
<h1 class="yangshi"></h1>
```

3.Id 选择器，用#Id名进行。只会对Id相同的样式进行渲染，注意Id是唯一的。

```html
<style>
#renzhicheng{
    color: red;
        }
</style>
-------
<h1 id="renzhicheng">标题1</h1>
```

注意：优先级：Id选择器 > 类选择器 > 标签选择器

#### 2.2.层次选择器

1.后代选择器：在某个元素的后面    祖爷爷   爷爷   爸爸   你。语法：父标签  子标签{}进行。

```html
<style>
    body p{
        color: red;
    }
</style>
-------<!--此时所有body标签以下的p下标签样式都变了。包括<ul><li>等-->
<body>
<p>p1</p>
<p>p2</p>
<p>p3</p>
<ul>
    <li>
        <p>p4</p>
    </li>
    <li>
        <p>p5</p>
    </li>
    <li>
        <p>p6</p>
    </li>
</ul>
</body>
```

2.子选择器。只有一代，儿子。语法：父标签>子标签{}。此时只有body下的p标签实现。

```html
<style>
    body>p{
        color: red;
    }
</style>
------p与前面的一样
<p></p>
```

3.楼下兄弟选择器。语法：.兄弟标签 + 楼下标签{}。此时只有p2的标签实现

```html
<style>
    .active + p{
        color: red;
    }
</style>

<p class="active">p1</p>
<p>p2</p>
```

4.通过选择器。语法：.class属性~{}。此时与p同级的相同p标签实现。

```html
<style>
    .active~{
        color: red;
    }
</style>
-----
<p class="active">p1</p>
<p>p2</p>
<p>p3</p>
```

#### 2.3.结构伪类选择器(太复杂，需要再查)

#### 2.4.属性选择器

1.标签可以有class,id等多种选择器。当选择id的语法：标签[选择器]{}。此时就会在标签中选择有选择器的实现。此时是有id的实现(或者id=first)。

```html
<style>                        或者
    a[id]{                         a[id=first]{
        color: red;                    color: red;
        }                                      }
</style>
-----
<p class="demo"></p>
<a href="" class="links item first" id="first">1</a>
<a href="" class="links item active" target="_blank" title="test">2</a>
<a href="" class="links item">3</a>
```

2.选择class的语法：标签名[class=”选择器属性“]。此时是class只为links的实现。 [class*="选择器属性"]为包含属性的实现。

```html
<style>                                  或者                  
    a[class="links item"]{              a[class*="links"]{
        color: red;                         color: red;
        }                                            }
</style>
------
```

3.选择href的语法：标签吗[href^="选择器属性"]。此时是以属性开头的实现。$=是以结尾实现。

```html
<style>                        或者
    a[href^=""]{                a[href^=""]{      
        color: red;              color: red;   
        }                               }
</style>
```

### 3.美化网页元素

1.<span>元素约定重点突出字，虽然还是用calss或Id来显示样式，这是一种约定。<div>代表块，用css属性可以标明是哪一块。

```html
<span class="">xxxx</span>
<div></div>
```

2.字体：大小，样式，粗细，颜色

```css
<style>                                         或者总和起来
xxx{                                             xxx{
        font-family: 楷体;      //字体样式           font: xxx xx xx xxx   //具体顺序百度
        font-size: 50px;       //字体大小
        font-weight: bold;     //字体粗细
        color: red;            //字体颜色
    }
</style>
```

3.文本：颜色，文本对齐的方式，首行缩进，行高，装饰如下划线。

```css
<style>
xxx{ 
    color: red;                //文本颜色
    text-align: center;        //文本对齐的位置
    text-indent: 2em;          //首行缩进
    height: 300px;              //块的高度
    line-height: 300px;         //行高
    text-decoration: underline;   //文本下面的划线
        
}
</style>
```

4.文本阴影和超链接伪类(需要时再查询)

5.列表：样式

```css
<style>
xxx{
    list-style: none;           //列表前面的样式
}
</style>
```

6.块的各种属性

```css
<style>
div{
    width: 1000px;                     //块的宽度
    height: 700px;                     //块的高度
    border: 1px solid red;            //块的边框粗细，样式，颜色
    background-image: url("");        //块的边框的背景图片，默认是全覆盖
    background-repeat: repeat-x;      //背景图片水平铺满
    background-repeat: repeat-x;      //背景图片竖直铺满
    background-repeat: no-repeat;     //不平铺
    background-position: 150px 2px;     //背景图片的位置，宽度和高度
    background: red url("") 270px 10px no-repeat;         //颜色，图片位置，宽度，高度，平铺方式
    
}
</style>

<div class></div>
```

### 4.盒子模型

[![DvShdI.jpg](https://s3.ax1x.com/2020/12/07/DvShdI.jpg)](https://imgchr.com/i/DvShdI)



- margin:外边距

- padding:内边距

- border:边框

1.边框：粗细，样式，颜色

```html
<style>        <!--这一步是经常要用的，将所有置为0-->
    xxx,xx,body{
        margin: 0;        //将外边距置于0
        padding: 0;        //将内边距置于0
        text-decoration: none;      //文本没有下划线
    }
    xxx{
        width: 300px;          //边框宽度
        border: 1px solid red;            //块的边框粗细，样式，颜色
    }
</style>
```

2.内外边距：

```html
xxx{
padding: 0 0 0 0;        //内边距上右下左的边距
margin: 0 auto:          //外边距上由。其中auto会导致div居中
margin-top: 0;           //上边距改为0。其他的都类似
}
```

### 5.diaplay与浮动

块级元素：独占一行。

```html
h1~h6   p   div   列表....
```

行级元素：不独占一行

```html
span   a img  strong.....
```

行内元素可以被包含在块级元素中，反之，则不可以~

2.display:

```html
xxx{
  display: inline;          //将标签改为行内元素
  display: block;           //将标签改为块元素
  display: inline-block;    //将标签该为块元素或者行内元素，但是可以内联，在一行！
  display: none;            //将
}
```

3.(浮动)float:

```html
xxxx{
float: right;                //将标签向右浮动起来
clear: both;                //两侧不允许浮动
clear: left;                //左边不允许浮动
clear: right;               //右侧不允许浮动
claer: none;                //可以浮动
}
```

4.父级边框塌陷问题

- 增加父级元素高度

```css
#father{
    border: 1px #000 soild;
    height:  800px;
}
```

- 增加一个空的div标签，清除浮动

- 增加overflow属性

  ```css
  #father{
     overflow: hidden;          //超过边框时自动隐藏
  }
  ```

- 给父类增加一个伪类：after

  ```css
  #father:after{
   content: '';
   display: block;
   clear:   both;
  }
  ```

  小结：

  1.浮动元素增加div，简单，代码中尽量避免空div

  2.设置父元素的高度，简单，元素假设有了固定的高度，就会被限制

  3.overflow，简单，下拉的一些场景避免使用。

  4.父类元素添加一个伪类：after（推荐）。

  ### 6.定位

  1.相对定位。相对于原来的位置，进行指定的偏移，相对定位的话，它任然在标准文档流中，会保持之前的位置。

  position: relative;

  ```css
  xxx{
      position: relative;
      top: -20px;
      left: 20px;
      bottom: -10px;
      right: 20px;
  }
  ```

  2.绝对定位：相对于xxx，进行指定的偏移。没有父级则相对于浏览器定位；父级存在的话，相对于父级偏移。

  position: absolute;

  3.固定定位：相对于原来位置，一直会固定不动。

  position: fixed;

  

  

  