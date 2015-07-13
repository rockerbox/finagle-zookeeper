## Finagle-ZooKeeper [![Build Status](https://travis-ci.org/finagle/finagle-zookeeper.svg?branch=master)](https://travis-ci.org/finagle/finagle-zookeeper)

finagle-zookeeper provides basic tools to communicate with a Zookeeper server asynchronously.

*Note: this is a Google Summer of Code 2014 project, mentored by Twitter, see [the mailing list](https://groups.google.com/forum/?hl=en#!topic/finaglers/GlLXNOvdSVg) for more details.*

##### Client
- `Client` is based on Finagle 6 client model.

##### Request

Every request returns a *twitter.util.Future* (see [Effective Scala](http://twitter.github.io/effectivescala/#Concurrency-Futures),
[Finagle documentation](https://twitter.github.io/scala_school/finagle.html#Future) and [Scaladoc](http://twitter.github.io/util/util-core/target/doc/main/api/com/twitter/util/Future.html))

##### [Wiki](https://github.com/finagle/finagle-zookeeper/wiki) [Scaladoc](http://finagle.github.io/finagle-zookeeper/#package)

* [How to create a ZkClient ?](https://github.com/finagle/finagle-zookeeper/wiki/1.-Create-a-ZkClient)

* [AutoReconnect feature](https://github.com/finagle/finagle-zookeeper/wiki/2.-AutoReconnect-(Automatic-Reconnection))

* [About host management](https://github.com/finagle/finagle-zookeeper/wiki/3.-Host-management)

* [Session](https://github.com/finagle/finagle-zookeeper/wiki/4.-Session)

* [Watcher manager](https://github.com/finagle/finagle-zookeeper/wiki/5.-Watcher-manager)

* [More about requests](https://github.com/finagle/finagle-zookeeper/wiki/6.-More-about-requests)


##### Client creation ( more details [here](https://github.com/finagle/finagle-zookeeper/wiki/1.-Create-a-ZkClient) )
```scala
  val client = Zookeeper.newRichClient("127.0.0.1:2181,10.0.0.10:2181,192.168.1.1:2181")
```
- `127.0.0.1:2181,10.0.0.10:2181,192.168.1.1:2181` is a String representing the server list, separated by a comma

##### Connect the client
```scala
val connect = client.connect
connect onSuccess { _ =>
  logger.info("Connected to zookeeper server")
} onFailure { exc =>
  logger.severe("Connect Error")
}
```

##### Disconnect the client
```
client.disconnect()
```

Return value `Future[Unit]`. It will stop all background operations related to features (preventive search, link checker), will clean everything and disconnect from the server.

##### Close the service
```
client.close()
```

Return value `Future[Unit]`. It will definitely close the service and stop background jobs. Make sure to use `disconnect` to close the session correctly before.

##### First request
Example of request with sequential composition :
```scala
val res = for {
  acl <- client.getACL("/zookeeper")
  _ <- client.create("/zookeeper/test", "HELLO".getBytes, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)
  _ <- client.exists("/zookeeper/test", true)
  _ <- client.setData("/zookeeper/test", "CHANGE".getBytes, -1)
} yield (acl)
```

##### create
```scala
client.create("/zookeeper/hello", "HELLO".getBytes, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)
```
- `/zookeeper/hello` : the node that you want to create
- `"HELLO".getBytes` : the data associated to this node
- `Ids.OPEN_ACL_UNSAFE` : default integrated ACL (world:anyone)
- `CreateMode.EPHEMERAL` : the creation mode

Return value `Future[String]` representing the path you have just created

- `CreateMode.PERSISTENT` persistent mode
- `CreateMode.EPHEMERAL` ephemeral mode
- `CreateMode.PERSISTENT_SEQUENTIAL` persistent and sequential mode
- `CreateMode.EPHEMERAL_SEQUENTIAL` ephemeral and sequential mode


##### delete
```scala
client.delete("/zookeeper/test", -1)
```
- `/zookeeper/test` : the node that you want to delete
- `-1` : current version of the node (-1 if you don't care)

Return value `Future[Unit]`

##### exists
```scala
client.exists("/zookeeper/test", false)
```
- `/zookeeper/test` : the node that you want to test
- `false` : Boolean if you don't want to set a watch this node

Return value `Future[ExistsResponse]` `ExistsResponse(stat: Option[Stat], watch: Option[Watcher])`, watch
will be composed of `Some(watcher:Watcher)` if you previously asked to set a watch on the node, otherwise
it will be `None`.

##### getACL
```scala
client.getACL("/zookeeper")
```
- `/zookeeper` : the node from which you want to retrieve the ACL

Return value `Future[GetACLResponse]` `GetACLResponse(acl: Array[ACL], stat: Stat)`

##### setACL
```scala
client.setACL("/zookeeper/test", Ids.OPEN_ACL_UNSAFE, -1)
```
- `/zookeeper/test` : the node that you want to update
- `Ids.OPEN_ACL_UNSAFE` : default integrated ACL (world:anyone)
- `-1` : current node's version (-1 if you don't care)

Return value `Future[Stat]`

##### getChildren
```scala
client.getChildren("/zookeeper", false)
```
- `/zookeeper` : the node that you want to get
- `false` : if you don't want to set a watch on this node

Return value `Future[GetChildrenResponse]` `GetChildrenResponse(children: Seq[String], watch: Option[Watcher])`
, watch will be composed of `Some(watcher:Watcher)` if you previously asked to set a watch on the node, otherwise
  it will be `None`.

##### getChildren2
```scala
client.getChildren2("/zookeeper", false)
```
- `/zookeeper` : the node that you want to get
- `false` : if you don't want to set a watch on this node

Return value `Future[GetChildren2Response]` `GetChildren2Response(children: Seq[String],
stat: Stat, watch: Option[Watcher])`, watch will be composed of `Some(watcher:Watcher)`
if you previously asked to set a watch on the node, otherwise it will be `None`.

##### getData
```scala
client.getData("/zookeeper/test", false)
```
- `/zookeeper/test` : the node that you want to get
- `false` : if you don't want to set a watch on this node

Return value `Future[GetDataResponse]` `GetDataResponse(data: Array[Byte], stat: Stat, watch: Option[Watcher])`,
watch will be composed of `Some(watcher:Watcher)` if you previously asked to set a watch on the node, otherwise
it will be `None`.

##### setData
```scala
client.setData("/zookeeper/test", "CHANGE".getBytes, -1)
```
- `/zookeeper/test` : the node that you want to update
- `"CHANGE".getBytes` : the data that you want to set on this node
- `-1` : current node's version (-1 if you don't care)

Return value `Future[Stat]`.

##### addAuth
```scala
client.addAuth("digest", "pat:pass".getBytes)
```
- `"digest"` : the authentication scheme
- `"pat:pass".getBytes` : data associated to the scheme

Return value `Future[Unit]`.

##### sync
```scala
client.sync("/zookeeper")
```
- `/zookeeper` : the node that you want to sync

Return value `Future[String]`.

##### Transaction

```scala
val opList = Seq(
  CreateRequest(
    "/zookeeper/hello", "TRANS".getBytes, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL),
  SetDataRequest("/zookeeper/hello", "changing".getBytes, -1),
  DeleteRequest("/zookeeper/hell", -1)
)
val res = client.transaction(opList)
```

Return value: `Future[TransactionResponse]`. A `TransactionResponse` is composed of a responseList,
it's a sequence of `OpResult`. This sequence is ordered in the same order than the original request sequence.

If one of the resquest contained in the `TransactionRequest` produces an error during the transaction proccessing,
all the operations of this transaction are cancelled, and the server will return a `TransactionResponse` where the
sequence of responses is only composed by `ErrorResponse`. 

An `ErrorResponse` will indicate the cause of the error
with its `exception` value. By reading the sequence of response from the transaction, if the exception is `OkException`
then the corresponding request is not the cause of the failure, you can go throught the list to find the problem
(exception will be different of `OkException`)

A transaction can be composed by one or more OpRequests :
```scala
case class CheckVersionRequest(path: String, version: Int)
case class CreateRequest(
  path: String,
  data: Array[Byte],
  aclList: Seq[ACL],
  createMode: Int
  )
case class Create2Request(
  path: String,
  data: Array[Byte],
  aclList: Seq[ACL],
  createMode: Int
  )
case class DeleteRequest(path: String, version: Int)
case class SetDataRequest(
  path: String,
  data: Array[Byte],
  version: Int
  )
```
Each opRequest will return a response of the same type (expect delete and checkVersion that return an EmptyResponse)
