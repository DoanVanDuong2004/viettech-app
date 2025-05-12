package com.datn.viettech_md_12.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.common.SortOption
import com.datn.viettech_md_12.component.item.CustomItemProducts
import com.datn.viettech_md_12.viewmodel.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    searchViewModel: SearchViewModel = koinViewModel()
) {
    val text = remember { mutableStateOf("") }
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isLoading by searchViewModel.isLoading.collectAsState()
    val errorMessage by searchViewModel.errorMessage.collectAsState()
    val shouldCloseBottomSheet by searchViewModel.shouldCloseBottomSheet.collectAsState()
    val history by searchViewModel.searchHistory.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSortOption by remember { mutableStateOf(SortOption.AZ) }
    var tempSortOption by remember { mutableStateOf(selectedSortOption) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(shouldCloseBottomSheet) {
        if (shouldCloseBottomSheet) {
            bottomSheetState.hide()
            showBottomSheet = false
            searchViewModel.onBottomSheetClosed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                modifier = Modifier.padding(start = 16.dp),
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "logo",
                        tint = Color(0xFF309A5F),
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "close",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
            )
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                text = text,
                onValueChanged = { newText ->
                    debounceJob?.cancel()
                    if (newText.isNotEmpty()) {
                        debounceJob = coroutineScope.launch {
                            delay(500)
                            searchViewModel.searchProducts(newText)
                            searchViewModel.saveToHistory(newText)
                        }
                    } else {
                        searchViewModel.clearSearchResults()
                    }
                },
                onFilterClick = {
                    tempSortOption = selectedSortOption
                    showBottomSheet = true
                },
                onClearText = {
                    searchViewModel.clearSearchResults()
                }
            )

            if (text.value.isEmpty() && history.isNotEmpty()) {
                SearchHistorySection(
                    history = history,
                    onClickItem = { item ->
                        text.value = item
                        searchViewModel.searchProducts(item)
                    },
                    onClearAll = {
                        searchViewModel.clearSearchHistory()
                    }
                )
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage ?: "Đã xảy ra lỗi", color = Color.Red)
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        items(searchResults) { product ->
                            CustomItemProducts(
                                product = product,
                                onClick = {
                                    navController.navigate("product_detail/${product.id}")
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = bottomSheetState,
                containerColor = Color.White
            ) {
                FilterBottomSheet(
                    selectedOption = tempSortOption,
                    onOptionSelected = { tempSortOption = it },
                    onApplyClick = {
                        selectedSortOption = tempSortOption
                        searchViewModel.applySort(selectedSortOption)
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}
