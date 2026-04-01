package com.easysharenet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class ProxyService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var serverSocket: ServerSocket? = null
    private val PROXY_PORT = 1080
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "proxy_service"
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification("Başlatılıyor..."))
        
        serviceScope.launch {
            try {
                startProxyServer()
            } catch (e: Exception) {
                e.printStackTrace()
                stopSelf()
            }
        }
        
        return START_STICKY
    }
    
    private suspend fun startProxyServer() {
        val selectorManager = SelectorManager(Dispatchers.IO)
        
        serverSocket = aSocket(selectorManager)
            .tcp()
            .bind(InetSocketAddress("127.0.0.1", PROXY_PORT))
        
        updateNotification("Çalışıyor - Port: $PROXY_PORT")
        
        while (true) {
            val clientSocket = serverSocket?.accept() ?: break
            
            serviceScope.launch {
                handleClient(clientSocket)
            }
        }
    }
    
    private suspend fun handleClient(clientSocket: Socket) {
        try {
            val input = clientSocket.openReadChannel()
            val output = clientSocket.openWriteChannel(autoFlush = true)
            
            // SOCKS5 handshake
            val version = input.readByte()
            if (version.toInt() != 5) {
                clientSocket.close()
                return
            }
            
            val nmethods = input.readByte()
            val methods = ByteArray(nmethods.toInt())
            input.readFully(methods, 0, nmethods.toInt())
            
            // No authentication
            output.writeByte(5)
            output.writeByte(0)
            output.flush()
            
            // Read request
            val ver = input.readByte()
            val cmd = input.readByte()
            val rsv = input.readByte()
            val atyp = input.readByte()
            
            if (cmd.toInt() != 1) { // Only CONNECT supported
                sendSocks5Error(output, 7)
                clientSocket.close()
                return
            }
            
            // Parse destination
            val destAddr = when (atyp.toInt()) {
                1 -> { // IPv4
                    val addr = ByteArray(4)
                    input.readFully(addr, 0, 4)
                    addr.joinToString(".") { (it.toInt() and 0xFF).toString() }
                }
                3 -> { // Domain
                    val len = input.readByte().toInt()
                    val domain = ByteArray(len)
                    input.readFully(domain, 0, len)
                    String(domain)
                }
                else -> {
                    sendSocks5Error(output, 8)
                    clientSocket.close()
                    return
                }
            }
            
            val destPort = ((input.readByte().toInt() and 0xFF) shl 8) or 
                          (input.readByte().toInt() and 0xFF)
            
            // Connect to destination
            val destSocket = try {
                aSocket(SelectorManager(Dispatchers.IO))
                    .tcp()
                    .connect(InetSocketAddress(destAddr, destPort))
            } catch (e: Exception) {
                sendSocks5Error(output, 1)
                clientSocket.close()
                return
            }
            
            // Send success response
            output.writeByte(5)
            output.writeByte(0)
            output.writeByte(0)
            output.writeByte(1)
            output.writeInt(0) // Bind address
            output.writeShort(0) // Bind port
            output.flush()
            
            // Relay data
            val destInput = destSocket.openReadChannel()
            val destOutput = destSocket.openWriteChannel(autoFlush = true)
            
            coroutineScope {
                launch { relay(input, destOutput) }
                launch { relay(destInput, output) }
            }
            
            destSocket.close()
            clientSocket.close()
            
        } catch (e: Exception) {
            e.printStackTrace()
            clientSocket.close()
        }
    }
    
    private suspend fun relay(input: ByteReadChannel, output: ByteWriteChannel) {
        try {
            val buffer = ByteBuffer.allocate(8192)
            while (!input.isClosedForRead) {
                buffer.clear()
                val count = input.readAvailable(buffer)
                if (count == -1) break
                buffer.flip()
                output.writeFully(buffer)
                output.flush()
            }
        } catch (e: Exception) {
            // Connection closed
        }
    }
    
    private suspend fun sendSocks5Error(output: ByteWriteChannel, errorCode: Int) {
        output.writeByte(5)
        output.writeByte(errorCode)
        output.writeByte(0)
        output.writeByte(1)
        output.writeInt(0)
        output.writeShort(0)
        output.flush()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Proxy Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
    
    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EasyShareNet")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }
    
    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification(text))
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        serverSocket?.close()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}
