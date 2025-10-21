package com.example.tugasmokom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen(navController: NavController, viewModel: NotesViewModel = viewModel()) {
    var textParts by remember { mutableStateOf(listOf<AnnotatedString>()) }
    var currentStyle by remember {
        mutableStateOf(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal))
    }
    var inputText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val fontSizes = listOf(12, 14, 16, 18, 20, 24, 28, 32)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Text Editor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        textParts = emptyList()
                        inputText = ""
                        scope.launch { snackbarHostState.showSnackbar("New note created 📝") }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "New Note", tint = Color.White)
                    }
                    IconButton(onClick = {
                        if (inputText.isNotBlank() || textParts.isNotEmpty()) {
                            val finalText = (textParts.joinToString(" ") { it.text } + " " + inputText).trim()
                            viewModel.addNote(finalText)
                            scope.launch { snackbarHostState.showSnackbar("Note saved 💾") }
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3F51B5),
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8F8F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(onClick = {
                        currentStyle = currentStyle.copy(
                            fontWeight = if (currentStyle.fontWeight == FontWeight.Bold)
                                FontWeight.Normal else FontWeight.Bold
                        )
                    }) {
                        Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = Color.Black)
                    }

                    IconButton(onClick = {
                        currentStyle = currentStyle.copy(
                            fontStyle = if (currentStyle.fontStyle == FontStyle.Italic)
                                FontStyle.Normal else FontStyle.Italic
                        )
                    }) {
                        Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = Color.Black)
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = "${currentStyle.fontSize.value.toInt()} pt",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Font") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor().width(120.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        fontSizes.forEach { size ->
                            DropdownMenuItem(
                                text = { Text("$size pt") },
                                onClick = {
                                    currentStyle = currentStyle.copy(fontSize = size.sp)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    textParts.forEach { part ->
                        Text(text = part)
                    }

                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = currentStyle.fontSize,
                            fontWeight = currentStyle.fontWeight,
                            fontStyle = currentStyle.fontStyle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        textParts = textParts + AnnotatedString(inputText, currentStyle)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(48.dp)
            ) {
                Text("Add Text")
            }
        }
    }
}
