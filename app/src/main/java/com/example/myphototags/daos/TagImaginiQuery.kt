package com.example.myphototags.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.myphototags.entities.TagImagini

@Dao
interface TagImaginiQuery {
    @Query("SELECT * FROM TagImagini")
    fun getALL(): List<TagImagini>

    @Insert
    fun insertAll(vararg tagImagini: TagImagini)

    @Delete
    fun delete(tagImagini: TagImagini)
}