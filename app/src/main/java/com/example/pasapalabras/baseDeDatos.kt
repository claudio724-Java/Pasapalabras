package com.example.pasapalabras

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class baseDeDatos(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(
            """
            CREATE TABLE Usuarios(
                email TEXT PRIMARY KEY,
                nombreUsuario TEXT NOT NULL,
                contraseña TEXT NOT NULL,
                foto BLOB,
                preferencias TEXT
            )
            """.trimIndent()
        )

        // Crear tabla Ranking
        db?.execSQL(
            """
            CREATE TABLE Ranking(
                idRanking INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL,
                nivel TEXT NOT NULL,
                puntuacion INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                FOREIGN KEY(email) REFERENCES Usuarios(email) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db?.execSQL(
            """
            CREATE TABLE Preguntas(
                idPregunta INTEGER PRIMARY KEY AUTOINCREMENT,
                letra TEXT NOT NULL,
                tipo TEXT NOT NULL,
                pregunta TEXT NOT NULL,
                respuesta TEXT NOT NULL,
                nivel TEXT NOT NULL,
                CHECK (
                    (tipo = 'empieza' AND respuesta LIKE letra || '%') OR
                    (tipo = 'contiene' AND respuesta LIKE '%' || letra || '%') OR
                    (tipo = 'termina' AND respuesta LIKE '%' || letra)
                )
            )
            """.trimIndent()
        )
        val preguntasFacil = listOf(
            ContentValues().apply {
                put("letra", "A")
                put("tipo", "contiene")
                put("pregunta", "Fruto comestible de un árbol que tiene una piel roja o verde y una pulpa jugosa.")
                put("respuesta", "Manzana")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "B")
                put("tipo", "empieza")
                put("pregunta", "Animal que vive en el mar y tiene un caparazón duro, como las tortugas o cangrejos.")
                put("respuesta", "Bivalvo")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "C")
                put("tipo", "empieza")
                put("pregunta", "Fruto comestible, de forma redonda y de color rojo o amarillo, que crece en un árbol.")
                put("respuesta", "Cereza")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "D")
                put("tipo", "empieza")
                put("pregunta", "Animal mamífero que vive en el mar, conocido por su inteligencia.")
                put("respuesta", "Delfín")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "E")
                put("tipo", "empieza")
                put("pregunta", "Planta de hojas verdes y comestibles, muy usada en ensaladas.")
                put("respuesta", "Escarola")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "F")
                put("tipo", "empieza")
                put("pregunta", "Instrumento musical que se toca soplando a través de un tubo largo.")
                put("respuesta", "Flauta")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "G")
                put("tipo", "empieza")
                put("pregunta", "Animal de granja que tiene lana, como el carnero o la oveja.")
                put("respuesta", "Ganso")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "H")
                put("tipo", "empieza")
                put("pregunta", "Fruto pequeño, redondo y comestible que se da en la planta de la vid.")
                put("respuesta", "Higo")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "I")
                put("tipo", "empieza")
                put("pregunta", "Sustancia dulce y viscosa que producen las abejas a partir del néctar de las flores.")
                put("respuesta", "Ídem")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "J")
                put("tipo", "empieza")
                put("pregunta", "Fruto de un árbol que es redondo y tiene una piel de color amarillo-verde.")
                put("respuesta", "Jabón")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "K")
                put("tipo", "empieza")
                put("pregunta", "Objeto de uso cotidiano que se utiliza para cortar el cabello.")
                put("respuesta", "Kératinización")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "L")
                put("tipo", "empieza")
                put("pregunta", "Animal conocido por su agilidad, que se caracteriza por saltar con facilidad.")
                put("respuesta", "Lince")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "M")
                put("tipo", "empieza")
                put("pregunta", "Árbol frutal que da frutos llamados peras.")
                put("respuesta", "Manzano")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "N")
                put("tipo", "empieza")
                put("pregunta", "Fruto de un árbol de color rojo o verde y sabor dulce.")
                put("respuesta", "Naranja")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "O")
                put("tipo", "empieza")
                put("pregunta", "Fruto comestible que se da en un árbol y tiene un sabor ácido.")
                put("respuesta", "Oliva")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "P")
                put("tipo", "empieza")
                put("pregunta", "Fruto de un árbol que se consume generalmente de color anaranjado y suave.")
                put("respuesta", "Pera")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "Q")
                put("tipo", "empieza")
                put("pregunta", "Fruto pequeño, generalmente de color rojo o negro, que se encuentra en racimos.")
                put("respuesta", "Querubín")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "R")
                put("tipo", "empieza")
                put("pregunta", "Fruto que tiene una forma redonda y de color rojo, muy dulce.")
                put("respuesta", "Rosa")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "S")
                put("tipo", "empieza")
                put("pregunta", "Planta de hojas largas y estrechas que se utiliza en la cocina.")
                put("respuesta", "Salmón")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "T")
                put("tipo", "empieza")
                put("pregunta", "Animal de granja que es domesticado y generalmente se encuentra en las granjas.")
                put("respuesta", "Toro")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "U")
                put("tipo", "empieza")
                put("pregunta", "Fruto que crece en racimos y se consume principalmente para hacer jugo.")
                put("respuesta", "Uva")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "V")
                put("tipo", "empieza")
                put("pregunta", "Planta que produce frutos rojos que se usan en mermeladas.")
                put("respuesta", "Violeta")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "W")
                put("tipo", "empieza")
                put("pregunta", "Fruto tropical que tiene una piel de color marrón y su carne es de color amarillo.")
                put("respuesta", "Wampee")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "X")
                put("tipo", "empieza")
                put("pregunta", "Fruto que se encuentra en las zonas tropicales de América y que se utiliza principalmente para hacer jugos.")
                put("respuesta", "Xoconostle")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "Y")
                put("tipo", "empieza")
                put("pregunta", "Fruto comestible que se obtiene de un árbol tropical.")
                put("respuesta", "Yuca")
                put("nivel", "Fácil")
            },
            ContentValues().apply {
                put("letra", "Z")
                put("tipo", "empieza")
                put("pregunta", "Fruto de color rojo que se consume generalmente en ensaladas.")
                put("respuesta", "Zanahoria")
                put("nivel", "Fácil")
            }
        )
        fun preguntaExiste(letra: String, tipo: String, nivel: String, db: SQLiteDatabase): Boolean {
            val query = "SELECT * FROM Preguntas WHERE letra = ? AND tipo = ? AND nivel = ?"
            val cursor = db.rawQuery(query, arrayOf(letra, tipo, nivel))
            val existe = cursor.count > 0
            cursor.close()
            return existe
        }
        preguntasFacil.forEach { pregunta ->
            // Verificar si la pregunta ya existe
            if (!preguntaExiste(pregunta.getAsString("letra"), pregunta.getAsString("tipo"), pregunta.getAsString("nivel"), db!!)) {
                db.insert("Preguntas", null, pregunta)
            }
        }
        //nivel medio
        val preguntasMedio = listOf(
            ContentValues().apply {
                put("letra", "A")
                put("tipo", "empieza")
                put("pregunta", "Objeto utilizado para iluminar espacios cerrados que se enciende con electricidad.")
                put("respuesta", "Amperio")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "B")
                put("tipo", "empieza")
                put("pregunta", "Cuerpo celeste que gira alrededor del Sol y tiene una órbita elíptica.")
                put("respuesta", "Bola de nieve")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "C")
                put("tipo", "empieza")
                put("pregunta", "Contenedor o recipiente utilizado para guardar cosas.")
                put("respuesta", "Caja")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "D")
                put("tipo", "empieza")
                put("pregunta", "Elemento químico que se encuentra en la tabla periódica, con el símbolo 'D'.")
                put("respuesta", "Deuterio")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "E")
                put("tipo", "empieza")
                put("pregunta", "Sustancia que se encuentra en la atmósfera y da color al cielo.")
                put("respuesta", "Escarlata")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "F")
                put("tipo", "empieza")
                put("pregunta", "Famoso escritor de novelas que creó una historia en la que aparece un monstruo creado por un científico.")
                put("respuesta", "Frankenstein")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "G")
                put("tipo", "empieza")
                put("pregunta", "Fruta que tiene una cáscara gruesa y se puede usar para hacer jugo.")
                put("respuesta", "Granada")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "H")
                put("tipo", "empieza")
                put("pregunta", "Animal que vive en el agua, con cuerpo largo y delgado.")
                put("respuesta", "Hércules")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "I")
                put("tipo", "empieza")
                put("pregunta", "Materia que compone los cuerpos celestes.")
                put("respuesta", "Íon")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "J")
                put("tipo", "empieza")
                put("pregunta", "Elemento usado para hacer joyas y adornos valiosos.")
                put("respuesta", "Joya")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "K")
                put("tipo", "empieza")
                put("pregunta", "Instrumento musical de percusión que se toca golpeando con baquetas.")
                put("respuesta", "Klong")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "L")
                put("tipo", "empieza")
                put("pregunta", "Nombre de un mes del año.")
                put("respuesta", "Luna")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "M")
                put("tipo", "empieza")
                put("pregunta", "Planta que da flores de muchos colores y que tiene una fragancia fuerte.")
                put("respuesta", "Magnolia")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "N")
                put("tipo", "empieza")
                put("pregunta", "Elemento que forma parte del aire.")
                put("respuesta", "Nitrógeno")
                put("nivel", "Medio")
            },

            ContentValues().apply {
                put("letra", "O")
                put("tipo", "empieza")
                put("pregunta", "Planeta más cercano al Sol.")
                put("respuesta", "Mercurio")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "P")
                put("tipo", "empieza")
                put("pregunta", "País ubicado en América del Sur, famoso por su tango.")
                put("respuesta", "Argentina")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "Q")
                put("tipo", "empieza")
                put("pregunta", "Metal precioso que se utiliza para hacer joyas.")
                put("respuesta", "Oro")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "R")
                put("tipo", "empieza")
                put("pregunta", "Animal que se encuentra en el Polo Norte, conocido por su color blanco.")
                put("respuesta", "Reno")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "S")
                put("tipo", "empieza")
                put("pregunta", "Elemento químico con símbolo 'S'.")
                put("respuesta", "Azufre")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "T")
                put("tipo", "empieza")
                put("pregunta", "Alimento originario de México que se hace con maíz y se sirve con diversos ingredientes.")
                put("respuesta", "Taco")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "U")
                put("tipo", "empieza")
                put("pregunta", "Continente que está al sur de América.")
                put("respuesta", "Antártida")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "V")
                put("tipo", "empieza")
                put("pregunta", "Ciudad que es la capital de Italia.")
                put("respuesta", "Roma")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "W")
                put("tipo", "empieza")
                put("pregunta", "País europeo conocido por su chocolate y relojes.")
                put("respuesta", "Suiza")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "X")
                put("tipo", "empieza")
                put("pregunta", "Elemento químico con símbolo 'X'.")
                put("respuesta", "Xenón")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "Y")
                put("tipo", "empieza")
                put("pregunta", "Ciudad estadounidense famosa por su Estatua de la Libertad.")
                put("respuesta", "Nueva York")
                put("nivel", "Medio")
            },
            ContentValues().apply {
                put("letra", "Z")
                put("tipo", "empieza")
                put("pregunta", "Elemento metálico con el símbolo 'Z'.")
                put("respuesta", "Zinc")
                put("nivel", "Medio")
            }
            )
        preguntasMedio.forEach { pregunta ->
            if (!preguntaExiste(pregunta.getAsString("letra"), pregunta.getAsString("tipo"), pregunta.getAsString("nivel"), db!!)) {
                db.insert("Preguntas", null, pregunta)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // En caso de que se necesiten actualizaciones futuras
        db?.execSQL("DROP TABLE IF EXISTS Ranking")
        db?.execSQL("DROP TABLE IF EXISTS Preguntas")
        db?.execSQL("DROP TABLE IF EXISTS Usuarios")
        onCreate(db)
    }


}