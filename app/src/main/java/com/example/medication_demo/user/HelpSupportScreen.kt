package com.example.medication_demo.user

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.ui.AppTopBar
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val HelpSupportGreen = Color(0xFF159447)

data class FaqItem(
    val question: String,
    val answer: String
)

private val faqItems = listOf(
    FaqItem(
        question = "Why did my medication reminders suddenly stop?",
        answer = "Reminders are based on how many doses you have left, " +
                "calculated from your quantity and dosage. Once you've used " +
                "up the full quantity you entered, the app stops sending " +
                "reminders for that medicine — this is expected, not a bug. " +
                "If you're still taking it, add more stock via Refill so the " +
                "app knows to keep reminding you."
    ),

    FaqItem(
        question = "Why am I not getting reminders for medicines set to \"As Needed\"?",
        answer = "\"As Needed\" medicines don't follow a fixed schedule, so " +
                "the app doesn't send scheduled reminders for them. Instead, " +
                "go to the medicine's detail page and tap the checkmark to " +
                "mark a dose as taken whenever you actually take it. Since " +
                "there's no fixed schedule, it won't show up on your " +
                "Medicine Schedule — but it will still be recorded and " +
                "visible in your Weekly History."
    ),

    FaqItem(
        question = "Why did I only get one low-stock notification even though my stock is still low?",
        answer = "The low-stock alert only fires the moment your stock " +
                "crosses the threshold from above to at-or-below it — not " +
                "continuously while it stays low. So if you don't refill " +
                "right away, you won't get repeated nagging reminders; " +
                "you'll only be notified again once you refill above the " +
                "threshold and it drops below again. If your stock reaches " +
                "0, you'll get a one-time alert that it's insufficient for " +
                "the next dose — but you'll need to manually go into the " +
                "medicine's details to update the quantity yourself; the " +
                "app won't keep reminding you after that single alert."
    ),

    FaqItem(
        question = "How is the low-stock threshold decided? Can I change it?",
        answer = "You set it yourself — when adding or editing a medicine, " +
                "turning on Refill Reminder lets you enter the quantity " +
                "that should trigger the \"running low\" alert. It's not a " +
                "fixed system default, so you're free to change it anytime " +
                "by editing the medicine."
    ),

    FaqItem(
        question = "If I change a medicine's dosage, will my past history records change too?",
        answer = "No — each history record stores a snapshot of the " +
                "dosage at the time you took it, so past records stay " +
                "exactly as they were. Editing a medicine's dosage only " +
                "affects future doses going forward; your Weekly History " +
                "won't be rewritten."
    ),

    FaqItem(
        question = "Why is my appointment not displayed under \"Upcoming\"?",
        answer = "The appointment may have an incorrect date or may already " +
                "be classified as a previous appointment. Check the " +
                "appointment details and your phone's date and time settings."
    ),

    FaqItem(
        question = "Is the recommended water goal suitable for everyone?",
        answer = "No. Water requirements vary according to health condition, " +
                "activity level, weather and medical advice. Consult a " +
                "healthcare professional if you are unsure about an " +
                "appropriate daily goal."
    ),

    FaqItem(
        question = "How is my monthly medication adherence calculated?",
        answer = "Medication adherence is calculated by comparing the number " +
                "of doses recorded as taken with the total number of scheduled " +
                "doses during the selected month."
    )
)

@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Help & Support",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            item {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HelpSupportGreen
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(faqItems) { faq ->
                FaqExpandableItem(faq = faq)
            }
        }
    }
}

@Composable
private fun FaqExpandableItem(faq: FaqItem) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = HelpSupportGreen
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = faq.answer,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp
                )
            }
        }

        androidx.compose.material3.HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.7.dp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HelpSupportScreenPreview() {
    Medication_DemoTheme {
        HelpSupportScreen()
    }
}