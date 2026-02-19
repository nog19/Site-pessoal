import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TodoApp {

    static final String ARQUIVO = "tarefas.txt";
    static final String SEP = "||";
    static List<Tarefa> tarefas = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static class Tarefa {
        int id;
        String titulo;
        String categoria;
        String prazo;
        boolean concluida;

        Tarefa(int id, String titulo, String categoria, String prazo, boolean concluida) {
            this.id = id;
            this.titulo = titulo;
            this.categoria = categoria;
            this.prazo = prazo;
            this.concluida = concluida;
        }

        String toCSV() {
            return id + SEP + titulo + SEP + categoria + SEP + prazo + SEP + concluida;
        }

        static Tarefa fromCSV(String linha) {
            String[] p = linha.split("\\|\\|", -1);
            return new Tarefa(Integer.parseInt(p[0]), p[1], p[2], p[3], Boolean.parseBoolean(p[4]));
        }

        @Override
        public String toString() {
            String status = concluida ? "✔" : "○";
            String prazoPart = prazo.isEmpty() ? "" : "  📅 " + prazo;
            String catPart = categoria.isEmpty() ? "" : "  [" + categoria + "]";
            return String.format("  %s #%d - %s%s%s", status, id, titulo, catPart, prazoPart);
        }
    }
// Funções de persistência
    static void salvar() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO))) {
            for (Tarefa t : tarefas) pw.println(t.toCSV());
        }
    }

    static void carregar() {
        File f = new File(ARQUIVO);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (!linha.isBlank()) tarefas.add(Tarefa.fromCSV(linha));
            }
        } catch (Exception e) {
            System.out.println("⚠ Não foi possível carregar tarefas salvas.");
        }
    }

    static int proximoId() {
        return tarefas.stream().mapToInt(t -> t.id).max().orElse(0) + 1;
    }

// Funções de interface
    static void cabecalho(String titulo) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.printf ("║  %-36s║%n", titulo);
        System.out.println("╚══════════════════════════════════════╝");
    }


    static String ler(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // Funções principais do menu   
    static void listar(boolean apenasAbertas) {
        List<Tarefa> lista = apenasAbertas
            ? tarefas.stream().filter(t -> !t.concluida).collect(java.util.stream.Collectors.toList())
            : tarefas;

        if (lista.isEmpty()) {
            System.out.println("  (nenhuma tarefa encontrada)");
            return;
        }

        // Agrupa por categoria
        Map<String, List<Tarefa>> grupos = new LinkedHashMap<>();
        for (Tarefa t : lista) {
            grupos.computeIfAbsent(t.categoria.isEmpty() ? "Sem categoria" : t.categoria,
                                   k -> new ArrayList<>()).add(t);
        }

        for (Map.Entry<String, List<Tarefa>> e : grupos.entrySet()) {
            System.out.println("\n  ▸ " + e.getKey());
            e.getValue().forEach(System.out::println);
        }
    }
}