package com.example.tugasmokom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Tombol Kalkulator Biasa
val normalButtonRows = listOf(
    listOf("(", ")", "⌫", "AC"),
    listOf("7", "8", "9", "/"),
    listOf("4", "5", "6", "X"),
    listOf("1", "2", "3", "-"),
    listOf("0", ".", "π", "+"),
    listOf("SC", "=")
)

// Tombol Kalkulator Scientific
val scientificButtonRows = listOf(
    listOf("(", ")", "LN", "LOG", "1/x"),
    listOf("ASIN", "ACOS", "ATAN", "x^y", "x!"),
    listOf("SIN", "COS", "TAN", "SQRT", "%"),
    listOf("7", "8", "9", "⌫", "AC"),
    listOf("4", "5", "6", "X", "/"),
    listOf("1", "2", "3", "+", "-"),
    listOf("0", ".", "π", "NOR", "=")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calculator(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel,
    navController: NavController
) {
    val equationText = viewModel.equationText.collectAsState()
    val resultText = viewModel.resultText.collectAsState()
    val isScientific = viewModel.isScientific.collectAsState()

    val currentButtonRows = if (isScientific.value) scientificButtonRows else normalButtonRows

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        // 🔹 TopAppBar dengan tombol kembali
        TopAppBar(
            title = { Text("Scientific Calculator") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF3F51B5),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF3F51B5))
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = equationText.value,
                fontSize = 24.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = resultText.value,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currentButtonRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { symbol ->

                        var weight = 1f

                        val isLastNormRow = row == normalButtonRows.last()
                        if (isLastNormRow) {
                            if (symbol == "SC") weight = 2f
                            else if (symbol == "=") weight = 3f
                        } else if (row == scientificButtonRows.last()) {
                            weight = 1f
                        }

                        val buttonSymbol = when (symbol) {
                            "SC" -> "SC"
                            "NOR" -> "NOR"
                            else -> symbol
                        }

                        CalcButton(
                            symbol = buttonSymbol,
                            onClick = { viewModel.onButtonClick(buttonSymbol) },
                            modifier = Modifier.weight(weight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalcButton(symbol: String, onClick: () -> Unit, modifier: Modifier) {
    val buttonColor = when (symbol) {
        "AC" -> Color(0xFFEF9A9A)
        "⌫", "/", "X", "+", "-", "%" -> Color(0xFFC5CAE9)
        "=" -> Color(0xFF3F51B5)
        "SC", "NOR" -> Color(0xFF9FA8DA)
        in listOf("7", "8", "9", "4", "5", "6", "1", "2", "3", "0", ".", "π", "ANS") -> Color.White
        else -> Color(0xFFE0E0E0)
    }

    val contentColor = when (symbol) {
        "=" -> Color.White
        "AC" -> Color(0xFFD32F2F)
        else -> Color(0xFF212121)
    }

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = symbol,
            fontSize = 18.sp,
            fontWeight = if (symbol == "=") FontWeight.Bold else FontWeight.Medium
        )
    }
}
