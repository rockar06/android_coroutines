package com.rockar.coroutines.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Title(
    val title: String,
    @PrimaryKey val id: Int = 0
)
