# 探索Akka框架(一)

我们期望通过一个系统的教程，基于Akka框架构建一个分布式的数据库，用于存储和查询一些结构化的数据；与此同时，与大家一起探索Akka框架和Scala编程语言。希望这个系列能够一直继续下去，最终实现如下功能：

- RESTful交互接口
- SQL交互接口
- 简单的KV存储
- 分布式KV存储
- 高可用KV存储
- 多维度OLAP支持
- 针对时间维度的优化

## 1. 工具栈

- Intellij Idea 15.x
- scala 2.11.x
- Akka 2.4.2
- sbt 1.3.8

## 2. 需求

我们期望基于Akka框架构建一个单节点的数据库，用于存储和查询探针信息。我们假设：

- 每个探针都有`name`,`host`,`process`等基本信息，并且这三个信息唯一决定一个探针
- server会为每一个探针分配一个唯一的探针ID

### 2.1 数据写入

- request:

```javascript
POST /api/v1/events

{
  "name": "zhxiaog-pc",
  "host": "127.0.0.1",
  "process": 3025
}
```

- response:

```javascript
Status: 201
Location: "/api/v1/query?k=zhxiaog-pc:127.0.0.1:3025"
```

### 2.2 数据查询

- request

```javascript
GET "/api/v1/query?k=zhxiaog-pc:127.0.0.1:3025"
```

- response

```javascript
{
  "id": -1
}
```

## 3. 构建工具sbt

### 3.1 build.sbt文件

```scala
lazy val commonSettings = Seq(
  organization := "com.oneapm",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "arkenstone"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.2",
  "com.typesafe.akka" %% "akka-stream" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.2" % "test"
)

libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit-experimental" % "2.4.2-RC3"

libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.6"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
```

- `:=` 赋值
- `+=` append a single element
- `++=` concatenate another sequence
- build.sbt文件代码行之间需要有空行间隔？？？
- 关于scala版本：
  - 在上面的build.sbt文件中，以下两者等价：
    - `"com.typesafe.akka" %% "akka-actor" % "2.4.2"`
    - `"com.typesafe.akka" % "akka-actor_2.11" % "2.4.2"`
  - test scope引用：`"com.typesafe.akka" %% "akka-testkit" % "2.4.2" % "test"`

这里有sbt默认导入的[Keys](http://www.scala-sbt.org/0.13/sxr/sbt/Keys.scala.html)，可以了解下有个初步印象。

## 4. Akka TDD

### scalatest

```scala
class DbActorTest
  extends TestKit(ActorSystem("DbActorTest")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll
{
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "DbActor" should {
    "persist row" in {
      val actor = system.actorOf(DbActor.props)

      actor ! Row("test")

      Await.result((actor ? Query("test")).mapTo[Long], 10 seconds) shouldBe >=(1L)
    }
  }
}

```

- scalatest支持[多种测试风格](http://www.scalatest.org/user_guide/selecting_a_style):
  - **WordSpec**
  - FlatSpec
  - FunSuite
- Matchers，类似Hamcrest类库，提供断言函数，可以参考[这个列表](http://www.scalatest.org/user_guide/using_matchers)

### Akka testkit

- [TestKit](http://doc.akka.io/docs/akka/snapshot/scala/testing.html)
  - TestKit提供了一个内置的Actor: testActor
  - 当使用ImplicitSender时，所有发回的消息都会转发到testActor
  - TestKit内提供的`expectMsg*`系列断言对象都是testActor
  - 例如：

    ```
    actor ! Query("test")
    expectMsgPF() {
      case id: Long => id shouldBe >=(1L)
    }
    ```

- TestActors
  - EchoActor
  - ForwardActor
辅助测试的Actor，用于将testActor“注入”到其他Actor中

- TestProb
Akka中具有mock特性的actor，该actor
  - 将所有收到的消息都转向testActor
  - 可以回复消息

### Akka Http testkit

- 内置了implicit ActorSystem
- 将请求结果隐式转换为了`RouteTest`,该对象包含对响应结果的各种验证

## 5. Akka Http Higher Level API

## `Directives`

- [PathDirectives](http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0-M2/scala/http/path-matchers.html)

  ```scala
  path("api" / "v1") {...}

  path("users") {
    pathEnd {
      // list
    } ~ path(Int) {

    }
  }

  path("i" ~ IntNumber | "h" ~ HexIntNumber)
  ```

- MarshallingDirectives
