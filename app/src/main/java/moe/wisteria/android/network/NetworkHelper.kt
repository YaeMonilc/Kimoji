package moe.wisteria.android.network

import moe.wisteria.android.network.api.ChannelApi
import moe.wisteria.android.network.api.PicaApi
import moe.wisteria.android.util.HmacUtils
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.net.ProxySelector
import java.util.UUID

const val CHANNEL_SERVER_URL = "http://68.183.234.72"
const val PICACOMIC_SERVER_URL = "https://picaapi.picacomic.com"

private object PicacomicConstValue {
    const val API_KEY = "C69BAF41DA5ABD1FFEDC6D2FEA56B"
    const val APP_CHANNEL = "1"
    const val APP_VERSION = "2.2.1.3.3.4"
    const val APP_UUID = "defaultUuid"
    const val IMAGE_QUALITY = "original"
    const val APP_PLATFORM = "android"
    const val APP_BUILD_VERSION = "45"
    const val ACCEPT = "application/vnd.picacomic.com.v1+json"
    const val USER_AGENT = "okhttp/3.8.1"
    const val CONTENT_TYPE = "application/json; charset=UTF-8"
    const val HOST = "picaapi.picacomic.com"

    const val HMAC_KEY = "~d}\$Q7\$eIni=V)9\\RK/P.RM4;9[7|@/CA}b~OW!3?EV`:<>M7pddUBL5n|0/*Cn"
}

var defaultOkHttpClient: OkHttpClient = getOkhttpClient()
    private set

val channelApi: ChannelApi by lazy {
    getBaseRetrofit(
        baseUrl = CHANNEL_SERVER_URL
    ).create(ChannelApi::class.java)
}

val picaApi: PicaApi by lazy {
    getBaseRetrofit(
        baseUrl = PICACOMIC_SERVER_URL
    ).create(PicaApi::class.java)
}

private fun getBaseRetrofit(
    baseUrl: String
) = Retrofit.Builder().apply {
    baseUrl(baseUrl)
    client(defaultOkHttpClient)
    addConverterFactory(GsonConverterFactory.create())
}.build()

@OptIn(ExperimentalStdlibApi::class)
private fun getOkhttpClient(
    dnsUrl: String? = null,
) = OkHttpClient.Builder().apply {
    dnsUrl?.let {
        dns(object : Dns {
            override fun lookup(hostname: String): List<InetAddress> {
                return Dns.SYSTEM.lookup(it)
            }
        })
    }

    proxySelector(ProxySelector.getDefault())

    addInterceptor {
        it.proceed(
            it.request().newBuilder().apply {
                it.request().let { request ->
                    val url = request.url.toString()

                    if (url.startsWith(PICACOMIC_SERVER_URL)) {
                        PicacomicConstValue.apply {
                            val path = url.replace("$PICACOMIC_SERVER_URL/", "")
                            val nonce = UUID.randomUUID().toString().replace("-", "")
                            val time = (System.currentTimeMillis() / 1000).toString()
                            val signature = HmacUtils.hmacSHA256(
                                key = HMAC_KEY,
                                data = "$path$time$nonce${ request.method }$API_KEY".lowercase()
                            ).toHexString()

                            addHeader("api-key", API_KEY)
                            addHeader("app-channel", APP_CHANNEL)
                            addHeader("time", time)
                            addHeader("nonce", nonce)
                            addHeader("signature", signature)
                            addHeader("app-version", APP_VERSION)
                            addHeader("app-uuid", APP_UUID)
                            addHeader("image-quality", IMAGE_QUALITY)
                            addHeader("app-platform", APP_PLATFORM)
                            addHeader("app-build-version", APP_BUILD_VERSION)
                            addHeader("accept", ACCEPT)
                            addHeader("User-Agent", USER_AGENT)
                            addHeader("Content-Type", CONTENT_TYPE)
                            addHeader("Host", HOST)
                        }
                    }
                }
            }.build()
        )
    }
}.build()

fun setDefaultOkhttpClientDns(
    dnsUrl: String?
) {
    dnsUrl?.let {
        defaultOkHttpClient = getOkhttpClient(it)
    }
}