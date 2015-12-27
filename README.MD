废话少说，我们直接开门见山吧。假如你的公司后台采用JAVA作为开发语言，同时又有iOS/Android平台的APP，今天我为大家带来一个非常方便和强大的跨平台RPC框架。

说了这么多，到底怎么用？

1：明确需求；假如用户下载了我们的APP（以Android为例），这个时候用户需要注册。那么我们的后台需要有一个提供用户注册的方法或者叫API，我就一步到位吧。这个方法就长这样子
	User register(String username, String password) throws IOException; 相信大家一眼就能看出来，这是个接口上的抽象方法，因为它没有方法体也没有修饰符。

2：定义接口；为什么要接口？因为我们要让Android/iOS客户端调用的时候代码书写就像在调用本地方法一样啊。例如在Android端使用的话就会变成
	User user = api.register("叶良辰","我有一百种方法让你在github混不下去"); 很简单有没有？很想EJB、WebService是不是？我会说差不多，但是比它们都更简单更轻量级。

3：完整实现；这里我们注意到有一个User类，也应该有一个API接口同时也应该有一个API实现类。好吧，那我们就创建一个Maven多模块项目将它们一一写出来吧。
	我们最好把项目分成几个模块，等下我们会知道为什么需要分模块。
	
	那就创建一个结构类似下面的Maven项目吧
	/demo-parent
	   |
	   |--/demo-core
	   |
	   |--/demo-web 
	   
	其中我们把 User类 和 API接口 放在 demo-core模块中，让demo-web依赖demo-core模块，再把APIImpl这个接口实现类放在demo-web模块中，这三个类的主要代码如下
	
	// 注意让你的参与网络传输的类型实现 Serializable 接口，轻松拥有可序列化功能。
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
	
4：框架上场；怎么地？这样写出来就可以用啦？嗯，差不多了，我们继续看。
	a.首先将框架从github里导入到你的eclipse中，地址：https://github.com/core-lib/pigeon.git  这是个maven项目，有几个模块，第一次编译比较久因为需要下载一些第三方依赖。。。
	
	b.假如你已经编译完成没有任何问题。
	
	c.让demo-core项目依赖你下载的pigeon框架的pigeon-core模块，版本号看你下载的版本为准。
	
		<dependency>
			<groupId>payne.framework</groupId>
			<artifactId>pigeon-core</artifactId>
			<version>3.6.0-release</version>
		</dependency>
		
	d.我们把API接口加上一个框架提供的注解@Open，其他东西不用修改，然后它就变成了
		
		@Open
		public interface API {
		
			User register(String username, String password) throws IOException; 
			
		}
	
	e.下面我们写一个单元测试类吧，功能总是需要测试的对吧。给demo-web模块添加pigeon-server和pigeon-client依赖，如果没有junit依赖也补上去。版本号以你下载的为准。。。
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
		
		运行 testRegister 方法。。。
		
5.客户端使用API；到这里我们就会明白为什么我说项目要分模块。因为我们需要把这个模块打包给Android使用，或者生成对应的模型和接口给iOS使用。所以我们打包的时候不可能把实现类也打包给客户端吧？因为实现类我们放在了demo-web，所以客户端也就只知道接口而且，面向接口编程也是pigeon框架所提倡的。
	a.假设你已经打好包了，把demo-core的jar包放到Android中，并且将我们之前打包好的pigeon-core、pigeon-introspection和pigeon-client三个jar包放入Android项目中。
	
	b.构造客户端，记得改IP地址和端口，指向到你后台的IP地址和端口。
		Client client = new Client("server.ip", 10086);
		api = client.create("HTTP", "application/x-java-serialized-object", "/APIImpl", API.class);
		
	c.通过 APITests 的main方法启动后台，后台一旦启动就会在等待请求的状态
	
	d.调用后台，在适当的地方使用代码调用后台服务，例如在用户点了注册的确定按钮。注意！注意！这里虽然看上去是在调用本地代码，但是实际上是网络交互，所以不要放在主线程！！！用AsyncTask吧。
		User user = api.register("叶良辰","我有一百种方法让你在github混不下去");
		System.out.println("返回结果" + user.getUsername() + ":" + user.getPassword());
		
	e.当你写完一个AsyncTaks时，你会发现，似乎还是很麻烦的样子。不用担心，框架有更好的方案。让你直接在主线程写代码，框架实现异步调用和回调主线程代码。
	
6.其实上述只是pigeon框架的冰山一角，框架提供session，序列化，编码，压缩，校验，加密，签名，参数校验。。。每种数据转换都有接口和原生的几个实现，可以直接使用也可以自定义拓展，所有这些数据
转换功能都是无需编写代码，只需要在开放接口或方法上标注对应注解即可轻松拥有。

网络协议
HTTP=payne.framework.pigeon.core.protocol.HTTPChannel
HTTP/1.0=payne.framework.pigeon.core.protocol.HTTPChannel
HTTP/1.1=payne.framework.pigeon.core.protocol.HTTPChannel

加密
RSA=payne.framework.pigeon.core.encryption.RSAInvocationEncryptor
AES=payne.framework.pigeon.core.encryption.AESInvocationEncryptor
DES=payne.framework.pigeon.core.encryption.DESInvocationEncryptor
DESede=payne.framework.pigeon.core.encryption.DESedeInvocationEncryptor

签名
MD2WithRSA=payne.framework.pigeon.core.signature.MD2WithRSAInvocationSigner
MD5WithRSA=payne.framework.pigeon.core.signature.MD5WithRSAInvocationSigner
SHA1WithRSA=payne.framework.pigeon.core.signature.SHA1WithRSAInvocationSigner
SHA256WithRSA=payne.framework.pigeon.core.signature.SHA256WithRSAInvocationSigner

编码
base64=payne.framework.pigeon.core.encoding.Base64InvocationEncoder
base32=payne.framework.pigeon.core.encoding.Base32InvocationEncoder
hex=payne.framework.pigeon.core.encoding.HexInvocationEncoder

压缩
flat=payne.framework.pigeon.core.compression.FlatInvocationCompressor
GZIP=payne.framework.pigeon.core.compression.GZIPInvocationCompressor
LZ4=payne.framework.pigeon.core.compression.LZ4InvocationCompressor
snappy=payne.framework.pigeon.core.compression.SnappyInvocationCompressor

序列化
application/url=payne.framework.pigeon.core.formatting.URLInvocationFormatter
application/json=payne.framework.pigeon.core.formatting.JSONInvocationFormatter
application/xml=payne.framework.pigeon.core.formatting.XMLInvocationFormatter
application/yaml=payne.framework.pigeon.core.formatting.YAMLInvocationFormatter
application/smile=payne.framework.pigeon.core.formatting.SmileInvocationFormatter
application/cbor=payne.framework.pigeon.core.formatting.CBORInvocationFormatter
application/avro=payne.framework.pigeon.core.formatting.AVROInvocationFormatter
application/x-www-form-urlencoded=payne.framework.pigeon.core.formatting.FormInvocationFormatter
application/x-java-serialized-object=payne.framework.pigeon.core.formatting.ObjectInvocationFormatter
application/hessian=payne.framework.pigeon.core.formatting.HessianInvocationFormatter
application/hessian2=payne.framework.pigeon.core.formatting.Hessian2InvocationFormatter
application/burlap=payne.framework.pigeon.core.formatting.BurlapInvocationFormatter

校验
MD2=payne.framework.pigeon.core.digestion.MD2InvocationDigester
MD5=payne.framework.pigeon.core.digestion.MD5InvocationDigester
SHA-1=payne.framework.pigeon.core.digestion.SHA1InvocationDigester
SHA-256=payne.framework.pigeon.core.digestion.SHA256InvocationDigester


7.框架轻松跟市面上流行的技术进行集成，例如Spring，Hibernate，web服务器（tomcat。。。）更好的整合在你已有的项目和拓展你的项目。在保证不修改你现有的开放服务的实现类代码前提下，可以通过抽象出一个接口就能通过pigeon框架的形式去开放远程接口方式的调用。

8.更详细的使用教程，更强大的功能使用请下载pigeon-sample工程。地址：https://github.com/core-lib/pigeon-sample.git

9.如何使用session？如何生成objective-c客户端？如何生成异步API？如何拓展框架？框架的运行原理？请下载pigeon-sample工程。地址：https://github.com/core-lib/pigeon-sample.git

10.有任何问题，随时随地可以加我QQ：646742615（我的昵称是Payne）  或发邮件到我QQ邮箱：646742615@qq.com。你也可以微信我：change1921