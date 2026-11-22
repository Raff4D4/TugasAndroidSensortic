package com.zenspace.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zenspace.app.model.FocusSession
import com.zenspace.app.model.SessionStats

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "zenspace.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE focus_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                category TEXT NOT NULL,
                duration_seconds INTEGER NOT NULL,
                average_lux REAL NOT NULL,
                light_status TEXT NOT NULL,
                distraction_count INTEGER NOT NULL,
                stability_score INTEGER NOT NULL,
                created_at INTEGER NOT NULL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS focus_sessions")
        onCreate(db)
    }

    fun insertSession(session: FocusSession): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", session.title)
            put("category", session.category)
            put("duration_seconds", session.durationSeconds)
            put("average_lux", session.averageLux)
            put("light_status", session.lightStatus)
            put("distraction_count", session.distractionCount)
            put("stability_score", session.stabilityScore)
            put("created_at", session.createdAtMillis)
        }
        return db.insert("focus_sessions", null, values)
    }

    fun getAllSessions(categoryFilter: String? = null): List<FocusSession> {
        val db = readableDatabase
        val sessions = mutableListOf<FocusSession>()
        
        val applyFilter = !categoryFilter.isNullOrBlank() && categoryFilter != "Semua"
        val selection = if (applyFilter) "category = ?" else null
        val selectionArgs = if (applyFilter) arrayOf(categoryFilter) else null

        db.query(
            "focus_sessions",
            null,
            selection,
            selectionArgs,
            null,
            null,
            "created_at DESC"
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val session = FocusSession(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        durationSeconds = cursor.getInt(cursor.getColumnIndexOrThrow("duration_seconds")),
                        averageLux = cursor.getFloat(cursor.getColumnIndexOrThrow("average_lux")),
                        lightStatus = cursor.getString(cursor.getColumnIndexOrThrow("light_status")),
                        distractionCount = cursor.getInt(cursor.getColumnIndexOrThrow("distraction_count")),
                        stabilityScore = cursor.getInt(cursor.getColumnIndexOrThrow("stability_score")),
                        createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                    )
                    sessions.add(session)
                } while (cursor.moveToNext())
            }
        }
        return sessions
    }

    fun getStats(): SessionStats {
        val db = readableDatabase
        var stats = SessionStats()
        db.rawQuery("""
            SELECT 
                COUNT(*) as total_sessions,
                COALESCE(SUM(duration_seconds), 0) as total_seconds,
                COALESCE(AVG(average_lux), 0) as average_lux,
                COALESCE(SUM(distraction_count), 0) as total_distractions
            FROM focus_sessions
        """, null).use { cursor ->
            if (cursor.moveToFirst()) {
                stats = SessionStats(
                    totalSessions = cursor.getInt(cursor.getColumnIndexOrThrow("total_sessions")),
                    totalSeconds = cursor.getInt(cursor.getColumnIndexOrThrow("total_seconds")),
                    averageLux = cursor.getFloat(cursor.getColumnIndexOrThrow("average_lux")),
                    totalDistractions = cursor.getInt(cursor.getColumnIndexOrThrow("total_distractions"))
                )
            }
        }
        return stats
    }

    fun updateSessionTitle(id: Long, newTitle: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", newTitle)
        }
        return db.update("focus_sessions", values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteSession(id: Long): Int {
        val db = writableDatabase
        return db.delete("focus_sessions", "id = ?", arrayOf(id.toString()))
    }

    fun clearAllSessions(): Int {
        val db = writableDatabase
        return db.delete("focus_sessions", null, null)
    }
}
