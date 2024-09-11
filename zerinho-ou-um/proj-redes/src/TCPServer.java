import java.io.BufferedReader; 
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TCPServer {

    public static void main(String argv[]) {
        ServerSocket servidorRecepcao = null;
        ExecutorService executor = Executors.newFixedThreadPool(3);
        try {
            servidorRecepcao = new ServerSocket(8181);
            System.out.println("Servidor TCP para o jogo 'Zerinho ou Um' rodando!");

            while (true) {
                System.out.println("Aguardando três jogadores...");

                Socket socketJogador1 = servidorRecepcao.accept();
                System.out.println("Jogador 1 conectado!");
                Socket socketJogador2 = servidorRecepcao.accept();
                System.out.println("Jogador 2 conectado!");
                Socket socketJogador3 = servidorRecepcao.accept();
                System.out.println("Jogador 3 conectado!");

                enviarInfoJogador(socketJogador1, 1);
                enviarInfoJogador(socketJogador2, 2);
                enviarInfoJogador(socketJogador3, 3);

                Future<String> escolhaJogador1 = executor.submit(() -> lerEscolha(socketJogador1));
                Future<String> escolhaJogador2 = executor.submit(() -> lerEscolha(socketJogador2));
                Future<String> escolhaJogador3 = executor.submit(() -> lerEscolha(socketJogador3));

                String escolha1 = escolhaJogador1.get();
                String escolha2 = escolhaJogador2.get();
                String escolha3 = escolhaJogador3.get();

                int vencedor = determinarVencedor(escolha1, escolha2, escolha3);

                enviarResultado(socketJogador1, vencedor == 1);
                enviarResultado(socketJogador2, vencedor == 2);
                enviarResultado(socketJogador3, vencedor == 3);

                socketJogador1.close();
                socketJogador2.close();
                socketJogador3.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (servidorRecepcao != null && !servidorRecepcao.isClosed()) {
                try {
                    servidorRecepcao.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
        }
    }

    private static void enviarInfoJogador(Socket socketJogador, int numeroJogador) {
        try {
            DataOutputStream escritor = new DataOutputStream(socketJogador.getOutputStream());
            escritor.writeBytes("Você é o Jogador " + numeroJogador + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String lerEscolha(Socket socketJogador) throws Exception {
        String escolha = "";
        BufferedReader leitor = new BufferedReader(new InputStreamReader(socketJogador.getInputStream()));
        DataOutputStream escritor = new DataOutputStream(socketJogador.getOutputStream());

        while (true) {
            escritor.writeBytes("Escolha 0 ou 1: \n");
            escolha = leitor.readLine();

            if (escolha != null && (escolha.equals("0") || escolha.equals("1"))) {
                break;
            } else {
                escritor.writeBytes("Escolha inválida. Tente novamente.\n");
            }
        }

        return escolha;
    }

    private static void enviarResultado(Socket socketJogador, boolean eVencedor) {
        try {
            DataOutputStream escritor = new DataOutputStream(socketJogador.getOutputStream());
            if (eVencedor) {
                escritor.writeBytes("Você venceu!\n");
            } else {
                escritor.writeBytes("Você perdeu!\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int determinarVencedor(String escolha1, String escolha2, String escolha3) {
        int soma = 0;

        try {
            soma = Integer.parseInt(escolha1) + Integer.parseInt(escolha2) + Integer.parseInt(escolha3);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter escolhas para inteiros.");
            return -1;
        }

        if (soma == 0 || soma == 3) {
            return 0;
        } else if (soma == 1) {
            if (escolha1.equals("1")) {
                return 1;
            } else if (escolha2.equals("1")) {
                return 2;
            } else {
                return 3;
            }
        } else {
            if (escolha1.equals("0")) {
                return 1;
            } else if (escolha2.equals("0")) {
                return 2;
            } else {
                return 3;
            }
        }
    }
}
