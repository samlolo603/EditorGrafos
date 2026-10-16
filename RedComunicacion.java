import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo del grafo y algoritmos de árbol de expansión mínima (Kruskal / Prim).
 * <p>
 * Es la capa de datos que usa la interfaz gráfica {@code Principal}. Trabaja con
 * grafos no dirigidos y ponderados: cada arista se representa una sola vez y
 * (A, B) equivale a (B, A) en las búsquedas y eliminaciones.
 * <p>
 * Los vértices se guardan en un {@link List} para conservar el orden de
 * inserción y permitir el mapeo por índice (matriz de adyacencia, Dijkstra y
 * Floyd-Warshall que implementa {@code Principal}).
 */
public class RedComunicacion {

    // CLASE ARISTA
    static class Arista implements Comparable<Arista> {
        String origen, destino;
        int peso;

        Arista(String o, String d, int p) { origen = o; destino = d; peso = p; }

        @Override public int compareTo(Arista otra) { return Integer.compare(this.peso, otra.peso); }

        @Override public String toString() { return origen + " -- " + destino + " : " + peso; }
    }

    // UNION-FIND (para Kruskal)
    static class UnionFind {
        Map<String, String> padre = new HashMap<>();
        Map<String, Integer> rango = new HashMap<>();

        void agregar(String x) {
            padre.putIfAbsent(x, x);
            rango.putIfAbsent(x, 0);
        }
        String find(String x) {
            if (!padre.get(x).equals(x)) padre.put(x, find(padre.get(x)));
            return padre.get(x);
        }
        boolean union(String x, String y) {
            String rx = find(x), ry = find(y);
            if (rx.equals(ry)) return false;
            if (rango.get(rx) < rango.get(ry))      padre.put(rx, ry);
            else if (rango.get(rx) > rango.get(ry)) padre.put(ry, rx);
            else { padre.put(ry, rx); rango.put(rx, rango.get(rx) + 1); }
            return true;
        }
    }

    // ESTRUCTURAS GLOBALES
    static List<String>         vertices = new ArrayList<>();
    static Map<String, String>  nombres  = new LinkedHashMap<>();
    static List<Arista>         aristas  = new ArrayList<>();
    static boolean              hayCambiosSinGuardar = false;

    // GRAFO POR DEFECTO
    static void cargarGrafoPorDefecto() {
        nombres.put("A", "Puebla");
        nombres.put("B", "Ciudad de México");
        nombres.put("C", "Tlaxcala");
        nombres.put("D", "Veracruz");
        nombres.put("E", "Oaxaca");
        nombres.put("F", "Querétaro");
        nombres.put("G", "Hidalgo");
        nombres.put("H", "Morelos");
        vertices.addAll(nombres.keySet());

        String[] cod = {"A","B","C","D","E","F","G","H"};
        int[][] ars = {
            {0,1,7},{0,2,3},{0,4,9},{1,2,5},{1,3,6},{1,5,4},{1,6,8},
            {2,6,5},{2,7,7},{3,4,6},{3,5,5},{4,7,6},{5,6,3},{5,7,4},{6,7,5}
        };
        for (int[] a : ars) aristas.add(new Arista(cod[a[0]], cod[a[1]], a[2]));

        hayCambiosSinGuardar = true;
    }

    // OPERACIONES SOBRE EL GRAFO
    static void agregarVertice(String cod, String nombre) {
        if (vertices.contains(cod)) return;
        vertices.add(cod);
        nombres.put(cod, nombre);
        hayCambiosSinGuardar = true;
    }

    static void eliminarVertice(String cod) {
        if (!vertices.contains(cod)) return;
        vertices.remove(cod);
        nombres.remove(cod);
        aristas.removeIf(a -> a.origen.equals(cod) || a.destino.equals(cod));
        hayCambiosSinGuardar = true;
    }

    static void agregarArista(String o, String d, int peso) {
        if (!vertices.contains(o) || !vertices.contains(d)) return;
        aristas.add(new Arista(o, d, peso));
        hayCambiosSinGuardar = true;
    }

    static void eliminarArista(String o, String d) {
        boolean eliminada = aristas.removeIf(a ->
                (a.origen.equals(o) && a.destino.equals(d)) ||
                (a.origen.equals(d) && a.destino.equals(o)));
        if (eliminada) hayCambiosSinGuardar = true;
    }

    // KRUSKAL
    static List<Arista> kruskal() {
        if (vertices.isEmpty() || aristas.isEmpty()) return null;

        List<Arista> orden = new ArrayList<>(aristas);
        Collections.sort(orden);

        UnionFind uf = new UnionFind();
        for (String v : vertices) uf.agregar(v);

        List<Arista> mst = new ArrayList<>();
        for (Arista a : orden) {
            if (mst.size() == vertices.size() - 1) break;
            if (!uf.find(a.origen).equals(uf.find(a.destino))) {
                uf.union(a.origen, a.destino);
                mst.add(a);
            }
        }
        if (mst.size() != vertices.size() - 1) return null;
        return mst;
    }

    // PRIM
    static List<Arista> prim(String inicio) {
        if (vertices.isEmpty() || aristas.isEmpty()) return null;

        List<String> lista = new ArrayList<>(vertices);
        int n = lista.size();
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < n; i++) idx.put(lista.get(i), i);

        final int INF = Integer.MAX_VALUE / 2;
        int[][] mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            java.util.Arrays.fill(mat[i], INF);
            mat[i][i] = 0;
        }
        for (Arista a : aristas) {
            int u = idx.get(a.origen), v = idx.get(a.destino);
            if (a.peso < mat[u][v]) { mat[u][v] = a.peso; mat[v][u] = a.peso; }
        }

        boolean[] enMST = new boolean[n];
        int[] clave = new int[n], padre = new int[n];
        java.util.Arrays.fill(clave, INF);
        java.util.Arrays.fill(padre, -1);

        int s = idx.get(inicio);
        clave[s] = 0;

        List<Arista> mst = new ArrayList<>();

        for (int iter = 0; iter < n; iter++) {
            int u = -1;
            for (int v = 0; v < n; v++)
                if (!enMST[v] && (u == -1 || clave[v] < clave[u])) u = v;
            if (u == -1 || clave[u] == INF) break;
            enMST[u] = true;

            if (padre[u] != -1) {
                mst.add(new Arista(lista.get(padre[u]), lista.get(u), clave[u]));
            }

            for (int v = 0; v < n; v++) {
                if (!enMST[v] && mat[u][v] < clave[v]) { clave[v] = mat[u][v]; padre[v] = u; }
            }
        }

        if (mst.size() != n - 1) return null;
        return mst;
    }

    // UTILIDADES
    static int sumaPesos(List<Arista> lista) {
        int s = 0;
        for (Arista a : lista) s += a.peso;
        return s;
    }

    // PERSISTENCIA DE GRAFOS (carpeta ./grafos)
    static final String CARPETA = "grafos";
    static final String ARCHIVO_ULTIMO = ".ultimo";

    static File carpetaGrafos() {
        File dir = new File(CARPETA);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    // Formato de cada línea del archivo:
    //   V,codigo,nombre        (vértice)
    //   E,origen,destino,peso  (arista)
    static void guardarGrafo(String nombre) throws IOException {
        File f = new File(carpetaGrafos(), nombre + ".txt");

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(f), StandardCharsets.UTF_8))) {
            bw.write("# Editor de Grafos - V,codigo,nombre | E,origen,destino,peso");
            bw.newLine();
            for (String v : vertices) {
                bw.write("V," + v + "," + nombres.getOrDefault(v, v));
                bw.newLine();
            }
            for (Arista a : aristas) {
                bw.write("E," + a.origen + "," + a.destino + "," + a.peso);
                bw.newLine();
            }
        }
        hayCambiosSinGuardar = false;
    }

    static void cargarGrafo(String nombre) throws IOException {
        File f = new File(carpetaGrafos(), nombre + ".txt");
        if (!f.exists()) {
            throw new IOException("No existe el grafo '" + nombre + "'.");
        }

        List<String> nv = new ArrayList<>();
        Map<String, String> nn = new LinkedHashMap<>();
        List<Arista> na = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(f), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] p = linea.split(",");
                if (p.length >= 3 && p[0].equalsIgnoreCase("V")) {
                    String cod = p[1].trim().toUpperCase();
                    if (!cod.isEmpty() && !nv.contains(cod)) {
                        nv.add(cod);
                        nn.put(cod, p[2].trim());
                    }
                } else if (p.length >= 4 && p[0].equalsIgnoreCase("E")) {
                    try {
                        int w = Integer.parseInt(p[3].trim());
                        na.add(new Arista(
                            p[1].trim().toUpperCase(),
                            p[2].trim().toUpperCase(),
                            w));
                    } catch (NumberFormatException ignored) {
                        // Línea de arista mal formada: se ignora.
                    }
                }
            }
        }

        vertices.clear(); vertices.addAll(nv);
        nombres.clear();  nombres.putAll(nn);
        aristas.clear();  aristas.addAll(na);
        hayCambiosSinGuardar = false;
    }

    // Nombres de los grafos guardados (sin extensión), ordenados.
    static List<String> listarGrafos() {
        List<String> lista = new ArrayList<>();
        File[] archivos = carpetaGrafos().listFiles(
                (d, n) -> n.toLowerCase().endsWith(".txt"));
        if (archivos != null) {
            Arrays.sort(archivos,
                Comparator.comparing(File::getName,
                    String.CASE_INSENSITIVE_ORDER));
            for (File f : archivos) {
                String n = f.getName();
                lista.add(n.substring(0, n.length() - 4));
            }
        }
        return lista;
    }

    // Nombre del último grafo guardado/abierto, o null.
    static String leerUltimo() {
        File f = new File(carpetaGrafos(), ARCHIVO_ULTIMO);
        if (!f.exists()) return null;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(f), StandardCharsets.UTF_8))) {
            String linea = br.readLine();
            if (linea == null) return null;
            linea = linea.trim();
            return linea.isEmpty() ? null : linea;
        } catch (IOException e) {
            return null;
        }
    }

    static void escribirUltimo(String nombre) {
        File f = new File(carpetaGrafos(), ARCHIVO_ULTIMO);
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(f), StandardCharsets.UTF_8))) {
            if (nombre != null) bw.write(nombre);
        } catch (IOException ignored) {
            // Guardar la referencia del último grafo es best-effort.
        }
    }
}
