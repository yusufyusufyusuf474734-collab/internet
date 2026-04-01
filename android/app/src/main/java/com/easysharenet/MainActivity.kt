package com.easysharenet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    
    private var isRunning by mutableStateOf(false)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                MainScreen(
                    isRunning = isRunning,
                    onToggle = { toggleService() }
                )
            }
        }
    }
    
    private fun toggleService() {
        if (isRunning) {
            stopService(Intent(this, ProxyService::class.java))
            isRunning = false
        } else {
            startForegroundService(Intent(this, ProxyService::class.java))
            isRunning = true
        }
    }
}

@Composable
fun MainScreen(
    isRunning: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Logo/Title
            Text(
                text = "🌐",
                fontSize = 72.sp
            )
            
            Text(
                text = "EasyShareNet",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D2D2D)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Durum",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = if (isRunning) "🟢 Çalışıyor" else "🔴 Durduruldu",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) Color(0xFF4CAF50) else Color(0xFFFF5252)
                    )
                    
                    if (isRunning) {
                        Divider(color = Color.Gray.copy(alpha = 0.3f))
                        
                        InfoRow("Port", "1080")
                        InfoRow("Protokol", "SOCKS5")
                        InfoRow("Adres", "127.0.0.1")
                    }
                }
            }
            
            // Toggle Button
            Button(
                onClick = onToggle,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFFF5252) else Color(0xFF2196F3)
                )
            ) {
                Text(
                    text = if (isRunning) "DURDUR" else "BAŞLAT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Instructions
            if (!isRunning) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2D2D2D)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📱 Kullanım",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Text(
                            text = "1. USB debugging açık olmalı\n" +
                                  "2. Telefonu USB ile bağlayın\n" +
                                  "3. BAŞLAT'a basın\n" +
                                  "4. Linux'ta: easysharenet-connect",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
