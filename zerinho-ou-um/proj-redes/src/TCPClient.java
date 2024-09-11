import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClient {

    public static void main(String argv[]) throws Exception {

        BufferedReader leitorTeclado = new BufferedReader(new InputStreamReader(System.in));

        Socket socketCliente = new Socket("localhost", 8181);
        System.out.println("Cliente conectado ao servidor!");

        DataOutputStream envioParaServidor = new DataOutputStream(socketCliente.getOutputStream());
        BufferedReader recebimentoDoServidor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

        String infoJogador = recebimentoDoServidor.readLine();
        System.out.println(infoJogador);

        String mensagemServidor = recebimentoDoServidor.readLine();
        System.out.println(mensagemServidor);

        String escolha = leitorTeclado.readLine();

        while (!escolha.equals("0") && !escolha.equals("1")) {
            System.out.println("Entrada inválida! Escolha 0 ou 1:");
            escolha = leitorTeclado.readLine();
        }

        envioParaServidor.writeBytes(escolha + '\n');

        String resultado = recebimentoDoServidor.readLine();
        System.out.println(resultado);

        socketCliente.close();
    }
}

