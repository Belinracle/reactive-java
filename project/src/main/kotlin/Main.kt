package org.example

import com.binance.connector.client.SpotClient
import com.binance.connector.client.WebSocketStreamClient
import com.binance.connector.client.impl.SpotClientImpl
import com.binance.connector.client.impl.WebSocketStreamClientImpl
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import io.nats.client.Connection
import io.nats.client.Nats
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.net.URI
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlin.system.exitProcess


val objectMapper = jacksonObjectMapper()
val logger: Logger = LoggerFactory.getLogger("candles_listener")

fun main() {
    val mongoDatabase = connectToMongoDB()
    mongoDatabase.createCollection("spots")
    val spotCollection: MongoCollection<Document> = mongoDatabase.getCollection("spots")

    val natsConnection = createNatsConnection()

    val jedisPool = JedisPool(
        buildPoolConfig(),
        "dva-gandona-gitlab.ru", 62185
    )

    val client: SpotClient = SpotClientImpl()
    val market = client.createMarket()
    val availableAssets = market.exchangeInfo(mapOf("symbolStatus" to "TRADING"))
    val parsedSymbols = objectMapper.readValue<Map<String, Any>>(availableAssets)["symbols"]?.let { symbols ->
        (symbols as List<Map<String, Any>>).map { symbol ->
            val symbolMap = symbol as Map<String, Any>
            val symbol = symbolMap["symbol"] as String
            val filters = symbolMap["filters"] as List<Map<String, Any>>
//            spotCollection.replaceOne(
//                Filters.eq("_id", symbol),
//                Document.parse(objectMapper.writeValueAsString(mapOf("_id" to symbol, "filters" to filters))),
//                ReplaceOptions().upsert(true)
//            )
            symbol
        }
    }?.filter { it.endsWith("USDT") and ((it.length - it.replace("USD", "").length) == 3) }?.toSet()
        ?: throw IllegalArgumentException("какая то хуйня пришла")

    val symbolsToSubscribe = parsedSymbols.take(16).map {
        "${it.lowercase()}@kline_1m"
    } as ArrayList<String>
    logger.info("symbolsToSubscribe $symbolsToSubscribe")
    logger.info("symbolsToSubscribe size ${symbolsToSubscribe.size}")

    val parallelism = 8
    val symbolsChunks = symbolsToSubscribe.chunked(symbolsToSubscribe.size / parallelism)
    var counter = 0L
    val ioExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    Flux.merge(symbolsChunks.map { symbolsChunk ->
        var eventsCounter = 0L
        Flux.create<String>({ sink ->
            val wsStreamClient: WebSocketStreamClient = WebSocketStreamClientImpl()
            wsStreamClient.combineStreams(
                symbolsChunk as ArrayList<String>,
                { response -> //onOpenCallback
                    logger.info("On open callback for $response")
                },
                { event ->
                    run {
                        eventsCounter++
                        if ((eventsCounter % 10) == 0L) {
                            logger.info("$eventsCounter elements emitted, but requested ${sink.requestedFromDownstream()}")
                        }
                        sink.next(event)
                    }
                },
                { code, reason -> //onClosingCallback = {}
                    logger.error("On closing callback for $code, reason: $reason")
                    wsStreamClient.closeAllConnections()
                    exitProcess(1)
                },
                { code, reason -> //onCloseCallback = {}
                    logger.error("On closed callback for $code, reason: $reason")
                    wsStreamClient.closeAllConnections()
                    exitProcess(1)
                },
                { t, response -> //onFailureCallback = {}
                    logger.error("On failure callback with code $response", t)
                    wsStreamClient.closeAllConnections()
                    exitProcess(1)
                })
        }, FluxSink.OverflowStrategy.BUFFER)
    })
        .parallel()
        .runOn(Schedulers.boundedElastic())
//        .log()
        .map { event ->
            val parsedEvent = objectMapper.readValue<Map<String, Any>>(event)
            val klineEvent = parsedEvent["data"] as Map<String, Any>
            val klineDto = klineEvent["k"] as Map<String, Any>
            klineDto
        }
        .flatMap { klineDto ->
            Mono.zip(
                Mono.fromFuture(CompletableFuture.supplyAsync({
                    // save to postgres db
                    jedisPool.borrowObject()
                        .lpush("queue#spotCandles", klineDto.toString())
                    return@supplyAsync 1
                }, ioExecutorService)),
                Mono.fromFuture(CompletableFuture.supplyAsync({
                    spotCollection.updateOne(
                        Filters.eq("_id", klineDto["s"]!!),
                        Document("\$set", Document("lastPrice", klineDto["c"]!!))
                    )
                }, ioExecutorService)),
                Mono.just(klineDto)
            )
        }
        .subscribe { tuple ->
            natsConnection.publish("candles", objectMapper.writeValueAsString(tuple.t3).toByteArray())
        }
}

fun connectToMongoDB(): MongoDatabase {
    val user = System.getenv("MONGO_USERNAME") ?: throw IllegalArgumentException("MONGO_USERNAME is null")
    val password = System.getenv("MONGO_PASSWORD") ?: throw IllegalArgumentException("MONGO_USERNAME is null")
    val url = System.getenv("MONGO_URL") ?: throw IllegalArgumentException("MONGO_USERNAME is null")
    val maxPoolSize = 100
    val databaseName = "reactive_java"

    val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
    val uri = "mongodb://$credentials$url/?maxPoolSize=$maxPoolSize&w=majority"

    val mongoClient = MongoClients.create(uri)
    val database = mongoClient.getDatabase(databaseName)

    return database
}

fun createNatsConnection(): Connection {
    val natsUrl = System.getenv("NATS_URL") ?: throw IllegalArgumentException("NATS url is empty")
    return Nats.connect(natsUrl)
}

private fun buildPoolConfig(): JedisPoolConfig {
    val poolConfig = JedisPoolConfig()
    poolConfig.maxTotal = 128
    poolConfig.maxIdle = 128
    poolConfig.minIdle = 16
    poolConfig.testOnBorrow = true
    poolConfig.testOnReturn = true
    poolConfig.testWhileIdle = true
    poolConfig.numTestsPerEvictionRun = 3
    poolConfig.blockWhenExhausted = true
    return poolConfig
}

//        klineStartTime = get("t")!! as Long,
//        klineCloseTime = get("T")!! as Long,
//        symbol = get("s")!! as String,
//        interval = get("i")!! as String,
//        firstTradeId = get("f")!!.toString().toLong(),
//        lastTradeId = get("L")!!.toString().toLong(),
//        openPrice = (get("o")!! as String).toDouble(),
//        closePrice = (get("c")!! as String).toDouble(),
//        highPrice = (get("h")!! as String).toDouble(),
//        lowPrice = (get("l")!! as String).toDouble(),
//        baseAssetVolume = (get("v")!! as String).toDouble(),
//        numberOfTrades = get("n")!! as Int,
//        isKlineClosed = get("x")!! as Boolean,
//        quoteAssetVolume = (get("q")!! as String).toDouble(),
//        takerBuyBaseAssetVolume = (get("V")!! as String).toDouble(),
//        takerBuyQuoteAssetVolume = (get("Q")!! as String).toDouble(),
//        ignoreField = get("B")!! as String