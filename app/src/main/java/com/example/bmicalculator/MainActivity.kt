package com.example.bmicalculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bmicalculator.ui.theme.BMICalculatorTheme
import kotlin.math.roundToInt


val BiruUtama = Color(0xFF1E5EFF)     // TopAppBar & tombol Reset
val UnguHitung = Color(0xFF7C3AED)    // tombol Hitung BMI
val HijauKategori = Color(0xFF22A447) // tombol Lihat Kategori
val LatarHalaman = Color(0xFFF6F8FC)
val LatarKartu = Color(0xFFEEF3FF)
val TeksGelap = Color(0xFF1F2937)
val TeksAbu = Color(0xFF6B7280)

data class KategoriBmi(val nama: String, val rentang: String, val warna: Color)

val daftarKategori = listOf(
    KategoriBmi("Kurus", "< 18.5", Color(0xFF3B82F6)),
    KategoriBmi("Normal", "18.5 – 24.9", Color(0xFF22A447)),
    KategoriBmi("Berlebih", "25.0 – 29.9", Color(0xFFF59E0B)),
    KategoriBmi("Obesitas", "≥ 30.0", Color(0xFFEF4444))
)

fun cariKategori(bmi: Double): KategoriBmi = when {
    bmi < 18.5 -> daftarKategori[0]
    bmi < 25.0 -> daftarKategori[1]
    bmi < 30.0 -> daftarKategori[2]
    else -> daftarKategori[3]
}

fun hitungBmi(beratKg: Double, tinggiCm: Double): Double {
    val tinggiM = tinggiCm / 100
    val bmi = beratKg / (tinggiM * tinggiM)
    return (bmi * 10).roundToInt() / 10.0
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BMICalculatorTheme {
                BmiCalculatorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiCalculatorApp() {
    val context = LocalContext.current

    // State
    var berat by remember { mutableStateOf("") }
    var tinggi by remember { mutableStateOf("") }
    var hasilBmi by remember { mutableStateOf<Double?>(null) }
    var tampilkanKategori by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = LatarHalaman,
        topBar = {
            TopAppBar(
                title = { Text("BMI Calculator", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(
                            context,
                            "BMI = berat (kg) ÷ tinggi (m)²",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(Icons.Default.Info, contentDescription = "Info rumus BMI")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BiruUtama,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Input berat badan
            item {
                InputAngka(
                    label = "Berat Badan (kg)",
                    nilai = berat,
                    placeholder = "Contoh: 70",
                    onNilaiBerubah = { berat = it }
                )
            }

            // Input tinggi badan
            item {
                InputAngka(
                    label = "Tinggi Badan (cm)",
                    nilai = tinggi,
                    placeholder = "Contoh: 170",
                    onNilaiBerubah = { tinggi = it }
                )
            }

            // Button 1: Hitung BMI
            item {
                Button(
                    onClick = {
                        val b = berat.toDoubleOrNull()
                        val t = tinggi.toDoubleOrNull()
                        if (b == null || t == null || b <= 0 || t <= 0) {
                            Toast.makeText(
                                context,
                                "Isi berat dan tinggi badan dengan angka yang benar",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            hasilBmi = hitungBmi(b, t)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnguHitung)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hitung BMI", fontSize = 16.sp)
                }
            }

            // Button 2: Reset
            item {
                OutlinedButton(
                    onClick = {
                        berat = ""
                        tinggi = ""
                        hasilBmi = null
                        tampilkanKategori = false
                        Toast.makeText(context, "Data direset", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, BiruUtama),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BiruUtama)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset", fontSize = 16.sp)
                }
            }

            // Button 3: Lihat / Sembunyikan Kategori
            item {
                Button(
                    onClick = { tampilkanKategori = !tampilkanKategori },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HijauKategori)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (tampilkanKategori) "Sembunyikan Kategori" else "Lihat Kategori",
                        fontSize = 16.sp
                    )
                }
            }

            // Kartu hasil BMI
            item {
                KartuHasil(bmi = hasilBmi)
            }

            // Tabel kategori (muncul saat tombol Lihat Kategori ditekan)
            if (tampilkanKategori) {
                item {
                    KartuKategori(bmi = hasilBmi)
                }
            }
        }
    }
}

@Composable
fun InputAngka(
    label: String,
    nilai: String,
    placeholder: String,
    onNilaiBerubah: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LatarKartu)
            .padding(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TeksGelap
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = nilai,
            // Hanya menerima angka dan titik desimal
            onValueChange = { input -> onNilaiBerubah(input.filter { it.isDigit() || it == '.' }) },
            placeholder = { Text(placeholder) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = TeksGelap,
                unfocusedTextColor = TeksGelap
            )
        )
    }
}

@Composable
fun KartuHasil(bmi: Double?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LatarKartu),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hasil BMI",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TeksGelap
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (bmi == null) {
                Text("--", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = TeksAbu)
                Text(
                    text = "Isi data lalu tekan Hitung BMI",
                    fontSize = 13.sp,
                    color = TeksAbu
                )
            } else {
                val kategori = cariKategori(bmi)
                Text(
                    text = "$bmi",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = TeksGelap
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Badge kategori
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(kategori.warna)
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = kategori.nama,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun KartuKategori(bmi: Double?) {
    val kategoriAktif = bmi?.let { cariKategori(it) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Kategori BMI (WHO)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TeksGelap
            )
            Spacer(modifier = Modifier.height(8.dp))

            daftarKategori.forEach { kategori ->
                val aktif = kategori == kategoriAktif
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (aktif) kategori.warna.copy(alpha = 0.15f) else Color.Transparent
                        )
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(kategori.warna)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = kategori.nama,
                        modifier = Modifier.weight(1f),
                        fontWeight = if (aktif) FontWeight.Bold else FontWeight.Normal,
                        color = TeksGelap
                    )
                    Text(text = kategori.rentang, color = TeksAbu)
                }
            }
        }
    }
}

// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BmiCalculatorPreview() {
    BMICalculatorTheme {
        BmiCalculatorApp()
    }
}