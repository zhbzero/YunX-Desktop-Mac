package com.yunx.app.data.network

import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI

class HttpClientsProxyTest {
    @Test fun `direct mode never delegates requests to the system proxy selector`() {
        val original = ProxySelector.getDefault()
        var selections = 0
        ProxySelector.setDefault(object : ProxySelector() {
            override fun select(uri: URI): List<Proxy> {
                if (uri.scheme in listOf("http", "https")) selections++
                return listOf(Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 1)))
            }
            override fun connectFailed(uri: URI, sa: SocketAddress, ioe: IOException) = Unit
        })
        try {
            HttpClients.setProxy(null, 0)
            MockWebServer().use { server ->
                server.start()
                for (client in listOf(HttpClients.apiClient(), HttpClients.downloadClient())) {
                    assertSame(Proxy.NO_PROXY, client.proxy)
                    server.enqueue(MockResponse().setBody("direct"))
                    client.newCall(Request.Builder().url(server.url("/download")).build()).execute().use {
                        assertEquals("direct", it.body!!.string())
                    }
                }
            }
            assertEquals(0, selections)
        } finally {
            ProxySelector.setDefault(original)
            HttpClients.setProxy(null, 0)
        }
    }

    @Test fun `an explicit proxy is still used for both clients`() {
        try {
            MockWebServer().use { server ->
                server.start()
                HttpClients.setProxy("127.0.0.1", server.port)
                for (client in listOf(HttpClients.apiClient(), HttpClients.downloadClient())) {
                    server.enqueue(MockResponse().setBody("proxied"))
                    client.newCall(Request.Builder().url("http://download.invalid/file").build()).execute().use {
                        assertEquals("proxied", it.body!!.string())
                    }
                    assertEquals("GET http://download.invalid/file HTTP/1.1", server.takeRequest().requestLine)
                }
            }
        } finally {
            HttpClients.setProxy(null, 0)
        }
    }
}
