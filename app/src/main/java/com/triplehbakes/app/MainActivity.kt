package com.triplehbakes.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Data Models ---
data class PriceOption(val label: String, val price: Int)

data class MenuItem(
    val id: Int,
    val name: String,
    val options: List<PriceOption>
)

data class CartItem(
    val item: MenuItem,
    val selectedOption: PriceOption,
    var count: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleHBakesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2B1B17)
                ) {
                    BakingAppMainScreen()
                }
            }
        }
    }
}

// --- Theme Definition ---
@Composable
fun TripleHBakesTheme(content: @Composable () -> Unit) {
    val darkChocolateScheme = darkColorScheme(
        primary = Color(0xFFD7CCC8),
        secondary = Color(0xFF8D6E63),
        background = Color(0xFF2B1B17),
        surface = Color(0xFF3E2723),
        onPrimary = Color(0xFF2B1B17),
        onSurface = Color(0xFFF5E6DA)
    )
    MaterialTheme(colorScheme = darkChocolateScheme, content = content)
}

// --- Menu Data ---
val sampleMenu = listOf(
    MenuItem(
        1, "Fudge Brownie",
        listOf(PriceOption("Per piece", 45), PriceOption("Quarter Kg", 200), PriceOption("Half Kg", 380))
    ),
    MenuItem(
        2, "Triple Chocolate Brownie",
        listOf(PriceOption("Per piece", 60), PriceOption("Quarter Kg", 250), PriceOption("Half Kg", 450))
    ),
    MenuItem(
        3, "Nuts Overloaded Brownie",
        listOf(PriceOption("Per piece", 60), PriceOption("Quarter Kg", 250), PriceOption("Half Kg", 450))
    ),
    MenuItem(
        4, "White Chocolate Blondie",
        listOf(PriceOption("Per piece", 55), PriceOption("Quarter Kg", 250), PriceOption("Half Kg", 430))
    ),
    MenuItem(
        5, "Tea Cake",
        listOf(PriceOption("Quarter Kg", 130), PriceOption("Half Kg", 250))
    ),
    MenuItem(
        6, "Chocolate Cake",
        listOf(PriceOption("Quarter Kg", 130), PriceOption("Half Kg", 250))
    ),
    MenuItem(
        7, "Banana Cake",
        listOf(PriceOption("Quarter Kg", 130), PriceOption("Half Kg", 250))
    )
)

// --- Main UI Layout ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BakingAppMainScreen() {
    val cart = remember { mutableStateListOf<CartItem>() }
    var showCartScreen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Triple H Bakes", color = Color(0xFFF5E6DA), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C0D0A)),
                actions = {
                    Button(
                        onClick = { showCartScreen = !showCartScreen },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
                    ) {
                        Text(if (showCartScreen) "Menu" else "Cart (${cart.sumOf { it.count }})")
                    }
                }
            )
        },
        containerColor = Color(0xFF2B1B17)
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (showCartScreen) {
                CartScreen(cart = cart, onOrderPlaced = { cart.clear(); showCartScreen = false })
            } else {
                MenuListScreen(onAddToCart = { item, option ->
                    val existing = cart.find { it.item.id == item.id && it.selectedOption.label == option.label }
                    if (existing != null) {
                        existing.count++
                    } else {
                        cart.add(CartItem(item, option, 1))
                    }
                })
            }
        }
    }
}

// --- Menu View ---
@Composable
fun MenuListScreen(onAddToCart: (MenuItem, PriceOption) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sampleMenu) { item ->
            MenuItemCard(item = item, onAddToCart = onAddToCart)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(item: MenuItem, onAddToCart: (MenuItem, PriceOption) -> Unit) {
    var selectedOption by remember { mutableStateOf(item.options.first()) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2723)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5E6DA))
            Spacer(modifier = Modifier.height(12.dp))
            
            // Option Selection Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = "${selectedOption.label} - Rs ${selectedOption.price}",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFFF5E6DA),
                        unfocusedTextColor = Color(0xFFF5E6DA),
                        focusedContainerColor = Color(0xFF2B1B17),
                        unfocusedContainerColor = Color(0xFF2B1B17)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    item.options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text("${option.label} - Rs ${option.price}") },
                            onClick = {
                                selectedOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    onAddToCart(item, selectedOption)
                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
            ) {
                Text("Add to Cart", color = Color(0xFFF5E6DA))
            }
        }
    }
}

// --- Cart & Checkout View ---
@Composable
fun CartScreen(cart: List<CartItem>, onOrderPlaced: () -> Unit) {
    val context = LocalContext.current
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }

    val subtotal = cart.sumOf { it.selectedOption.price * it.count }

    if (cart.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Your cart is empty!", color = Color(0xFFF5E6DA), fontSize = 18.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Order Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5E6DA))
            }

            items(cart) { cartItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3E2723), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(cartItem.item.name, color = Color(0xFFF5E6DA), fontWeight = FontWeight.Bold)
                        Text("${cartItem.selectedOption.label} x ${cartItem.count}", color = Color(0xFFD7CCC8))
                    }
                    Text("Rs ${cartItem.selectedOption.price * cartItem.count}", color = Color(0xFFF5E6DA), fontWeight = FontWeight.Bold)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Subtotal: Rs $subtotal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5E6DA))
                
                // Delivery Charge Notice
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E342E)),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "ℹ️ Free shipping up to 3 kms. Above 3km charges applicable as per the distance.",
                        color = Color(0xFFFFCC80),
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Customer Details (Cash On Delivery)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5E6DA))
                
                OutlinedTextField(
                    value = customerName, onValueChange = { customerName = it },
                    label = { Text("Name") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = customerPhone, onValueChange = { customerPhone = it },
                    label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = customerAddress, onValueChange = { customerAddress = it },
                    label = { Text("Delivery Address") }, modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (customerName.isNotBlank() && customerPhone.isNotBlank() && customerAddress.isNotBlank()) {
                            val orderSummary = cart.joinToString("\n") { 
                                "- ${it.item.name} (${it.selectedOption.label}) x ${it.count} = Rs ${it.selectedOption.price * it.count}" 
                            }
                            val emailBody = """
                                New COD Order Received for Triple H Bakes!
                                
                                Customer Info:
                                Name: $customerName
                                Phone: $customerPhone
                                Address: $customerAddress
                                
                                Items Ordered:
                                $orderSummary
                                
                                Total Amount: Rs $subtotal
                                Payment Method: Cash on Delivery
                            """.trimIndent()

                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:onlinejob1712@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "New COD Order - Triple H Bakes")
                                putExtra(Intent.EXTRA_TEXT, emailBody)
                            }
                            context.startActivity(intent)

                            Toast.makeText(context, "Order Placed! Please send the confirmation email.", Toast.LENGTH_LONG).show()
                            onOrderPlaced()
                        } else {
                            Toast.makeText(context, "Please fill out all details", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
                ) {
                    Text("Place Cash On Delivery Order", fontSize = 16.sp, color = Color(0xFFF5E6DA))
                }
            }
        }
    }
}
