package com.example.data.model

data class GroundingChunk(
    val title: String,
    val url: String
)

data class SearchGroundedResult(
    val query: String,
    val responseText: String,
    val searchQueries: List<String> = emptyList(),
    val sources: List<GroundingChunk> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
