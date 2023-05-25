package com.example.myphototags.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myphototags.daos.ImaginiQuery
import com.example.myphototags.daos.TagImaginiQuery
import com.example.myphototags.daos.TagsQuery
import com.example.myphototags.entities.Converters
import com.example.myphototags.entities.Imagini
import com.example.myphototags.entities.TagImagini
import com.example.myphototags.entities.Tags

@Database(entities = arrayOf(Tags::class, Imagini::class, TagImagini::class), version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun tagsDao(): TagsQuery
    abstract fun imaginiDao(): ImaginiQuery
    abstract fun tagImaginiDao(): TagImaginiQuery
}