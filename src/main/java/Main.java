import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args){
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.err.println("Logs from your program will appear here!");

        // Uncomment this block to pass the first stage
        //
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 9092;
        try {
            serverSocket = new ServerSocket(port);
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);
            // Wait for connection from client.
            clientSocket = serverSocket.accept();
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

            /*
            Request Header v2 => request_api_key request_api_version correlation_id client_id TAG_BUFFER
              request_api_key => INT16
              request_api_version => INT16
              correlation_id => INT32
              client_id => NULLABLE_STRING - For non-null strings, first the length N is given as an INT16.
                Then N bytes follow which are the UTF-8 encoding of the character sequence. A null value is
                encoded with length of -1

            */
            int messageSize = dis.readInt();
            short apiKey = dis.readShort();
            short requestApiVersion = dis.readShort();
            int correlationId = dis.readInt();
            short clientIdLength = dis.readShort();
            String clientId = null;
            if (clientIdLength >= 0) {
                clientId = new String(dis.readNBytes(clientIdLength));
            }
            OutputStream out = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            short errorCode = 0;

            switch (apiKey) {
                case 18: // ApiVersions
                    System.out.println("asdf");
                    if (requestApiVersion >=0 && requestApiVersion <= 4) {
                        /* ApiVersions Response (Version: 4) => error_code [api_keys] throttle_time_ms TAG_BUFFER
                          error_code => INT16
                          api_keys => api_key min_version max_version TAG_BUFFER
                            api_key => INT16
                            min_version => INT16
                            max_version => INT16
                          throttle_time_ms => INT32
                          */
                    }
                default:
                    //unsupported
                    errorCode = ApiErrors.UNSUPPORTED_VERSION.getErrorCode();
            }

            dos.writeInt(0); // message_size
            dos.writeInt(correlationId); // correlation_id
            dos.writeShort(errorCode);
            dos.flush();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
