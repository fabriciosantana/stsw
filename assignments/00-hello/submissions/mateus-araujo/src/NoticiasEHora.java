import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoticiasEHora {
    private static volatile boolean executando = true;
    private static int contadorNoticias = 0;
    private static int contadorHoras = 0;
    private static final int MAX_NOTICIAS = 10;
    private static final int MIN_HORAS = 5;

    public static void main(String[] args) {
        System.out.println("Iniciando envio de notícias e horas...\n");
        
        // Thread que envia notícias a cada 5 segundos
        Thread threadNoticias = new Thread(() -> {
            String[] noticias = {
                "Novo avanço na tecnologia de inteligência artificial",
                "Economia mundial mostra sinais de recuperação",
                "Descoberta científica promete revolucionar medicina",
                "Evento esportivo reúne milhares de espectadores",
                "Iniciativa sustentável ganha destaque internacional",
                "Pesquisa revela mudanças nos hábitos de consumo",
                "Projeto cultural une artistas de diferentes países",
                "Inovação em energias renováveis avança rapidamente",
                "Educação digital se torna prioridade global",
                "Cooperação internacional fortalece relações diplomáticas"
            };
            
            while (executando && contadorNoticias < MAX_NOTICIAS) {
                try {
                    System.out.println("NOTÍCIA " + (contadorNoticias + 1) + ": " + noticias[contadorNoticias]);
                    contadorNoticias++;
                    
                    if (contadorNoticias >= MAX_NOTICIAS) {
                        System.out.println("✅ Todas as notícias foram enviadas!");
                        verificarFinalizacao();
                        break;
                    }
                    
                    Thread.sleep(5000); // Espera 5 segundos
                } catch (InterruptedException e) {
                    System.out.println("Thread de notícias interrompida");
                    break;
                }
            }
        });

        // Thread que envia a hora a cada 10 segundos
        Thread threadHora = new Thread(() -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            while (executando && contadorHoras < MIN_HORAS) {
                try {
                    String horaAtual = LocalDateTime.now().format(formatter);
                    System.out.println(" HORA " + (contadorHoras + 1) + ": " + horaAtual);
                    contadorHoras++;
                    
                    if (contadorHoras >= MIN_HORAS) {
                        System.out.println("✅ Horas mínimas atingidas!");
                        verificarFinalizacao();
                        break;
                    }
                    
                    Thread.sleep(10000); // Espera 10 segundos
                } catch (InterruptedException e) {
                    System.out.println("Thread de hora interrompida");
                    break;
                }
            }
        });

        // Inicia as threads
        threadNoticias.start();
        threadHora.start();

        // Aguarda as threads terminarem
        try {
            threadNoticias.join(60000); // Timeout de 60 segundos
            threadHora.join(60000);     // Timeout de 60 segundos
        } catch (InterruptedException e) {
            System.out.println("Thread principal interrompida");
            executando = false;
        }

        // Garante que as threads parem
        executando = false;
        threadNoticias.interrupt();
        threadHora.interrupt();

        System.out.println("\n🎯 Programa finalizado!");
        System.out.println("📊 Resumo: " + contadorNoticias + " notícias e " + contadorHoras + " horas enviadas");
    }

    private static synchronized void verificarFinalizacao() {
        if (contadorNoticias >= MAX_NOTICIAS && contadorHoras >= MIN_HORAS) {
            executando = false;
        }
    }
}