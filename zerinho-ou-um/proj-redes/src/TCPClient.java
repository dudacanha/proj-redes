import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClient {

    public static void main(String[] args) {

        try (Socket socketCliente = new Socket("localhost", 8181);
             BufferedReader leitorTeclado = new BufferedReader(new InputStreamReader(System.in));
             DataOutputStream envioParaServidor = new DataOutputStream(socketCliente.getOutputStream());
             BufferedReader recebimentoDoServidor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()))) {

            System.out.println("Cliente conectado ao servidor!");

            String infoJogador = recebimentoDoServidor.readLine();
            System.out.println(infoJogador);

            String mensagemServidor = recebimentoDoServidor.readLine();
            System.out.println(mensagemServidor);

            String escolha = leitorTeclado.readLine().trim();

            while (!escolha.equals("0") && !escolha.equals("1")) {
                System.out.println("Entrada inválida! Escolha 0 ou 1:");
                escolha = leitorTeclado.readLine().trim();
            }

            envioParaServidor.writeBytes(escolha + '\n');

            String resultado = recebimentoDoServidor.readLine();
            System.out.println(resultado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
