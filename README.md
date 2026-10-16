# 📊 Editor de Grafos - Estructura de Datos y sus Aplicaciones

Aplicación de escritorio desarrollada en **Java (Swing)** para crear, editar, visualizar y analizar **grafos no dirigidos y ponderados**, y ejecutar los algoritmos clásicos de teoría de grafos: Kruskal, Prim, Dijkstra y Floyd-Warshall.

## 🚀 Características Principales

- **Creación gráfica interactiva:** agregar, mover (arrastrando con el ratón) y eliminar nodos y aristas. Cada nodo se identifica con un código único y editable (A, B, C...). Límite de **20 vértices**.
- **Grafos ponderados:** cada arista tiene un **peso entero ≥ 0** que representa distancias, tiempos o costos.
- **Tipografía resaltada en el panel:** el código del nodo se dibuja en **negrita** dentro del círculo, con contraste automático (texto claro u oscuro según el color del nodo). El nombre completo del vértice aparece debajo del nodo y el peso de cada arista se muestra sobre una etiqueta legible.
- **Colores configurables:** desde el **menú contextual** (clic derecho) sobre un nodo o arista se elige el color con (`JColorChooser`); el botón **"Restablecer colores"** devuelve todos los elementos a los colores predeterminados.
- **Menú contextual (clic derecho):** sobre un **nodo** → *Cambiar color*, *Renombrar*, *Eliminar*; sobre una **arista** → *Cambiar color*, *Cambiar peso*, *Eliminar*. Acciones rápidas sin usar los botones de la barra.
- **Resaltado al pasar el cursor:** el nodo bajo el puntero se resalta con un anillo y el cursor cambia a una mano.
- **Guardado y apertura de grafos:** el botón **"Guardar grafo"** guarda el grafo actual en la carpeta `grafos/` (formato de texto UTF-8). El botón **"Abrir grafo"** muestra una lista para **visualizar** los grafos guardados, marcando el último usado e incluyendo siempre el **grafo predeterminado**. Al iniciar, la aplicación carga automáticamente el último grafo guardado o, si no hay, el predeterminado.
- **Eliminar grafo actual:** el botón **"Eliminar grafo actual"** borra todos los nodos y aristas (con confirmación previa) para empezar un grafo desde cero.
- **Matriz de adyacencia editable:** se abre en un diálogo y permite editar hasta 20×20 celdas. Valida que la matriz sea **simétrica** y que los valores sean enteros no negativos o `∞` (sin conexión). Al guardar, reconstruye las aristas del grafo.
- **Algoritmos implementados:**
  - **Kruskal:** árbol de expansión mínima seleccionando las aristas de menor peso sin formar ciclos.
  - **Prim:** árbol de expansión mínima a partir de un vértice inicial.
  - **Dijkstra:** caminos más cortos desde un vértice origen, con distancias y ruta acumulada.
  - **Floyd-Warshall:** caminos mínimos entre todos los pares de vértices.
- Los resultados de cada algoritmo se muestran en una **ventana de texto**.

## 🏗️ Estructura del proyecto

El código está dividido en dos clases (ambas en el paquete por defecto):

1. **`Principal.java`** — interfaz gráfica: ventana principal, panel de dibujo (`PanelGrafo`), menú contextual, botones, diálogos de entrada y presentación de resultados.
2. **`RedComunicacion.java`** — modelo del grafo (vértices, nombres y aristas), lógica de los algoritmos (Kruskal, Prim, `sumaPesos`) y persistencia de grafos (guardar/cargar/listar).

> `Principal` usa `RedComunicacion` como modelo de datos; **ambas clases deben estar presentes** para compilar.

## 🛠️ Requisitos y Ejecución

- **Lenguaje:** Java (JDK 17 o superior; probado con JDK 21).
- **Interfaz gráfica:** Swing (incluido en el JDK; no requiere JavaFX ni dependencias externas).
- **Compilación y ejecución** desde la carpeta del proyecto:

  ```bash
  javac Principal.java RedComunicacion.java
  java Principal
  ```

  También se puede abrir el proyecto en un IDE (IntelliJ IDEA, Eclipse o NetBeans) y ejecutar la clase principal **`Principal`**.

## ⚠️ Notas y limitaciones

- Solo se admiten **grafos no dirigidos**: la matriz de adyacencia debe ser simétrica y (A,B) equivale a (B,A).
- Los **colores** de nodos y aristas se guardan únicamente en memoria durante la sesión (no se persisten en el archivo).
- Los **grafos guardados** se almacenan como archivos de texto (UTF-8) en la carpeta `grafos/`, creada junto al programa; `.ultimo` recuerda el nombre del último grafo usado.
- No existe ejecución **paso a paso ni animación**: los algoritmos se ejecutan de una vez y muestran su resultado final en un diálogo.
- Al iniciar se carga el **último grafo** guardado/abierto o, si no existe, un **grafo por defecto** de 8 vértices (sucursales A–H).
