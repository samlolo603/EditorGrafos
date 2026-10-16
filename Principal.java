/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dayan
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
public class Principal extends JFrame{
    
    private PanelGrafo areaTrabajo;

    // Nombre del grafo actual (para el guardado rápido).
    private String nombreGrafoActual = "grafo";

    // Colores configurables por el usuario (estado de la vista).
    private final Map<String, Color> coloresNodos =
        new LinkedHashMap<>();
    private final Map<String, Color> coloresAristas =
        new LinkedHashMap<>();

    private static final Color COLOR_NODO_DEFECTO =
        new Color(190, 220, 255);
    private static final Color COLOR_ARISTA_DEFECTO =
        new Color(120, 120, 120);

    private static final Font FUENTE_NODO =
        new Font("SansSerif", Font.BOLD, 14);
    private static final Font FUENTE_NOMBRE =
        new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FUENTE_PESO =
        new Font("SansSerif", Font.BOLD, 12);

    private static final String GRAFO_PREDETERMINADO =
        "(Grafo predeterminado)";

    private static class EstadoDistancia
            implements Comparable<EstadoDistancia> {

        String vertice;
        long distancia;

        EstadoDistancia(String vertice, long distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(EstadoDistancia otro) {
            return Long.compare(distancia, otro.distancia);
        }
    }

    public Principal() {
        setTitle("Editor de Grafos - Estructuras de Datos");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cargarGrafoInicial();

        JPanel filaEdicion =
            new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JPanel filaArchivo =
            new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JButton agregarNodo = new JButton("Agregar nodo");
        JButton agregarArista = new JButton("Agregar arista");
        JButton matriz = new JButton("Matriz de adyacencia");
        JButton resetColores = new JButton("Restablecer colores");
        JButton guardar = new JButton("Guardar grafo");
        JButton abrir = new JButton("Abrir grafo");
        JButton eliminarGrafo = new JButton("Eliminar grafo actual");

        filaEdicion.add(agregarNodo);
        filaEdicion.add(agregarArista);
        filaEdicion.add(matriz);
        filaEdicion.add(resetColores);

        filaArchivo.add(guardar);
        filaArchivo.add(abrir);
        filaArchivo.add(eliminarGrafo);

        // Dos filas explícitas: evita que FlowLayout recorte los botones
        // al pasar a una segunda línea dentro de BorderLayout.NORTH.
        JPanel herramientas = new JPanel(new GridLayout(2, 1, 0, 0));
        herramientas.add(filaEdicion);
        herramientas.add(filaArchivo);

        JPanel algoritmos = new JPanel(new FlowLayout());

        JButton kruskal = new JButton("Kruskal");
        JButton prim = new JButton("Prim");
        JButton dijkstra = new JButton("Dijkstra");
        JButton floyd = new JButton("Floyd-Warshall");

        algoritmos.add(kruskal);
        algoritmos.add(prim);
        algoritmos.add(dijkstra);
        algoritmos.add(floyd);

        areaTrabajo = new PanelGrafo();

        agregarNodo.addActionListener(e -> agregarNodo());
        agregarArista.addActionListener(e -> agregarArista());
        matriz.addActionListener(e -> mostrarMatriz());
        resetColores.addActionListener(e -> restablecerColores());
        guardar.addActionListener(e -> guardarGrafoDialogo());
        abrir.addActionListener(e -> abrirGrafoDialogo());
        eliminarGrafo.addActionListener(e -> eliminarGrafoActual());

        kruskal.addActionListener(e -> ejecutarKruskal());
        prim.addActionListener(e -> ejecutarPrim());
        dijkstra.addActionListener(e -> ejecutarDijkstra());
        floyd.addActionListener(e -> ejecutarFloydWarshall());

        add(herramientas, BorderLayout.NORTH);
        add(areaTrabajo, BorderLayout.CENTER);
        add(algoritmos, BorderLayout.SOUTH);
    }

    private void actualizarGrafo() {
        areaTrabajo.repaint();
    }

    private void agregarNodo() {
        if (RedComunicacion.vertices.size() >= 20) {
            JOptionPane.showMessageDialog(this,
                "El grafo no puede tener más de 20 vértices.");
            return;
        }

        String codigo = JOptionPane.showInputDialog(this,
            "Código del nuevo vértice:");

        if (codigo == null || codigo.trim().isEmpty()) {
            return;
        }

        codigo = codigo.trim().toUpperCase();

        if (RedComunicacion.vertices.contains(codigo)) {
            JOptionPane.showMessageDialog(this,
                "Ese vértice ya existe.");
            return;
        }

        String nombre = JOptionPane.showInputDialog(this,
            "Nombre del vértice:");

        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = codigo;
        }

        RedComunicacion.agregarVertice(codigo, nombre.trim());
        actualizarGrafo();
    }

    private void agregarArista() {
        String origen = JOptionPane.showInputDialog(this,
            "Código del vértice de origen:");

        if (origen == null) {
            return;
        }

        origen = origen.trim().toUpperCase();

        String destino = JOptionPane.showInputDialog(this,
            "Código del vértice de destino:");

        if (destino == null) {
            return;
        }

        destino = destino.trim().toUpperCase();

        if (!RedComunicacion.vertices.contains(origen)
                || !RedComunicacion.vertices.contains(destino)) {
            JOptionPane.showMessageDialog(this,
                "Uno o ambos vértices no existen.");
            return;
        }

        if (origen.equals(destino)) {
            JOptionPane.showMessageDialog(this,
                "No puedes conectar un vértice consigo mismo.");
            return;
        }

        String textoPeso = JOptionPane.showInputDialog(this,
            "Peso de la arista:");

        if (textoPeso == null) {
            return;
        }

        try {
            int peso = Integer.parseInt(textoPeso.trim());

            if (peso < 0) {
                JOptionPane.showMessageDialog(this,
                    "El peso no puede ser negativo.");
                return;
            }

            RedComunicacion.agregarArista(origen, destino, peso);
            actualizarGrafo();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El peso debe ser un número entero.");
        }
    }

    private void mostrarMatriz() {
        List<String> vertices =
            new ArrayList<>(RedComunicacion.vertices);

        int n = vertices.size();

        if (n == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay vértices en el grafo.");
            return;
        }

        String[] columnas = new String[n + 1];
        Object[][] datos = new Object[n][n + 1];

        columnas[0] = "Vértice";

        for (int i = 0; i < n; i++) {
            columnas[i + 1] = vertices.get(i);
            datos[i][0] = vertices.get(i);

            for (int j = 0; j < n; j++) {
                datos[i][j + 1] = (i == j) ? 0 : "∞";
            }
        }

        for (RedComunicacion.Arista a :
                RedComunicacion.aristas) {

            int i = vertices.indexOf(a.origen);
            int j = vertices.indexOf(a.destino);

            if (i >= 0 && j >= 0) {
                Object actual = datos[i][j + 1];

                if ("∞".equals(actual)
                        || a.peso < (Integer) actual) {
                    datos[i][j + 1] = a.peso;
                    datos[j][i + 1] = a.peso;
                }
            }
        }

        javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(datos, columnas) {

                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return columna > 0 && columna - 1 != fila;
                }
            };

        JTable tabla = new JTable(modelo);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(700, 350));

        int opcion = JOptionPane.showConfirmDialog(
            this,
            scroll,
            "Matriz de adyacencia editable",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        if (tabla.isEditing()
                && !tabla.getCellEditor().stopCellEditing()) {
            JOptionPane.showMessageDialog(this,
                "Termina de editar la celda antes de guardar.");
            return;
        }

        int[][] pesos = new int[n][n];
        final int SIN_CONEXION = -1;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (i == j) {
                    pesos[i][j] = 0;
                    continue;
                }

                Object valor = modelo.getValueAt(i, j + 1);
                String texto = String.valueOf(valor).trim();

                if (texto.equals("∞")
                        || texto.equalsIgnoreCase("inf")
                        || texto.equalsIgnoreCase("infinito")
                        || texto.equals("-")) {

                    pesos[i][j] = SIN_CONEXION;

                } else {
                    try {
                        int peso = Integer.parseInt(texto);

                        if (peso < 0) {
                            throw new NumberFormatException();
                        }

                        pesos[i][j] = peso;

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                            "Todos los pesos deben ser enteros "
                            + "no negativos o ∞ para indicar "
                            + "que no existe conexión.");
                        return;
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                if (pesos[i][j] != pesos[j][i]) {
                    JOptionPane.showMessageDialog(this,
                        "La matriz debe ser simétrica. "
                        + "Los valores de A-B y B-A deben coincidir.");
                    return;
                }
            }
        }

        RedComunicacion.aristas.clear();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                if (pesos[i][j] != SIN_CONEXION) {
                    RedComunicacion.aristas.add(
                        new RedComunicacion.Arista(
                            vertices.get(i),
                            vertices.get(j),
                            pesos[i][j]
                        )
                    );
                }
            }
        }

        RedComunicacion.hayCambiosSinGuardar = true;

        depurarColores();
        actualizarGrafo();

        JOptionPane.showMessageDialog(this,
            "Matriz guardada correctamente.\n"
            + "El grafo y sus aristas han sido actualizados.");
    }

    private void ejecutarKruskal() {
        List<RedComunicacion.Arista> resultado =
            RedComunicacion.kruskal();

        if (resultado == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo obtener el árbol de expansión mínima.");
            return;
        }

        StringBuilder texto =
            new StringBuilder("Árbol mínimo con Kruskal:\n\n");

        for (RedComunicacion.Arista a : resultado) {
            texto.append(a).append("\n");
        }

        texto.append("\nPeso total: ")
            .append(RedComunicacion.sumaPesos(resultado));

        mostrarResultado("Kruskal", texto.toString());
    }

    private void ejecutarPrim() {
        String inicio = JOptionPane.showInputDialog(this,
            "Código del vértice inicial para Prim:");

        if (inicio == null || inicio.trim().isEmpty()) {
            return;
        }

        inicio = inicio.trim().toUpperCase();

        if (!RedComunicacion.vertices.contains(inicio)) {
            JOptionPane.showMessageDialog(this,
                "El vértice inicial no existe.");
            return;
        }

        List<RedComunicacion.Arista> resultado =
            RedComunicacion.prim(inicio);

        if (resultado == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo obtener el árbol de expansión mínima.");
            return;
        }

        StringBuilder texto =
            new StringBuilder("Árbol mínimo con Prim:\n\n");

        for (RedComunicacion.Arista a : resultado) {
            texto.append(a).append("\n");
        }

        texto.append("\nPeso total: ")
            .append(RedComunicacion.sumaPesos(resultado));

        mostrarResultado("Prim", texto.toString());
    }

    private void ejecutarDijkstra() {
        if (RedComunicacion.vertices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Primero agrega al menos un vértice.");
            return;
        }

        for (RedComunicacion.Arista a :
                RedComunicacion.aristas) {
            if (a.peso < 0) {
                JOptionPane.showMessageDialog(this,
                    "Dijkstra no puede trabajar con pesos negativos.");
                return;
            }
        }

        String inicio = JOptionPane.showInputDialog(this,
            "Código del vértice inicial para Dijkstra:");

        if (inicio == null || inicio.trim().isEmpty()) {
            return;
        }

        inicio = inicio.trim().toUpperCase();

        if (!RedComunicacion.vertices.contains(inicio)) {
            JOptionPane.showMessageDialog(this,
                "El vértice inicial no existe.");
            return;
        }

        Map<String, Long> distancias = new LinkedHashMap<>();
        Map<String, String> anteriores = new LinkedHashMap<>();

        for (String vertice : RedComunicacion.vertices) {
            distancias.put(vertice, Long.MAX_VALUE);
            anteriores.put(vertice, null);
        }

        distancias.put(inicio, 0L);

        PriorityQueue<EstadoDistancia> cola =
            new PriorityQueue<>();

        cola.add(new EstadoDistancia(inicio, 0));

        while (!cola.isEmpty()) {
            EstadoDistancia estado = cola.poll();
            String actual = estado.vertice;

            if (estado.distancia != distancias.get(actual)) {
                continue;
            }

            for (RedComunicacion.Arista a :
                    RedComunicacion.aristas) {

                String vecino = null;

                if (a.origen.equals(actual)) {
                    vecino = a.destino;
                } else if (a.destino.equals(actual)) {
                    vecino = a.origen;
                }

                if (vecino == null
                        || !distancias.containsKey(vecino)) {
                    continue;
                }

                long nuevaDistancia =
                    estado.distancia + a.peso;

                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    anteriores.put(vecino, actual);

                    cola.add(new EstadoDistancia(
                        vecino, nuevaDistancia
                    ));
                }
            }
        }

        StringBuilder texto = new StringBuilder();

        texto.append("RESULTADOS DE DIJKSTRA\n");
        texto.append("Vértice inicial: ").append(inicio);
        texto.append("\n\n");

        texto.append(String.format(
            "%-12s %-15s %s%n",
            "Vértice", "Distancia", "Ruta más corta"
        ));

        texto.append(
            "--------------------------------------------------\n"
        );

        for (String vertice : RedComunicacion.vertices) {
            long distancia = distancias.get(vertice);

            texto.append(String.format("%-12s ", vertice));

            if (distancia == Long.MAX_VALUE) {
                texto.append(String.format(
                    "%-15s %s%n",
                    "Inalcanzable",
                    "No existe ruta"
                ));
            } else {
                String ruta = construirRuta(
                    inicio, vertice, anteriores
                );

                texto.append(String.format(
                    "%-15d %s%n",
                    distancia,
                    ruta
                ));
            }
        }

        mostrarResultado("Resultados de Dijkstra", texto.toString());
    }

    private void ejecutarFloydWarshall() {
        List<String> vertices =
            new ArrayList<>(RedComunicacion.vertices);

        int n = vertices.size();

        if (n == 0) {
            JOptionPane.showMessageDialog(this,
                "Primero agrega al menos un vértice.");
            return;
        }

        final long INF = Long.MAX_VALUE / 4;

        long[][] distancias = new long[n][n];
        int[][] siguiente = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distancias[i][j] = (i == j) ? 0 : INF;
                siguiente[i][j] = (i == j) ? i : -1;
            }
        }

        for (RedComunicacion.Arista a :
                RedComunicacion.aristas) {

            int i = vertices.indexOf(a.origen);
            int j = vertices.indexOf(a.destino);

            if (i >= 0 && j >= 0
                    && a.peso < distancias[i][j]) {
                distancias[i][j] = a.peso;
                distancias[j][i] = a.peso;
                siguiente[i][j] = j;
                siguiente[j][i] = i;
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (distancias[i][k] != INF
                            && distancias[k][j] != INF) {

                        long nuevaDistancia =
                            distancias[i][k] + distancias[k][j];

                        if (nuevaDistancia < distancias[i][j]) {
                            distancias[i][j] = nuevaDistancia;
                            siguiente[i][j] = siguiente[i][k];
                        }
                    }
                }
            }
        }

        StringBuilder texto = new StringBuilder();

        texto.append("RESULTADOS DE FLOYD-WARSHALL\n\n");
        texto.append(
            "Distancias y rutas más cortas entre ciudades\n\n"
        );

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (i == j) {
                    continue;
                }

                texto.append(vertices.get(i))
                    .append(" -> ")
                    .append(vertices.get(j))
                    .append("\nDistancia: ");

                if (distancias[i][j] == INF) {
                    texto.append("Inalcanzable\n");
                    texto.append("Ruta: No existe ruta\n\n");
                    continue;
                }

                texto.append(distancias[i][j])
                    .append("\nRuta: ");

                List<String> ruta = new ArrayList<>();
                int actual = i;
                ruta.add(vertices.get(actual));

                int pasos = 0;

                while (actual != j
                        && siguiente[actual][j] != -1
                        && pasos < n) {

                    actual = siguiente[actual][j];
                    ruta.add(vertices.get(actual));
                    pasos++;
                }

                texto.append(String.join(" -> ", ruta));
                texto.append("\n\n");
            }
        }

        mostrarResultado(
            "Resultados de Floyd-Warshall",
            texto.toString()
        );
    }

    private String construirRuta(
            String inicio,
            String destino,
            Map<String, String> anteriores) {

        List<String> ruta = new ArrayList<>();
        String actual = destino;

        while (actual != null) {
            ruta.add(actual);

            if (actual.equals(inicio)) {
                break;
            }

            actual = anteriores.get(actual);
        }

        if (ruta.isEmpty()
                || !ruta.get(ruta.size() - 1).equals(inicio)) {
            return "No existe ruta";
        }

        Collections.reverse(ruta);
        return String.join(" -> ", ruta);
    }

    private void mostrarResultado(
            String titulo, String contenido) {

        JTextArea area = new JTextArea(contenido, 20, 65);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(750, 400));

        JOptionPane.showMessageDialog(
            this,
            scroll,
            titulo,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    //  GUARDADO Y APERTURA DE GRAFOS 

    // Al iniciar: carga el último grafo guardado o, si no hay, el predeterminado.
    private void cargarGrafoInicial() {
        String ultimo = RedComunicacion.leerUltimo();

        if (ultimo != null) {
            try {
                RedComunicacion.cargarGrafo(ultimo);
                nombreGrafoActual = ultimo;
                return;
            } catch (IOException ex) {
                // El archivo del último grafo ya no existe: se usa el predeterminado.
            }
        }

        RedComunicacion.cargarGrafoPorDefecto();
        nombreGrafoActual = "grafo";
    }

    private void guardarGrafoDialogo() {
        if (RedComunicacion.vertices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay ningún grafo que guardar.");
            return;
        }

        String nombre = JOptionPane.showInputDialog(this,
            "Nombre del grafo a guardar:", nombreGrafoActual);

        if (nombre == null) {
            return;
        }

        nombre = sanitizarNombre(nombre);

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre no es válido.");
            return;
        }

        try {
            RedComunicacion.guardarGrafo(nombre);
            RedComunicacion.escribirUltimo(nombre);
            nombreGrafoActual = nombre;

            JOptionPane.showMessageDialog(this,
                "Grafo guardado como \"" + nombre + "\".\n"
                + "(Carpeta \"grafos\")");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo guardar el grafo:\n" + ex.getMessage());
        }
    }

    private void abrirGrafoDialogo() {
        List<String> guardados = RedComunicacion.listarGrafos();
        String ultimo = RedComunicacion.leerUltimo();

        final javax.swing.DefaultListModel<String> modelo =
            new javax.swing.DefaultListModel<>();

        final List<String> nombres = new ArrayList<>();

        modelo.addElement(GRAFO_PREDETERMINADO);
        nombres.add(null);

        for (String g : guardados) {
            modelo.addElement(g.equals(ultimo) ? g + "  (último)" : g);
            nombres.add(g);
        }

        JList<String> lista = new JList<>(modelo);
        lista.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lista.setVisibleRowCount(10);
        lista.setSelectedIndex(0);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(320, 220));

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.add(new JLabel("Selecciona el grafo a visualizar:"),
            BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        int opcion = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Abrir grafo guardado",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        int indice = lista.getSelectedIndex();

        if (indice < 0 || indice >= nombres.size()) {
            return;
        }

        String elegido = nombres.get(indice);

        try {
            if (elegido == null) {
                RedComunicacion.cargarGrafoPorDefecto();
                RedComunicacion.escribirUltimo(null);
                nombreGrafoActual = "grafo";
            } else {
                RedComunicacion.cargarGrafo(elegido);
                RedComunicacion.escribirUltimo(elegido);
                nombreGrafoActual = elegido;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir el grafo:\n" + ex.getMessage());
            return;
        }

        depurarColores();
        areaTrabajo.reiniciarPosiciones();
        actualizarGrafo();
    }

    // Quita caracteres no válidos para nombres de archivo.
    private static String sanitizarNombre(String nombre) {
        return nombre.trim()
            .replaceAll("[\\\\/:*?\"<>|]", "_")
            .replaceAll("\\s+", " ");
    }

    // Vacía el grafo actual (todos los nodos y aristas).
    private void eliminarGrafoActual() {
        if (RedComunicacion.vertices.isEmpty()
                && RedComunicacion.aristas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay ningún grafo que eliminar.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el grafo actual? Se borrarán todos los "
            + "nodos y aristas.",
            "Eliminar grafo actual",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        RedComunicacion.vertices.clear();
        RedComunicacion.nombres.clear();
        RedComunicacion.aristas.clear();
        RedComunicacion.hayCambiosSinGuardar = true;

        coloresNodos.clear();
        coloresAristas.clear();

        nombreGrafoActual = "grafo";

        areaTrabajo.reiniciarPosiciones();
        actualizarGrafo();
    }

    //  ACCIONES CONTEXTUALES (menú de clic derecho) 

    private void renombrarVertice(String codigo) {
        String actual = RedComunicacion.nombres.getOrDefault(codigo, codigo);

        String nuevo = JOptionPane.showInputDialog(this,
            "Nuevo nombre para el vértice " + codigo + ":", actual);

        if (nuevo == null) {
            return;
        }

        nuevo = nuevo.trim();

        if (nuevo.isEmpty()) {
            nuevo = codigo;
        }

        RedComunicacion.nombres.put(codigo, nuevo);
        RedComunicacion.hayCambiosSinGuardar = true;
        actualizarGrafo();
    }

    private void eliminarVerticeDirecto(String codigo) {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el vértice " + codigo + " y todas sus aristas?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        RedComunicacion.eliminarVertice(codigo);
        depurarColores();
        actualizarGrafo();
    }

    private void cambiarPesoArista(RedComunicacion.Arista arista) {
        String texto = JOptionPane.showInputDialog(this,
            "Nuevo peso para " + arista.origen + " - " + arista.destino + ":",
            arista.peso);

        if (texto == null) {
            return;
        }

        try {
            int peso = Integer.parseInt(texto.trim());

            if (peso < 0) {
                JOptionPane.showMessageDialog(this,
                    "El peso no puede ser negativo.");
                return;
            }

            arista.peso = peso;
            RedComunicacion.hayCambiosSinGuardar = true;
            actualizarGrafo();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El peso debe ser un número entero.");
        }
    }

    private void eliminarAristaDirecto(RedComunicacion.Arista arista) {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la arista " + arista.origen + " - "
            + arista.destino + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        RedComunicacion.eliminarArista(arista.origen, arista.destino);
        depurarColores();
        actualizarGrafo();
    }

    //  COLORES DE NODOS Y ARISTAS 

    private static String claveArista(String a, String b) {
        return a.compareTo(b) <= 0 ? a + "-" + b : b + "-" + a;
    }

    private void configurarColorNodo(String codigo) {
        Color actual = coloresNodos.getOrDefault(
            codigo, COLOR_NODO_DEFECTO);

        Color elegido = JColorChooser.showDialog(
            this,
            "Color del vértice " + codigo,
            actual
        );

        if (elegido != null) {
            coloresNodos.put(codigo, elegido);
            actualizarGrafo();
        }
    }

    private void configurarColorArista(RedComunicacion.Arista arista) {
        String clave = claveArista(arista.origen, arista.destino);

        Color actual = coloresAristas.getOrDefault(
            clave, COLOR_ARISTA_DEFECTO);

        Color elegido = JColorChooser.showDialog(
            this,
            "Color de la arista " + arista.origen + " - " + arista.destino,
            actual
        );

        if (elegido != null) {
            coloresAristas.put(clave, elegido);
            actualizarGrafo();
        }
    }

    private void restablecerColores() {
        coloresNodos.clear();
        coloresAristas.clear();
        actualizarGrafo();
    }

    // Elimina los colores de elementos que ya no existen en el grafo.
    private void depurarColores() {
        coloresNodos.keySet().retainAll(RedComunicacion.vertices);

        Set<String> claves = new HashSet<>();

        for (RedComunicacion.Arista a : RedComunicacion.aristas) {
            claves.add(claveArista(a.origen, a.destino));
        }

        coloresAristas.keySet().retainAll(claves);
    }

    // Elige texto claro u oscuro según el brillo del fondo.
    private static Color colorTextoContraste(Color fondo) {
        double luminancia = (0.299 * fondo.getRed()
                + 0.587 * fondo.getGreen()
                + 0.114 * fondo.getBlue()) / 255.0;

        return luminancia > 0.6 ? Color.BLACK : Color.WHITE;
    }

    class PanelGrafo extends JPanel {

        private final Map<String, Point> posiciones =
            new LinkedHashMap<>();

        private String verticeSeleccionado;
        private String verticeResaltado;
        private int desplazamientoX;
        private int desplazamientoY;

        public PanelGrafo() {
            setBackground(Color.WHITE);

            MouseAdapter raton = new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        mostrarMenuContextual(e);
                        return;
                    }

                    verticeSeleccionado = null;

                    for (String vertice : RedComunicacion.vertices) {
                        Point p = posiciones.get(vertice);

                        if (p != null
                                && p.distance(e.getPoint()) <= 25) {
                            verticeSeleccionado = vertice;
                            desplazamientoX = e.getX() - p.x;
                            desplazamientoY = e.getY() - p.y;
                            break;
                        }
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (verticeSeleccionado != null) {
                        int x = Math.max(22,
                            Math.min(getWidth() - 22,
                                e.getX() - desplazamientoX));

                        int y = Math.max(22,
                            Math.min(getHeight() - 22,
                                e.getY() - desplazamientoY));

                        posiciones.put(
                            verticeSeleccionado,
                            new Point(x, y)
                        );

                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        mostrarMenuContextual(e);
                        return;
                    }

                    verticeSeleccionado = null;
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    String anterior = verticeResaltado;
                    verticeResaltado = null;

                    for (String vertice : RedComunicacion.vertices) {
                        Point p = posiciones.get(vertice);

                        if (p != null
                                && p.distance(e.getPoint()) <= 25) {
                            verticeResaltado = vertice;
                            break;
                        }
                    }

                    setCursor(new Cursor(verticeResaltado != null
                            ? Cursor.HAND_CURSOR
                            : Cursor.DEFAULT_CURSOR));

                    if (!java.util.Objects.equals(
                            anterior, verticeResaltado)) {
                        repaint();
                    }
                }
            };

            addMouseListener(raton);
            addMouseMotionListener(raton);
        }

        // Vuelve a calcular la distribución de los nodos (al abrir un grafo).
        void reiniciarPosiciones() {
            posiciones.clear();
            verticeResaltado = null;
            repaint();
        }

        // Clic derecho: menú de configuración del nodo o la arista bajo el cursor.
        private void mostrarMenuContextual(MouseEvent e) {
            for (String vertice : RedComunicacion.vertices) {
                Point p = posiciones.get(vertice);

                if (p != null && p.distance(e.getPoint()) <= 25) {
                    mostrarMenuNodo(vertice, e.getX(), e.getY());
                    return;
                }
            }

            RedComunicacion.Arista arista = aristaEn(e.getPoint());

            if (arista != null) {
                mostrarMenuArista(arista, e.getX(), e.getY());
            }
        }

        private void mostrarMenuNodo(String codigo, int x, int y) {
            JPopupMenu menu = new JPopupMenu();

            JMenuItem color = new JMenuItem("Cambiar color...");
            JMenuItem renombrar = new JMenuItem("Renombrar...");
            JMenuItem eliminar = new JMenuItem("Eliminar");

            color.addActionListener(ev -> configurarColorNodo(codigo));
            renombrar.addActionListener(ev -> renombrarVertice(codigo));
            eliminar.addActionListener(ev -> eliminarVerticeDirecto(codigo));

            menu.add(color);
            menu.add(renombrar);
            menu.addSeparator();
            menu.add(eliminar);

            menu.show(this, x, y);
        }

        private void mostrarMenuArista(
                RedComunicacion.Arista arista, int x, int y) {

            JPopupMenu menu = new JPopupMenu();

            JMenuItem color = new JMenuItem("Cambiar color...");
            JMenuItem peso = new JMenuItem("Cambiar peso...");
            JMenuItem eliminar = new JMenuItem("Eliminar");

            color.addActionListener(ev -> configurarColorArista(arista));
            peso.addActionListener(ev -> cambiarPesoArista(arista));
            eliminar.addActionListener(ev -> eliminarAristaDirecto(arista));

            menu.add(color);
            menu.add(peso);
            menu.addSeparator();
            menu.add(eliminar);

            menu.show(this, x, y);
        }

        // Devuelve la arista más cercana al punto (tolerancia en píxeles).
        private RedComunicacion.Arista aristaEn(Point punto) {
            for (RedComunicacion.Arista a : RedComunicacion.aristas) {
                Point p1 = posiciones.get(a.origen);
                Point p2 = posiciones.get(a.destino);

                if (p1 != null && p2 != null
                        && distanciaSegmento(punto, p1, p2) <= 8) {
                    return a;
                }
            }
            return null;
        }

        private double distanciaSegmento(Point p, Point a, Point b) {
            double dx = b.x - a.x;
            double dy = b.y - a.y;
            double largo2 = dx * dx + dy * dy;

            if (largo2 == 0) {
                return p.distance(a);
            }

            double t = ((p.x - a.x) * dx + (p.y - a.y) * dy) / largo2;
            t = Math.max(0, Math.min(1, t));

            double proyX = a.x + t * dx;
            double proyY = a.y + t * dy;

            return Math.hypot(p.x - proyX, p.y - proyY);
        }

        private void prepararPosiciones() {
            posiciones.keySet().retainAll(
                RedComunicacion.vertices
            );

            int cantidad = RedComunicacion.vertices.size();

            if (cantidad == 0) {
                return;
            }

            int centroX = getWidth() / 2;
            int centroY = getHeight() / 2;
            int radioX = Math.max(40, getWidth() / 2 - 65);
            int radioY = Math.max(40, getHeight() / 2 - 65);

            int i = 0;

            for (String vertice : RedComunicacion.vertices) {
                if (!posiciones.containsKey(vertice)) {
                    double angulo =
                        2 * Math.PI * i / cantidad;

                    int x = centroX
                        + (int) (radioX * Math.cos(angulo));

                    int y = centroY
                        + (int) (radioY * Math.sin(angulo));

                    posiciones.put(vertice, new Point(x, y));
                }

                i++;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            prepararPosiciones();

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            for (RedComunicacion.Arista a :
                    RedComunicacion.aristas) {

                Point origen = posiciones.get(a.origen);
                Point destino = posiciones.get(a.destino);

                if (origen != null && destino != null) {
                    Color colorArista = coloresAristas.getOrDefault(
                        claveArista(a.origen, a.destino),
                        COLOR_ARISTA_DEFECTO);

                    g2.setColor(colorArista);
                    g2.setStroke(new BasicStroke(2.4f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                    g2.drawLine(
                        origen.x, origen.y,
                        destino.x, destino.y
                    );

                    int medioX = (origen.x + destino.x) / 2;
                    int medioY = (origen.y + destino.y) / 2;

                    dibujarEtiquetaPeso(
                        g2,
                        String.valueOf(a.peso),
                        medioX,
                        medioY,
                        colorArista
                    );
                }
            }

            for (String vertice : RedComunicacion.vertices) {
                Point p = posiciones.get(vertice);

                if (p == null) {
                    continue;
                }

                Color relleno = coloresNodos.getOrDefault(
                    vertice, COLOR_NODO_DEFECTO);
                Color borde = relleno.darker();
                boolean resaltado =
                    vertice.equals(verticeResaltado);

                // Sombra suave para dar profundidad.
                g2.setColor(new Color(0, 0, 0, 45));
                g2.fillOval(p.x - 19, p.y - 18, 40, 40);

                g2.setColor(relleno);
                g2.fillOval(p.x - 20, p.y - 20, 40, 40);

                g2.setColor(borde);
                g2.setStroke(new BasicStroke(resaltado ? 3.5f : 2.2f));
                g2.drawOval(p.x - 20, p.y - 20, 40, 40);

                if (resaltado) {
                    g2.setColor(new Color(255, 190, 0, 200));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(p.x - 25, p.y - 25, 50, 50);
                }

                // Código corto dentro del nodo, en negrita.
                g2.setFont(FUENTE_NODO);
                FontMetrics fm = g2.getFontMetrics();

                int textoX = p.x - fm.stringWidth(vertice) / 2;
                int textoY = p.y + fm.getAscent() / 2 - 2;

                g2.setColor(new Color(255, 255, 255, 200));
                g2.drawString(vertice, textoX + 1, textoY + 1);

                g2.setColor(colorTextoContraste(relleno));
                g2.drawString(vertice, textoX, textoY);

                // Nombre completo debajo del nodo, si difiere del código.
                String nombre = RedComunicacion.nombres.get(vertice);

                if (nombre != null && !nombre.isEmpty()
                        && !nombre.equals(vertice)) {

                    g2.setFont(FUENTE_NOMBRE);
                    FontMetrics fmN = g2.getFontMetrics();

                    int ancho = fmN.stringWidth(nombre);
                    int nombreX = p.x - ancho / 2;
                    int nombreY = p.y + 22 + fmN.getAscent();

                    g2.setColor(new Color(255, 255, 255, 220));
                    g2.fillRoundRect(
                        nombreX - 4,
                        nombreY - fmN.getAscent(),
                        ancho + 8,
                        fmN.getHeight(),
                        6,
                        6
                    );

                    g2.setColor(new Color(35, 35, 35));
                    g2.drawString(nombre, nombreX, nombreY);
                }
            }

            g2.dispose();
        }

        private void dibujarEtiquetaPeso(
                Graphics2D g2,
                String texto,
                int centroX,
                int centroY,
                Color color) {

            g2.setFont(FUENTE_PESO);
            FontMetrics fm = g2.getFontMetrics();

            int ancho = fm.stringWidth(texto);
            int baseY = centroY - 6;
            int x = centroX + 6;

            g2.setColor(new Color(255, 255, 255, 225));
            g2.fillRoundRect(
                x - 3,
                baseY - fm.getAscent() - 1,
                ancho + 6,
                fm.getHeight() + 2,
                8,
                8
            );

            g2.setColor(color.darker());
            g2.drawString(texto, x, baseY);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Principal ventana = new Principal();
            ventana.setVisible(true);
        });
    }
}