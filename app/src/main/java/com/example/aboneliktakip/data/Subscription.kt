package com.example.aboneliktakip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,               // Abonelik adı (Spotify, Netflix vb.)
    val amount: Double,             // Tutar
    val currency: String = "TRY",   // Para birimi
    val category: String,           // Kategori (Eğlence, İş, Eğitim)
    val paymentDay: Int,            // Ayın kaçında çekiliyor? (1-31)
    val billingCycle: String = "Aylık", // Aylık veya Yıllık
    val isActive: Boolean = true
)