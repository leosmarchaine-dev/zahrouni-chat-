package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MarketplaceItem
import com.example.data.model.MarketplaceNotification
import com.example.data.model.TunisiaCities
import com.example.ui.theme.TunisiaRedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    items: List<MarketplaceItem>,
    notifications: List<MarketplaceNotification>,
    unreadNotifCount: Int,
    onAddProduct: (String, String, Double, String, String, List<String>) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onUpdatePrice: (String, Double) -> Unit,
    onMarkSold: (String) -> Unit,
    onContactSeller: (String, String) -> Unit,
    onSimulateFriendUpload: () -> Unit,
    onClearNotifs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedItemForDetail by remember { mutableStateOf<MarketplaceItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showEditPriceDialogFor by remember { mutableStateOf<MarketplaceItem?>(null) }

    val categories = listOf("All", "My Products", "Phones & Tech", "Electronics", "Vehicles", "Home & Goods")

    val filteredItems = remember(items, searchQuery, selectedCategory) {
        items.filter { item ->
            val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.location.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "My Products" -> item.isMine
                else -> item.category.equals(selectedCategory, ignoreCase = true)
            }

            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TunisiaRedPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Market Place 🛒",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "Buy & Sell in Tunisia 🇹🇳",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Simulate Friend Upload Button
                IconButton(
                    onClick = onSimulateFriendUpload,
                    modifier = Modifier.testTag("simulate_friend_product_button")
                ) {
                    Icon(
                        Icons.Default.GroupAdd,
                        contentDescription = "Simulate Friend Listing",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Notification Bell Icon with Badge
                BadgedBox(
                    badge = {
                        if (unreadNotifCount > 0) {
                            Badge(containerColor = TunisiaRedPrimary) {
                                Text(unreadNotifCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = {
                            showNotificationsSheet = true
                            onClearNotifs()
                        },
                        modifier = Modifier.testTag("marketplace_notif_button")
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Friend Product Notifications"
                        )
                    }
                }
            }
        }

        // Search Bar & Categories
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search products, cars, phones in Tunisia...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TunisiaRedPrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("marketplace_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TunisiaRedPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Product Grid
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No products found",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Be the first to list an item for sale!",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    ProductGridCard(
                        item = item,
                        onClick = { selectedItemForDetail = item },
                        onDelete = { onDeleteProduct(item.id) },
                        onEditPrice = { showEditPriceDialogFor = item }
                    )
                }
            }
        }

        // Sell Item FAB
        Button(
            onClick = { showAddDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp)
                .testTag("add_product_fab")
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("إضافة منتج للبيع / Sell Item (10 Photos) 📸", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }

    // Detail Modal Sheet
    if (selectedItemForDetail != null) {
        ProductDetailModal(
            item = selectedItemForDetail!!,
            onDismiss = { selectedItemForDetail = null },
            onContactSeller = { name, phone ->
                onContactSeller(name, phone)
                selectedItemForDetail = null
            },
            onDelete = { id ->
                onDeleteProduct(id)
                selectedItemForDetail = null
            },
            onEditPrice = { item ->
                showEditPriceDialogFor = item
                selectedItemForDetail = null
            },
            onMarkSold = { id ->
                onMarkSold(id)
                selectedItemForDetail = null
            }
        )
    }

    // Add Product Dialog (Supports up to 10 photos)
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onAddProduct = { title, desc, price, loc, cat, imgs ->
                onAddProduct(title, desc, price, loc, cat, imgs)
                showAddDialog = false
            }
        )
    }

    // Notifications Dialog / Sheet
    if (showNotificationsSheet) {
        AlertDialog(
            onDismissRequest = { showNotificationsSheet = false },
            icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = TunisiaRedPrimary) },
            title = { Text("إشعارات منتجات الأصدقاء 🔔", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (notifications.isEmpty()) {
                        Text("No friend listing notifications yet.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    } else {
                        notifications.forEach { notif ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = notif.friendAvatar,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${notif.friendName} listed a new product:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "🛍️ ${notif.productTitle} - ${notif.productPrice}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TunisiaRedPrimary
                                    )
                                    Text(
                                        text = notif.timestamp,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsSheet = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Edit Price Dialog
    if (showEditPriceDialogFor != null) {
        val targetItem = showEditPriceDialogFor!!
        var newPriceInput by remember { mutableStateOf(targetItem.price.toString()) }

        AlertDialog(
            onDismissRequest = { showEditPriceDialogFor = null },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = TunisiaRedPrimary) },
            title = { Text("تعديل سعر المنتج / Edit Price", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = targetItem.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPriceInput,
                        onValueChange = { newPriceInput = it },
                        label = { Text("New Price in TND (د.ت)") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = TunisiaRedPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_price_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = newPriceInput.toDoubleOrNull()
                        if (parsed != null && parsed >= 0) {
                            onUpdatePrice(targetItem.id, parsed)
                            showEditPriceDialogFor = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary)
                ) {
                    Text("Save New Price 💾")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPriceDialogFor = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProductGridCard(
    item: MarketplaceItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEditPrice: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_card_${item.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = item.images.firstOrNull() ?: "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Photos Count Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${item.images.size} Photos", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (item.isMine) {
                    Surface(
                        color = TunisiaRedPrimary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "My Product",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (item.isSold) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color.Red,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "SOLD / تم البيع",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "${item.price} TND",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = TunisiaRedPrimary
                )
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(item.location, fontSize = 11.sp, color = Color.Gray)
                }

                if (item.isMine) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onEditPrice,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Price", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Product", tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailModal(
    item: MarketplaceItem,
    onDismiss: () -> Unit,
    onContactSeller: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onEditPrice: (MarketplaceItem) -> Unit,
    onMarkSold: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { item.images.size })

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Image Carousel (Up to 10 images)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = item.images[page],
                        contentDescription = "Product Photo ${page + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Page Indicator Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${item.images.size} Photos",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.price} TND 🇹🇳",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TunisiaRedPrimary
                        )
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${item.location}, Tunisia", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = item.postedTime, color = Color.Gray, fontSize = 13.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(text = "Description:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Seller Box
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    AsyncImage(
                        model = item.sellerAvatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.sellerName, fontWeight = FontWeight.Bold)
                        Text(item.sellerPhone, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Owner Actions vs Contact Action
                if (item.isMine) {
                    Text("Owner Actions / أدوات صاحب المنتج:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onEditPrice(item) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Price")
                        }
                        Button(
                            onClick = { onDelete(item.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete (فسخ)")
                        }
                    }
                } else {
                    Button(
                        onClick = { onContactSeller(item.sellerName, item.sellerPhone) },
                        colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("contact_seller_button")
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contact Seller via Zahrouni Chat 💬", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddProduct: (String, String, Double, String, String, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("Tunis") }
    var selectedCategory by remember { mutableStateOf("Phones & Tech") }
    val imageUrls = remember { mutableStateListOf<String>() }
    var sampleImageUrlInput by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            if (imageUrls.size < 10) {
                imageUrls.add(uri.toString())
            }
        }
    }

    val categories = listOf("Phones & Tech", "Electronics", "Vehicles", "Home & Goods", "Fashion", "Real Estate")

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Storefront, contentDescription = null, tint = TunisiaRedPrimary, modifier = Modifier.size(32.dp)) },
        title = {
            Text(
                "إضافة منتج للبيع / Add Product 🇹🇳",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Product Title (اسم المنتج)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("add_product_title_input")
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price in TND (السعر بـ د.ت)") },
                    leadingIcon = { Text("TND", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("add_product_price_input")
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Location selector
                Text("Governorate / Location:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(TunisiaCities.list) { city ->
                        FilterChip(
                            selected = selectedLocation == city,
                            onClick = { selectedLocation = city },
                            label = { Text(city, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Category selector
                Text("Category:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (الوصف)") },
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Up to 10 Images Upload Section
                Text(
                    text = "Product Photos (${imageUrls.size}/10 Max):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        enabled = imageUrls.size < 10,
                        onClick = { photoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = TunisiaRedPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gallery", color = MaterialTheme.colorScheme.onSurface)
                    }

                    // Quick Sample Image Adder for easy testing
                    Button(
                        enabled = imageUrls.size < 10,
                        onClick = {
                            val samples = listOf(
                                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600",
                                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600",
                                "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600"
                            )
                            imageUrls.add(samples[imageUrls.size % samples.size])
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Sample Pic")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Image Thumbnails Horizontal Row
                if (imageUrls.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(imageUrls.size) { idx ->
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, TunisiaRedPrimary, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = imageUrls[idx],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { imageUrls.removeAt(idx) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .background(Color.Red, CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val parsedPrice = price.toDoubleOrNull() ?: 100.0
                        onAddProduct(
                            title,
                            if (description.isBlank()) "Product listed on Zahrouni Chat Marketplace." else description,
                            parsedPrice,
                            selectedLocation,
                            selectedCategory,
                            imageUrls.toList()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TunisiaRedPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Publish Listing 🚀", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
