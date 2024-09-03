import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClient {

    public static void main(String argv[]) throws Exception {

        BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = new Socket("localhost", 8181);
        System.out.println("Cliente conectado ao servidor!");

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String playerInfo = inFromServer.readLine();
        System.out.println(playerInfo);

        String serverMessage = inFromServer.readLine();
        System.out.println(serverMessage);

        String choice = keyboardReader.readLine();

        while (!choice.equals("0") && !choice.equals("1")) {
            System.out.println("Entrada inválida! Escolha 0 ou 1:");
            choice = keyboardReader.readLine();
        }

        outToServer.writeBytes(choice + '\n');

        String result = inFromServer.readLine();
        System.out.println(result);

        clientSocket.close();
    }
}


