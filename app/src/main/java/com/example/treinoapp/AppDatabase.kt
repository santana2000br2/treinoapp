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
        MedicaoMensalEntity::class,
        FotoEvolucaoSessaoEntity::class,
        FotoEvolucaoFotoEntity::class,
        TreinoProntoEntity::class,
        TreinoProntoItemEntity::class,
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modeloDao(): ModeloDao
    abstract fun treinoDiaDao(): TreinoDiaDao
    abstract fun dietaAlimentoDao(): DietaAlimentoDao
    abstract fun pesoEvolucaoDao(): PesoEvolucaoDao
    abstract fun medicaoMensalDao(): MedicaoMensalDao
    abstract fun treinoConclusaoDao(): TreinoConclusaoDao
    abstract fun fotoEvolucaoDao(): FotoEvolucaoDao
    abstract fun treinoProntoDao(): TreinoProntoDao

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

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE treino_pronto_item ADD COLUMN diaSemana TEXT NOT NULL DEFAULT ''",
                )
                db.execSQL(
                    "ALTER TABLE treino_pronto_item ADD COLUMN descricaoOverride TEXT",
                )
                db.execSQL(
                    "DROP INDEX IF EXISTS index_treino_pronto_item_plano_modelo",
                )
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_treino_pronto_item_plano_modelo_dia
                    ON treino_pronto_item(treinoProntoId, modeloId, diaSemana)
                    """.trimIndent(),
                )
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS treino_pronto (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nome TEXT NOT NULL,
                        descricao TEXT NOT NULL DEFAULT ''
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS treino_pronto_item (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        treinoProntoId INTEGER NOT NULL,
                        modeloId INTEGER NOT NULL,
                        ordem INTEGER NOT NULL,
                        FOREIGN KEY(treinoProntoId) REFERENCES treino_pronto(id) ON DELETE CASCADE,
                        FOREIGN KEY(modeloId) REFERENCES modelos(id) ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_treino_pronto_item_treinoProntoId ON treino_pronto_item(treinoProntoId)",
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_treino_pronto_item_plano_modelo ON treino_pronto_item(treinoProntoId, modeloId)",
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS foto_evolucao_sessao (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        dataMillis INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS foto_evolucao_foto (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sessaoId INTEGER NOT NULL,
                        posicao TEXT NOT NULL,
                        uriLocal TEXT NOT NULL,
                        FOREIGN KEY(sessaoId) REFERENCES foto_evolucao_sessao(id) ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_foto_evolucao_foto_sessaoId ON foto_evolucao_foto(sessaoId)",
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_foto_evolucao_foto_sessaoId_posicao ON foto_evolucao_foto(sessaoId, posicao)",
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS medicao_mensal (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        mesReferencia TEXT NOT NULL,
                        dataMillis INTEGER NOT NULL,
                        pesoKg REAL,
                        alturaCm REAL,
                        cinturaCm REAL,
                        abdomenCm REAL,
                        peitoCm REAL,
                        quadrilCm REAL,
                        bracoEsquerdoCm REAL,
                        bracoDireitoCm REAL,
                        coxaEsquerdaCm REAL,
                        coxaDireitaCm REAL,
                        panturrilhaEsquerdaCm REAL,
                        panturrilhaDireitaCm REAL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_medicao_mensal_mesReferencia ON medicao_mensal(mesReferencia)",
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO medicao_mensal (mesReferencia, dataMillis, pesoKg)
                    SELECT
                        strftime('%Y-%m', dataMillis / 1000, 'unixepoch'),
                        MAX(dataMillis),
                        pesoKg
                    FROM peso_evolucao
                    GROUP BY strftime('%Y-%m', dataMillis / 1000, 'unixepoch')
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
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}