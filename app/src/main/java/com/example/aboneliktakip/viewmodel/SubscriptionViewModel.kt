package com.example.aboneliktakip.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aboneliktakip.data.AppDatabase
import com.example.aboneliktakip.data.Subscription
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).subscriptionDao()

    // Veritabanındaki tüm abonelikleri anlık dinleyen akış (StateFlow)
    val subscriptions: StateFlow<List<Subscription>> = dao.getAllSubscriptions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSubscription(subscription: Subscription) {
        viewModelScope.launch {
            dao.insertSubscription(subscription)
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            dao.deleteSubscription(subscription)
        }
    }

    fun updateSubscription(subscription: Subscription) {
        viewModelScope.launch {
            dao.updateSubscription(subscription)
        }
    }
}