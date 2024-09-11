import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {

    public static void main(String[] args) {
        ServerSocket servidorSocket = null;
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            servidorSocket = new ServerSocket(8181);
            System.out.println("Servidor TCP para o jogo 'Zerinho ou Um' rodando!");

            while (true) {
                System.out.println("Aguardando três jogadores...");

                Socket jogador1Socket = servidorSocket.accept();
                System.out.println("Jogador 1 conectado!");
                Socket jogador2Socket = servidorSocket.accept();
                System.out.println("Jogador 2 conectado!");
                Socket jogador3Socket = servidorSocket.accept();
                System.out.println("Jogador 3 conectado!");

                enviarInfoJogador(jogador1Socket, 1);
                enviarInfoJogador(jogador2Socket, 2);
                enviarInfoJogador(jogador3Socket, 3);

                String escolha1 = lerEscolha(jogador1Socket);
                String escolha2 = lerEscolha(jogador2Socket);
                String escolha3 = lerEscolha(jogador3Socket);

                int vencedor = determinarVencedor(escolha1, escolha2, escolha3);

                enviarResultado(jogador1Socket, vencedor == 1);
                enviarResultado(jogador2Socket, vencedor == 2);
                enviarResultado(jogador3Socket, vencedor == 3);

                jogador1Socket.close();
                jogador2Socket.close();
                jogador3Socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (servidorSocket != null && !servidorSocket.isClosed()) {
                try {
                    servidorSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
        }
    }

    private static void enviarInfoJogador(Socket jogadorSocket, int numeroJogador) {
        try {
            DataOutputStream escritor = new DataOutputStream(jogadorSocket.getOutputStream());
            escritor.writeBytes("Voce e o Jogador " + numeroJogador + "\n");
            escritor.writeBytes("Escolha 0 ou 1: \n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String lerEscolha(Socket jogadorSocket) throws Exception {
        BufferedReader leitor = new BufferedReader(new InputStreamReader(jogadorSocket.getInputStream()));
        String escolha = leitor.readLine().trim();
        return escolha;
    }

    private static void enviarResultado(Socket jogadorSocket, boolean vencedor) {
        try {
            DataOutputStream escritor = new DataOutputStream(jogadorSocket.getOutputStream());
            if (vencedor) {
                escritor.writeBytes("Voce venceu!\n");
            } else {
                escritor.writeBytes("Voce perdeu!\n");
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
