package com.example.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.LocalGlassTheme

data class GlassTodoItem(
    val id: Int,
    val text: String,
    val isDone: Boolean
)

@Composable
fun GlassNotesWidget(
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current

    val tasks = remember {
        mutableStateListOf(
            GlassTodoItem(1, "Review glassmorphism design tokens", true),
            GlassTodoItem(2, "Test smooth sweeping clock hands", true),
            GlassTodoItem(3, "Sync Android Home Screen widget", false),
            GlassTodoItem(4, "Configure ambient Dim twilight theme", false)
        )
    }

    var isAddingTask by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }

    GlassBox(
        modifier = modifier
            .fillMaxWidth()
            .testTag("glass_notes_widget"),
        shape = RoundedCornerShape(26.dp),
        elevation = 10.dp,
        glassAlpha = 0.16f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EditNote,
                        contentDescription = "Notes",
                        tint = tokens.action,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Glass Focus Tasks",
                        color = tokens.content,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                GlassBox(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    elevation = 2.dp,
                    glassAlpha = 0.18f,
                    onClick = { isAddingTask = !isAddingTask }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Task",
                        tint = tokens.action,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Quick add input field
            AnimatedVisibility(visible = isAddingTask) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(tokens.light.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newTaskText,
                        onValueChange = { newTaskText = it },
                        placeholder = { Text("Enter task...", color = tokens.contentSubtle, fontSize = 13.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = tokens.content,
                            unfocusedTextColor = tokens.content,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    GlassBox(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        elevation = 2.dp,
                        glassAlpha = 0.35f,
                        onClick = {
                            if (newTaskText.isNotBlank()) {
                                tasks.add(GlassTodoItem(tasks.size + 1, newTaskText.trim(), false))
                                newTaskText = ""
                                isAddingTask = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Confirm",
                            tint = tokens.action,
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            // Task list items
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEachIndexed { index, task ->
                    GlassTodoRow(
                        task = task,
                        tokens = tokens,
                        onToggle = {
                            tasks[index] = task.copy(isDone = !task.isDone)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassTodoRow(
    task: GlassTodoItem,
    tokens: com.example.ui.theme.GlassColorTokens,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(tokens.light.copy(alpha = if (task.isDone) 0.05f else 0.09f))
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = if (task.isDone) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
            contentDescription = if (task.isDone) "Done" else "Pending",
            tint = if (task.isDone) tokens.action else tokens.contentSubtle,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = task.text,
            color = if (task.isDone) tokens.contentSubtle else tokens.content,
            fontSize = 13.sp,
            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
            fontWeight = if (task.isDone) FontWeight.Normal else FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}
