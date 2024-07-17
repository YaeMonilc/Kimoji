package moe.wisteria.android.network

import moe.wisteria.android.network.api.ChannelApi
import moe.wisteria.android.network.api.PicacomicApi
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.net.ProxySelector

const val CHANNEL_SERVER_URL = "http://68.183.234.72"
const val PICACOMIC_SERVER_URL = "https://picaapi.picacomic.com"

var defaultOkHttpClient: OkHttpClient = getOkhttpClient()
    private set

val channelApi: ChannelApi by lazy {
    getBaseRetrofit(
        baseUrl = CHANNEL_SERVER_URL
    ).create(ChannelApi::class.java)
}

val picacomicApi: PicacomicApi by lazy {
    getBaseRetrofit(
        baseUrl = PICACOMIC_SERVER_URL
    ).create(PicacomicApi::class.java)
}

private fun getBaseRetrofit(
    baseUrl: String
) = Retrofit.Builder().apply {
    baseUrl(baseUrl)
    client(defaultOkHttpClient)
    addConverterFactory(GsonConverterFactory.create())
}.build()

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