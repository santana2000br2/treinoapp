package com.example.treinoapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ModeloEntity::class,
        TreinoDiaEntity::class,
        DietaAlimentoEntity::class,
        PesoEvolucaoEntity::class,
        TreinoConclusaoEntity::class,
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modeloDao(): ModeloDao
    abstract fun treinoDiaDao(): TreinoDiaDao
    abstract fun dietaAlimentoDao(): DietaAlimentoDao
    abstract fun pesoEvolucaoDao(): PesoEvolucaoDao
    abstract fun treinoConclusaoDao(): TreinoConclusaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS dieta_alimentos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tipoRefeicao TEXT NOT NULL,
                        nome TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE dieta_alimentos ADD COLUMN quantidade INTEGER NOT NULL DEFAULT 1"
                )
                db.execSQL(
                    "ALTER TABLE dieta_alimentos ADD COLUMN unidadeMedida TEXT NOT NULL DEFAULT '" +
                        DietaUnidadeMedida.GRAMAS + "'"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS peso_evolucao (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        pesoKg REAL NOT NULL,
                        dataMillis INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS treino_conclusao (
                        dataYmd TEXT NOT NULL,
                        treinoDiaId INTEGER NOT NULL,
                        PRIMARY KEY(dataYmd, treinoDiaId)
                    )
                    """.trimIndent(),
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "treino_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}