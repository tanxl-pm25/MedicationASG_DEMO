package com.example.medication_demo.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    showMoreMenu: Boolean = false,
    onHelpClick: (() -> Unit)? = null
) {

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    CenterAlignedTopAppBar(

        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },

        navigationIcon = {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },

        actions = {

            if (showMoreMenu) {

                IconButton(
                    onClick = {
                        menuExpanded = true
                    }
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {

                    if (onHelpClick != null) {
                        DropdownMenuItem(
                            text = {
                                Text("Help")
                            },
                            onClick = {
                                menuExpanded = false
                                onHelpClick()
                            }
                        )
                    }
                }
            }
        }
    )
}