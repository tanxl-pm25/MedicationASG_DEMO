package com.example.medication_demo.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.medication_demo.R
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val AppGreen = Color(0xFF17852B)
private val LightGreen = Color(0xFFE8F5E9)
private val TextGrey = Color(0xFF6B7280)

private data class BottomItem(
    val label: String,
    val icon: ImageVector? = null
)

@Composable
fun AppBottomNavigationBar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {

    val items = listOf(
        BottomItem(
            label = "Home",
            icon = Icons.Default.Home
        ),

        BottomItem(
            label = "Medicine"
        ),

        BottomItem(
            label = "History",
            icon = Icons.Default.History
        ),

        BottomItem(
            label = "Profile",
            icon = Icons.Default.PersonOutline
        )
    )

    Column {

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background
        ) {

            items.forEachIndexed { index, item ->

                NavigationBarItem(
                    selected = selectedIndex == index,

                    onClick = {
                        onSelected(index)
                    },

                    icon = {

                        if (item.label == "Medicine") {

                            Icon(
                                painter = painterResource(
                                    R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24
                                ),
                                contentDescription = item.label
                            )

                        } else {

                            Icon(
                                imageVector = item.icon!!,
                                contentDescription = item.label
                            )
                        }
                    },

                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp
                        )
                    },

                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = AppGreen,
                            selectedTextColor = AppGreen,
                            indicatorColor = LightGreen,
                            unselectedIconColor = TextGrey,
                            unselectedTextColor = TextGrey
                        )
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun AppBottomNavigationBarPreview() {
    Medication_DemoTheme {
        AppBottomNavigationBar(
            selectedIndex = 0,
            onSelected = {}
        )
    }
}