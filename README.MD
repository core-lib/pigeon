What's this?
====

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;###Pigeon是一个纯粹使用HTTP协议封装的一个符合RESTful规范的用于客户端与服务端之间通过接口规范来进行通讯的RPC框架.###

#####尤其擅长于Android/iOS与JAVA服务端的API调用,致力于解决目前移动端互联网开发的网络通讯的各种问题.#####

What are the benefits?
====

框架提供符合RESTful规范的功能,通过接口的方式开放后台的服务,前端引入后台打包好的接口和传输数据类型(VO类型),<br/>
大大简化前端的开发难度,前端通过接口来进行远程调用后台的开放服务,直接利用已经打包过来的传输数据类型,<br/>
减少重复定义传输数据类型的繁琐和无技术含量的工作.使用接口的方式来调用带来的好处是:<br/>
* 1.不需要考虑参数类型和明确知道返回值类型是否匹配,因为接口声明好了,编译时就能检查是否正确.
* 2.接口可声明可能抛出的异常,强制客户端需要对可能的异常进行处理.
* 3.框架屏蔽序列化和反序列化过程,让代码更简洁,书写就像是在调用本地的方法.
* 4.提供拦截器拓展,可以无限拓展对请求/回应的统一处理,轻松实现自动重新登录,网络不稳定自动重试,权限拦截,参数校验,日志记录等等统一切面功能
* 5.提供目前网络请求所有数据转换的注解配置,不需要任何代码,轻松享受框架提供的编码/压缩/校验/加密/签名等常用功能
* 6.提供多种客户端实现:同步/异步/RxJava 满足你日常的需要.
* 7.纯粹的HTTP规范,与流行的HTTP框架可以无缝对接
* 8.提供多种项目集成方式:与Spring集成(交给容器管理);与web服务器集成(作为一个Filter);裸开模式...

How to use?
====
说了这么多，到底怎么用？

**1：明确需求**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;假如用户下载了我们的APP（以Android为例），这个时候用户需要注册。那么我们的后台需要有一个提供用户注册的方法或者叫API，我就一步到位吧。这个方法就长这样子
	User register(String username, String password) throws IOException; 相信大家一眼就能看出来，这是个接口上的抽象方法，因为它没有方法体也没有修饰符。

**2：定义接口**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为什么要接口？因为我们要让Android/iOS客户端调用的时候代码书写就像在调用本地方法一样啊。例如在Android端使用的话就会变成
	User user = api.register("叶良辰","我有一百种方法让你在github混不下去"); 很简单有没有？很想EJB、WebService是不是？我会说差不多，但是比它们都更简单更轻量级。

**3：完整实现**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这里我们注意到有一个User类，也应该有一个API接口同时也应该有一个API实现类。<br/>
好吧，那我们就创建一个Maven多模块项目将它们一一写出来吧。
我们最好把项目分成几个模块，等下我们会知道为什么需要分模块。
那就创建一个结构类似下面的Maven项目吧
```
/demo-parent
   |
   |--/demo-core
   |
   |--/demo-web 
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其中我们把 User类 和 API接口 放在 demo-core模块中，让demo-web依赖demo-core模块，
再把APIImpl这个接口实现类放在demo-web模块中，这三个类的主要代码如下

// 注意让你的参与网络传输的类型实现 Serializable 接口，轻松拥有可序列化功能。
```
public class User implements Serializable {
	private static final long serialVersionUID = 5991214707724188798L;

	private String username;
	private String password;
	
	// getter setter ...
}

public interface API {
	
	// 注意这里的异常定义，框架明确要求开放方法必须定义IOException异常抛出，其实这大有用处，后面再说。
	User register(String username, String password) throws IOException;
	
}

public class APIImpl implements API {
	
	public User register(String username, String password) throws IOException {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		// 将user保存到数据库...
		return user;
	}
	
}
```	
**4：框架上场**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;怎么地？这样写出来就可以用啦？嗯，差不多了，我们继续看。
* a.首先将框架从github里导入到你的eclipse中，地址：https://github.com/core-lib/pigeon.git<br/> 这是个maven项目，有几个模块，第一次编译比较久因为需要下载一些第三方依赖。。。

* b.假如你已经编译完成没有任何问题。

* c.让demo-core项目依赖你下载的pigeon框架的pigeon-core模块，版本号看你下载的版本为准。
```
<dependency>
	<groupId>payne.framework</groupId>
	<artifactId>pigeon-core</artifactId>
	<version>3.6.0-release</version>
</dependency>
```
* d.我们把API接口加上一个框架提供的注解@Open，其他东西不用修改，然后它就变成了
```
@Open
public interface API {

	User register(String username, String password) throws IOException; 
	
}
```
* e.下面我们写一个单元测试类吧，功能总是需要测试的对吧。给demo-web模块添加pigeon-server和pigeon-client依赖，如果没有junit依赖也补上去。版本号以你下载的为准。。。
```
<dependency>
	<groupId>payne.framework</groupId>
	<artifactId>pigeon-server</artifactId>
	<version>3.6.0-release</version>
</dependency>

<dependency>
	<groupId>payne.framework</groupId>
	<artifactId>pigeon-client</artifactId>
	<version>3.6.0-release</version>
</dependency>

<dependency>
	<groupId>junit</groupId>
	<artifactId>junit</artifactId>
	<version>4.5</version>
</dependency>
```
```
public class APITests {
	private InvocationContext context;
	private API api;
	
	public static void main(String[] args) throws Exception {
		InvocationContext context = new BlockingInvocationContext();
		context.bind(10086);
		context.register(new APIImpl());
		context.startup();
	}

	@Before
	public void setup() throws Exception {
		// 启动服务端
		context = new BlockingInvocationContext();
		context.bind(10086); // 开放端口，根据自己喜好
		context.register(new APIImpl());
		context.startup();
		
		// 模拟客户端
		Client client = new Client("localhost", 10086);
		api = client.create("HTTP", "application/x-java-serialized-object", "/APIImpl", API.class);
	}
	
	@Test
	public void testRegister() throws Exception{
		User user = api.register("叶良辰","我有一百种方法让你在github混不下去");
		System.out.println("返回结果" + user.getUsername() + ":" + user.getPassword());
	}

	@After
	public void destroy() throws Exception {
		context.shutdown();
	}

}
```
运行 testRegister 方法。。。
		
**5：客户端使用API**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;到这里我们就会明白为什么我说项目要分模块。因为我们需要把这个模块打包给Android使用，或者生成对应的模型和接口给iOS使用。所以我们打包的时候不可能把实现类也打包给客户端吧？因为实现类我们放在了demo-web，所以客户端也就只知道接口而且，面向接口编程也是pigeon框架所提倡的。
	
* a.假设你已经打好包了，把demo-core的jar包放到Android中，并且将我们之前打包好的pigeon-core、pigeon-introspection和pigeon-client三个jar包放入Android项目中。
	
* b.构造客户端，记得改IP地址和端口，指向到你后台的IP地址和端口。
```
Client client = new Client("server.ip", 10086);
api = client.create("HTTP", "application/x-java-serialized-object", "/APIImpl", API.class);
```
* c.通过 APITests 的main方法启动后台，后台一旦启动就会在等待请求的状态
	
* d.调用后台，在适当的地方使用代码调用后台服务，例如在用户点了注册的确定按钮。注意！注意！这里虽然看上去是在调用本地代码，但是实际上是网络交互，所以不要放在主线程！！！用AsyncTask吧。
```
User user = api.register("叶良辰","我有一百种方法让你在github混不下去");
System.out.println("返回结果" + user.getUsername() + ":" + user.getPassword());
```
* e.当你写完一个AsyncTaks时，你会发现，似乎还是很麻烦的样子。不用担心，框架有更好的方案。让你直接在主线程写代码，框架实现异步调用和回调主线程代码。

**6：更多功能**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其实上述只是pigeon框架的冰山一角，框架提供session，序列化，编码，压缩，校验，加密，签名，参数校验。。。每种数据转换都有接口和原生的几个实现，可以直接使用也可以自定义拓展，所有这些数据
转换功能都是无需编写代码，只需要在开放接口或方法上标注对应注解即可轻松拥有。

**网络协议**
```
HTTP
HTTP/1.0
HTTP/1.1
```
**加密**
```
RSA
AES
DES
DESede
```
**签名**
```
MD2WithRSA
MD5WithRSA
SHA1WithRSA
SHA256WithRSA
```
**编码**
```
base64
base32
hex
```
**压缩**
```
flat
GZIP
LZ4
snappy
```
**序列化**
```
application/json
application/xml
application/yaml
application/smile
application/cbor
application/avro
application/x-www-form-urlencoded
application/x-java-serialized-object
application/hessian
application/hessian2
application/burlap
```
**校验**
```
MD2
MD5
SHA-1
SHA-256
```

**7：框架集成**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;框架轻松跟市面上流行的技术进行集成，例如Spring，Hibernate，web服务器（tomcat。。。）更好的整合在你已有的项目和拓展你的项目。在保证不修改你现有的开放服务的实现类代码前提下，可以通过抽象出一个接口就能通过pigeon框架的形式去开放远程接口方式的调用。

**8：更多教程**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;更详细的使用教程，更强大的功能使用请下载pigeon-sample工程。地址：https://github.com/core-lib/pigeon-sample.git

**9：更多功能**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如何使用session？如何生成objective-c客户端？如何生成异步API？如何拓展框架？框架的运行原理？请下载pigeon-sample工程。地址：https://github.com/core-lib/pigeon-sample.git

**10：有问题?**<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;有任何问题，随时随地可以加我QQ：646742615（我的昵称是Payne）  或发邮件到我QQ邮箱：646742615@qq.com。你也可以微信我：change1921
