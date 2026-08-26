package com.example.medication_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.history.WeeklyHistoryScreen
import com.example.medication_demo.medication.AddMedicineScreen
import com.example.medication_demo.medication.MedicineCalendarScreen
import com.example.medication_demo.medication.MedicineDetailsScreen
import com.example.medication_demo.medication.MedicineListScreen
import com.example.medication_demo.medication.MedicineScheduleScreen
import com.example.medication_demo.reminder.RefillReminderScreen
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import com.example.medication_demo.viewmodel.MedicineListViewModel
import com.example.medication_demo.viewmodel.MedicineViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Medication_DemoTheme {
                val medicineViewModel: MedicineViewModel = viewModel()
                val medicineListViewModel: MedicineListViewModel = viewModel()
                var currentScreen by remember {
                    mutableStateOf("list")
                }
                when (currentScreen) {
                    "list" -> {
                        MedicineListScreen(
                            medicineVm = medicineViewModel,
                            listVm = medicineListViewModel,
                            onAddMedicineClick = {
                                currentScreen = "add"
                            }
                        )
                    }
                    "add" -> {
                        AddMedicineScreen(
                            isEditMode = false,
                            vm = medicineViewModel,
                            onBackClick = {
                                medicineViewModel.resetAddMedicineForm()
                                currentScreen = "list"
                            },
                            onSaveClick = {
                                medicineViewModel.resetAddMedicineForm()
                                currentScreen = "list"
                            }
                        )
                    }
                }
                //RefillReminderScreen()
                //WeeklyHistoryScreen()
                //MedicineCalendarScreen()
                //MedicineScheduleScreen()
                //MedicineDetailsScreen()
                //MedicineListScreen()
                /*AddMedicineScreen(
                    isEditMode = false
                )*/
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Medication_DemoTheme {
        Greeting("Android")
    }
}