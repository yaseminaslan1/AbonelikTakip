package com.example.aboneliktakip

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aboneliktakip.data.Subscription
import com.example.aboneliktakip.viewmodel.SubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: SubscriptionViewModel) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var subscriptionToEdit by remember { mutableStateOf<Subscription?>(null) }
    var subscriptionToDelete by remember { mutableStateOf<Subscription?>(null) }
    var selectedCategory by remember { mutableStateOf("Tümü") }

    // Eklenmiş olan tüm benzersiz kategorileri dinamik olarak al
    val categories = remember(subscriptions) {
        val uniqueCategories = subscriptions.map { it.category }.filter { it.isNotBlank() }.distinct()
        listOf("Tümü") + uniqueCategories
    }

    // Seçili kategoriye göre listeyi filtrele
    val filteredSubscriptions = if (selectedCategory == "Tümü") {
        subscriptions
    } else {
        subscriptions.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    // Toplam harcama (seçili kategoriye göre dinamik hesaplanır)
    val totalExpense = filteredSubscriptions.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Abonelik & Bütçe Takip", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Abonelik Ekle")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Harcama Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (selectedCategory == "Tümü") "Aylık Toplam Harcama" else "$selectedCategory Harcaması",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₺${String.format("%.2f", totalExpense)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kayıtlı Abonelikler (${filteredSubscriptions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Yatay Kategori Filtre Çipleri
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory.equals(category, ignoreCase = true),
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredSubscriptions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (subscriptions.isEmpty()) {
                            "Henüz eklenmiş bir abonelik yok.\n'+' butonuna basarak ekle!"
                        } else {
                            "Bu kategoride abonelik bulunamadı."
                        },
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSubscriptions, key = { it.id }) { subscription ->
                        SubscriptionItemCard(
                            subscription = subscription,
                            onClick = { subscriptionToEdit = subscription },
                            onDeleteClick = { subscriptionToDelete = subscription }
                        )
                    }
                }
            }
        }
    }

    // Yeni Abonelik Ekleme
    if (showAddDialog) {
        SubscriptionFormDialog(
            dialogTitle = "Yeni Abonelik Ekle",
            confirmButtonLabel = "Ekle",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount, category, day ->
                viewModel.addSubscription(
                    Subscription(
                        name = name,
                        amount = amount,
                        category = category,
                        paymentDay = day
                    )
                )
                showAddDialog = false
            }
        )
    }

    // Abonelik Düzenleme
    if (subscriptionToEdit != null) {
        val target = subscriptionToEdit!!
        SubscriptionFormDialog(
            dialogTitle = "Aboneliği Düzenle",
            confirmButtonLabel = "Güncelle",
            initialName = target.name,
            initialAmount = target.amount.toString(),
            initialCategory = target.category,
            initialDay = target.paymentDay.toString(),
            onDismiss = { subscriptionToEdit = null },
            onConfirm = { name, amount, category, day ->
                viewModel.updateSubscription(
                    target.copy(
                        name = name,
                        amount = amount,
                        category = category,
                        paymentDay = day
                    )
                )
                subscriptionToEdit = null
            }
        )
    }

    // Silme Onayı
    if (subscriptionToDelete != null) {
        AlertDialog(
            onDismissRequest = { subscriptionToDelete = null },
            title = { Text("Aboneliği Sil") },
            text = { Text("\"${subscriptionToDelete?.name}\" aboneliğini silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        subscriptionToDelete?.let { viewModel.deleteSubscription(it) }
                        subscriptionToDelete = null
                    }
                ) {
                    Text("Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { subscriptionToDelete = null }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun SubscriptionItemCard(
    subscription: Subscription,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subscription.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${subscription.category} • Her ayın ${subscription.paymentDay}. günü",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "₺${String.format("%.2f", subscription.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Sil",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun SubscriptionFormDialog(
    dialogTitle: String,
    confirmButtonLabel: String,
    initialName: String = "",
    initialAmount: String = "",
    initialCategory: String = "",
    initialDay: String = "",
    onDismiss: () -> Unit,
    onConfirm: (name: String, amount: Double, category: String, paymentDay: Int) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var amount by remember(initialAmount) { mutableStateOf(initialAmount) }
    var category by remember(initialCategory) { mutableStateOf(initialCategory) }
    var paymentDay by remember(initialDay) { mutableStateOf(initialDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Abonelik Adı (örn. Netflix)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Aylık Tutar (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (örn. Eğlence)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = paymentDay,
                    onValueChange = { paymentDay = it },
                    label = { Text("Ödeme Günü (1-31)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    val parsedDay = paymentDay.toIntOrNull() ?: 1
                    if (name.isNotBlank() && parsedAmount > 0.0) {
                        onConfirm(
                            name,
                            parsedAmount,
                            category.ifBlank { "Genel" },
                            parsedDay.coerceIn(1, 31)
                        )
                    }
                }
            ) {
                Text(confirmButtonLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}