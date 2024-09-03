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
        ServerSocket welcomeSocket = null;
        ExecutorService executor = Executors.newFixedThreadPool(3);
        try {
            welcomeSocket = new ServerSocket(8181);
            System.out.println("TCP server para o jogo 'Zerinho ou Um' rodando!");

            while (true) {
                System.out.println("Aguardando três jogadores...");

                Socket player1Socket = welcomeSocket.accept();
                System.out.println("Jogador 1 conectado!");
                Socket player2Socket = welcomeSocket.accept();
                System.out.println("Jogador 2 conectado!");
                Socket player3Socket = welcomeSocket.accept();
                System.out.println("Jogador 3 conectado!");

                sendPlayerInfo(player1Socket, 1);
                sendPlayerInfo(player2Socket, 2);
                sendPlayerInfo(player3Socket, 3);

                Future<String> choicePlayer1 = executor.submit(() -> readChoice(player1Socket));
                Future<String> choicePlayer2 = executor.submit(() -> readChoice(player2Socket));
                Future<String> choicePlayer3 = executor.submit(() -> readChoice(player3Socket));

                String choice1 = choicePlayer1.get();
                String choice2 = choicePlayer2.get();
                String choice3 = choicePlayer3.get();

                int winner = determineWinner(choice1, choice2, choice3);

                sendResult(player1Socket, winner == 1);
                sendResult(player2Socket, winner == 2);
                sendResult(player3Socket, winner == 3);

                player1Socket.close();
                player2Socket.close();
                player3Socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (welcomeSocket != null && !welcomeSocket.isClosed()) {
                try {
                    welcomeSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
        }
    }

    private static void sendPlayerInfo(Socket playerSocket, int playerNumber) {
        try {
            DataOutputStream writer = new DataOutputStream(playerSocket.getOutputStream());
            writer.writeBytes("Voce e o Jogador " + playerNumber + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readChoice(Socket playerSocket) throws Exception { // Modificado para lançar exceção
        String choice = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            DataOutputStream writer = new DataOutputStream(playerSocket.getOutputStream());

            writer.writeBytes("Escolha 0 ou 1: \n");
            choice = reader.readLine();
            if (choice == null || (!choice.equals("0") && !choice.equals("1"))) {
                throw new Exception("Escolha invalida: " + choice); // Lança exceção se a escolha for inválida
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Propaga a exceção para ser tratada onde for necessário
        }
        return choice;
    }

    private static void sendResult(Socket playerSocket, boolean isWinner) {
        try {
            DataOutputStream writer = new DataOutputStream(playerSocket.getOutputStream());
            if (isWinner) {
                writer.writeBytes("Voce venceu!\n");
            } else {
                writer.writeBytes("Voce perdeu!\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int determineWinner(String choice1, String choice2, String choice3) {
        int sum = 0;

        try {
            sum = Integer.parseInt(choice1) + Integer.parseInt(choice2) + Integer.parseInt(choice3);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter escolhas para inteiros.");
            return -1;
        }

        if (sum == 0 || sum == 3) {
            return 0;
        } else if (sum == 1) {
            if (choice1.equals("1")) {
                return 1;
            } else if (choice2.equals("1")) {
                return 2;
            } else {
                return 3;
            }
        } else {
            if (choice1.equals("0")) {
                return 1;
            } else if (choice2.equals("0")) {
                return 2;
            } else {
                return 3;
            }
        }
    }
}
